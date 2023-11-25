package com.dbms.project.repository;

import com.dbms.project.db.MongoUtils;
import com.dbms.project.db.TableUtils;
import com.dbms.project.exceptions.DBMSException;
import com.dbms.project.model.Index;
import com.dbms.project.model.Operation;
import com.dbms.project.model.PrimaryKey;
import com.dbms.project.model.Table;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.json.simple.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unchecked")
@Repository
public class TableRepository {
    private final static Logger logger = LoggerFactory.getLogger(TableRepository.class);
    private final MongoUtils mongoUtils;

    public TableRepository(MongoUtils mongoUtils) {
        this.mongoUtils = mongoUtils;
    }

    public boolean createTable(String databaseName, Table table) throws Exception {
        logger.info("Create table with name: {} for database: {}", table.getTableName(), databaseName);
        TableUtils.createTable(databaseName, table);
        MongoTemplate mongoTemplate = mongoUtils.mongoTemplate(databaseName);
        mongoTemplate.createCollection(table.getTableName());

        for (var uniqueKey : table.getUniqueKeys()) {
            var uniqueKeyIndexName = String.format("UK_%s_%s", table.getTableName(), uniqueKey);
            mongoTemplate.createCollection(uniqueKeyIndexName);
        }

        for (var foreignKey : table.getForeignKeys()) {
            var referencedAttributes = String.join("-", foreignKey.getReferencedAttributes());
            var foreignKeyIndexName = String.format("FK_%s_%s", table.getTableName(), referencedAttributes);
            mongoTemplate.createCollection(foreignKeyIndexName);
        }

        return true;
    }

    public boolean deleteTable(String databaseName, String tableName) throws Exception{
        logger.info("Delete table: {} from database: {}", tableName, databaseName);
        TableUtils.deleteTable(databaseName, tableName);
        MongoTemplate mongoTemplate = mongoUtils.mongoTemplate(databaseName);
        mongoTemplate.dropCollection(tableName);
        return true;
    }

    public boolean deleteIndexTables(String databaseName, String tableName) throws Exception{
        logger.info("Delete table: {} from database: {}", tableName, databaseName);
        MongoTemplate mongoTemplate = mongoUtils.mongoTemplate(databaseName);
        mongoTemplate.dropCollection(tableName);
        return true;
    }

    public boolean createIndex(String databaseName, String tableName, Index index) throws Exception {
        logger.info("Create index: {} for table: {} from database: {}", index.getIndexName(), tableName, databaseName);
        TableUtils.createIndex(databaseName, tableName, index);
        var indexName = String.format("%s_%s_%s", index.getIsUnique() == 1 ? "UK" : "NUK", tableName, index.getIndexName());

        MongoTemplate mongoTemplate = mongoUtils.mongoTemplate(databaseName);
        mongoTemplate.createCollection(indexName);
        return true;
    }

    public JSONArray getAllTables(String databaseName) throws Exception {
        logger.info("Get all tables called.");
        return TableUtils.getAllTablesForDatabase(databaseName);
    }

    public void insertRow(String databaseName, String tableName, PrimaryKey key, String row) throws Exception{
        MongoTemplate mongoTemplate = mongoUtils.mongoTemplate(databaseName);
        if (mongoTemplate.getCollection(tableName).find(Filters.eq("_id", key.getPk())).first() != null) {
            throw new Exception("Duplicate primary key");
        }
        Document document = new Document("_id", key.getPk())
                .append("value", row);
        mongoTemplate.getCollection(tableName).insertOne(document);
    }

    public void deleteRow(String databaseName, String tableName, String rowId) {
        try {
            MongoTemplate mongoTemplate = mongoUtils.mongoTemplate(databaseName);
            mongoTemplate.getCollection(tableName).deleteOne(Filters.eq("_id", rowId));
        } catch (Exception e) {
            throw new DBMSException("400", "Invalid row id");
        }
    }

