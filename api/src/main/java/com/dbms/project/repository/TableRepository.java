package com.dbms.project.repository;

import com.dbms.project.db.MongoUtils;
import com.dbms.project.db.TableUtils;
import com.dbms.project.exceptions.DBMSException;
import com.dbms.project.model.Index;
import com.dbms.project.model.PrimaryKey;
import com.dbms.project.model.Table;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        table.getUniqueKeys().forEach(uniqueKey -> {
            mongoTemplate.createCollection(table.getTableName() + "_" + uniqueKey);
        });
        return true;
    }

    public boolean deleteTable(String databaseName, String tableName) throws Exception{
        logger.info("Delete table: {} from database: {}", tableName, databaseName);
        deleteUniqueAttributeTables(databaseName, tableName);
        TableUtils.deleteTable(databaseName, tableName);
        MongoTemplate mongoTemplate = mongoUtils.mongoTemplate(databaseName);
        mongoTemplate.dropCollection(tableName);
        return true;
    }

    public void deleteUniqueAttributeTables(String databaseName, String tableName) throws Exception {
        JSONObject tableJson = TableUtils.getTableFromDatabase(databaseName, tableName);
        Table table = Table.fromJSON(tableJson);
        MongoTemplate mongoTemplate = mongoUtils.mongoTemplate(databaseName);
        table.getUniqueKeys().stream().map(attr -> tableName + "_" + attr).forEach(mongoTemplate::dropCollection);
    }

    public boolean createIndex(String databaseName, String tableName, Index index) throws Exception {
        logger.info("Create index: {} for table: {} from database: {}", index.getIndexName(), tableName, databaseName);
        TableUtils.createIndex(databaseName, tableName, index);
        MongoTemplate mongoTemplate = mongoUtils.mongoTemplate(databaseName);
//        mongoTemplate.getCollection(tableName).createIndex();
        return true;
    }

    public JSONArray getAllTables(String databaseName) throws Exception {
        logger.info("Get all tables called.");
        return TableUtils.getAllTablesForDatabase(databaseName);
    }

    public List<?> getAllRows(String databaseName, Table table) throws Exception{
        MongoTemplate mongoTemplate = mongoUtils.mongoTemplate(databaseName);
        FindIterable<Document> documents = mongoTemplate.getCollection(table.getTableName()).find();

        List<Map<String, String>> records = new ArrayList<>();
        MongoCursor<Document> cursor = documents.iterator();
        while (cursor.hasNext()) {
            Map<String, String> rowValues = new HashMap<>();
            List<Object> values = new ArrayList<>(cursor.next().values());
            int i = 0;

            String attributesValues = ((String) values.get(0) + ";") + ((String) values.get(1));
            String[] attributes = attributesValues.split(";");
            while (i < table.getAttributes().size()) {
                rowValues.put(table.getAttributes().get(i).getAttributeName(), attributes[i]);
                i ++;
            }

            records.add(rowValues);
        }

        return records;
    }

    public void insertUniqueAttribute(String databaseName, String tableName, List<Pair<String, String>> uniqueAttributes, PrimaryKey key) throws Exception {
        MongoTemplate mongoTemplate = mongoUtils.mongoTemplate(databaseName);

        uniqueAttributes.forEach(uniqueAttr -> {
            var tableUniqueName = tableName + "_" + uniqueAttr.getFirst();
            Bson filter = Filters.eq("_id", uniqueAttr.getSecond());
            FindIterable<Document> documents = mongoTemplate.getCollection(tableUniqueName).find(filter);
            MongoCursor<Document> cursor = documents.iterator();
            if (cursor.hasNext()) {
                throw new DBMSException("Attribute " + uniqueAttr.getFirst() + " is unique and value " + uniqueAttr.getSecond() + " already exists in table " + tableName + " from database " + databaseName);
            }
            Document document = new Document("_id", uniqueAttr.getSecond())
                    .append("value", key.getPk());
            mongoTemplate.getCollection(tableUniqueName).insertOne(document);
        });
    }

    public void insertRow(String databaseName, String tableName, PrimaryKey key, String row, List<Pair<String, String>> uniqueAttributes) throws Exception{
        MongoTemplate mongoTemplate = mongoUtils.mongoTemplate(databaseName);
        Bson filter = Filters.eq("_id", key.getPk());
        FindIterable<Document> documents = mongoTemplate.getCollection(tableName).find(filter);
        MongoCursor<Document> cursor = documents.iterator();
        if (cursor.hasNext()) {
            throw new DBMSException("Id already exists in table " + tableName + " from database " + databaseName);
        }

        insertUniqueAttribute(databaseName, tableName, uniqueAttributes, key);

        Document document = new Document("_id", key.getPk())
                .append("value", row);
        mongoTemplate.getCollection(tableName).insertOne(document);
    }

    public void deleteRow(String databaseName, String tableName, String rowId) throws Exception{
        MongoTemplate mongoTemplate = mongoUtils.mongoTemplate(databaseName);
        mongoTemplate.getCollection(tableName).deleteOne(Filters.eq("_id", rowId));
    }
}