    public void insertRowInIndex(String databaseName, String indexName, PrimaryKey indexPrimaryKey, String pk, Boolean isUnique) throws Exception {
        MongoTemplate mongoTemplate = mongoUtils.mongoTemplate(databaseName);
        var index = mongoTemplate.getCollection(indexName).find(Filters.eq("_id", indexPrimaryKey.getPk())).first();

        if (index == null) {
            Document document = new Document("_id", indexPrimaryKey.getPk())
                    .append("value", pk);
            mongoTemplate.getCollection(indexName).insertOne(document);
        } else {
            if (isUnique) {
                throw new DBMSException("400", "Violating Unique Key Constraint");
            }
            var newValue = index.get("value") + ";" + pk;
            mongoTemplate.getCollection(indexName).updateOne(Filters.eq("_id", indexPrimaryKey.getPk()), new Document("$set", new Document("value", newValue)));
        }
    }

    public Map<String, String> getRow(String databaseName, Table table, String rowId) {
        try {
            MongoTemplate mongoTemplate = mongoUtils.mongoTemplate(databaseName);
            var record = mongoTemplate.getCollection(table.getTableName()).find(Filters.eq("_id", rowId)).first();

            Map<String, String> values = new HashMap<>();
            String[] id = ((String) record.get("_id")).split(",");
            String[] value = ((String) record.get("value")).split(";");
            AtomicInteger index = new AtomicInteger();
            AtomicInteger valueIndex = new AtomicInteger();

            table.getPrimaryKey().getPkAttributes().forEach((attr) -> {
                values.put(attr, id[index.getAndIncrement()]);
            });
            table.getAttributes().forEach((attr) -> {
                if (!attr.getIsPrimaryKey()) {
                    values.put(attr.getAttributeName(), value[valueIndex.getAndIncrement()]);
                }
            });
            return values;
        } catch (Exception e) {
            throw new DBMSException("400", "Invalid row id");
        }
    }

    public List<Map<String, String>> getAllRows(String databaseName, Table table) throws Exception{
        MongoTemplate mongoTemplate = mongoUtils.mongoTemplate(databaseName);
        FindIterable<Document> documents = mongoTemplate.getCollection(table.getTableName()).find();

        List<Map<String, String>> records = new ArrayList<>();
        MongoCursor<Document> cursor = documents.iterator();
        while (cursor.hasNext()) {
            Map<String, String> rowValues = new HashMap<>();
            List<Object> values = new ArrayList<>(cursor.next().values());
            int i = 0;

            String[] idValues = ((String) values.get(0)).split(",");
            String attributesValues = "";
            int j = 0;
            while (j < idValues.length) {
                attributesValues += idValues[j] + ";";
                j++;
            }

            attributesValues += ((String) values.get(1));
            String[] attributes = attributesValues.split(";");
            while (i < table.getAttributes().size()) {
                rowValues.put(table.getAttributes().get(i).getAttributeName(), attributes[i]);
                i ++;
            }

            records.add(rowValues);
        }

        return records;
    }

    public void deleteRowForIndex(String databaseName, String indexName, String indexPk, String deletedPK, boolean isUnique) {
        try {
            if (isUnique) {
                deleteRow(databaseName, indexName, indexPk);
            } else {
                MongoTemplate mongoTemplate = mongoUtils.mongoTemplate(databaseName);
                var index = mongoTemplate.getCollection(indexName).find(Filters.eq("_id", indexPk)).first();
                if (index == null) {
                    throw new DBMSException("400", "Invalid foreign key value.");
                }
                var previousValues = ((String) index.get("value")).split(";");
                StringBuilder newValue = new StringBuilder();
                for (int i = 0; i < previousValues.length; i++) {
                    if (previousValues[i].equals(deletedPK)) {
                        continue;
                    }
                    if (previousValues.length > 2 && i != previousValues.length - 1) {
                        newValue.append(previousValues[i]).append(";");
                    } else {
                        newValue.append(previousValues[i]);
                    }
                }
                if (newValue.isEmpty()) {
                    deleteRow(databaseName, indexName, indexPk);
                    return;
                }

                mongoTemplate.getCollection(indexName).updateOne(Filters.eq("_id", indexPk), new Document("$set", new Document("value", newValue.toString())));
            }
        } catch (Exception e) {
            throw new DBMSException("400", "Invalid row id");
        }
    }



    public void checkIfForeignKeyExists(String databaseName, String tableName, String rowId) {
        try {
            MongoTemplate mongoTemplate = mongoUtils.mongoTemplate(databaseName);
            var index = mongoTemplate.getCollection(tableName).find(Filters.eq("_id", rowId)).first();
            if (index == null) {
                throw new DBMSException("400", "Invalid foreign key value.");
            }
        } catch (Exception e) {
            throw new DBMSException("400", "Violated foreign key constraint.");
        }
    }

    public void checkUniqueKey(String databaseName, String uniqueKeyIndexName, String key) {
        try {
            MongoTemplate mongoTemplate = mongoUtils.mongoTemplate(databaseName);
            var index = mongoTemplate.getCollection(uniqueKeyIndexName).find(Filters.eq("_id", key)).first();
            if (index != null) {
                throw new DBMSException("400", "Violated unique key constraint.");
            }
        } catch (Exception e) {
            throw new DBMSException("400", "Violated unique key constraint.");
        }
    }

    public void checkUniqueIndex(String databaseName, String indexName, String pk) {
        try {
            MongoTemplate mongoTemplate = mongoUtils.mongoTemplate(databaseName);
            var index = mongoTemplate.getCollection(indexName).find(Filters.eq("_id", pk)).first();
            if (index != null) {
                throw new DBMSException("400", "Violated unique index constraint.");
            }
        } catch (Exception e) {
            throw new DBMSException("400", "Violated unique index constraint.");
        }
    }

    public void checkIfForeignKeyReferenced(String databaseName, String tableName, String rowId) {
        try {
            MongoTemplate mongoTemplate = mongoUtils.mongoTemplate(databaseName);
            var index = mongoTemplate.getCollection(tableName).find(Filters.eq("_id", rowId)).first();
            if (index != null) {
                throw new DBMSException("400", "Cannot delete. Row is referenced by another table.");
            }
        } catch (Exception e) {
            throw new DBMSException("400", "Cannot delete. Row is referenced by another table.");
        }
    }

    public List<Map<String, String>> getAllRecordsWithCondition(
            String databaseName, Table parentTable, String indexName, String indexKey, Operation operation
    ) throws Exception {

        MongoTemplate mongoTemplate = mongoUtils.mongoTemplate(databaseName);
        FindIterable<Document> documents = getMongoDocuments(mongoTemplate, indexName, indexKey, operation);
        if (documents == null) {
            throw new DBMSException("400", "Invalid operation");
        }

        List<Map<String, String>> records = new ArrayList<>();
        MongoCursor<Document> cursor = documents.iterator();
        while (cursor.hasNext()) {
            Map<String, String> rowValues = new HashMap<>();
            List<Object> values = new ArrayList<>(cursor.next().values());
            int i = 0;

            String attributesValues = ((String) values.get(1));
            String[] attributes = attributesValues.split(";");
            while (i < attributes.length) {
                rowValues = getRow(databaseName, parentTable, attributes[i]);
                i++;
                records.add(rowValues);
            }
        }

        return records;
    }

    private FindIterable<Document> getMongoDocuments(MongoTemplate mongoTemplate, String indexName, String indexKey, Operation operation) {
        if (operation.equals(Operation.EQUAL)) {
            return mongoTemplate.getCollection(indexName).find(Filters.eq("_id", indexKey));
        } else if (operation.equals(Operation.LESS_THAN)) {
            return mongoTemplate.getCollection(indexName).find(Filters.lt("_id", indexKey));
        } else if (operation.equals(Operation.GREATER_THAN)) {
            return mongoTemplate.getCollection(indexName).find(Filters.gt("_id", indexKey));
        } else if (operation.equals(Operation.LIKE)) {
            return mongoTemplate.getCollection(indexName).find(Filters.regex("_id", "^" + indexKey + "$"));
        }
        return null;
    }
}
