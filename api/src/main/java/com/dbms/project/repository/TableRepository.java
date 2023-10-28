package com.dbms.project.repository;

import com.dbms.project.db.MongoUtils;
import com.dbms.project.db.TableUtils;
import com.dbms.project.model.Index;
import com.dbms.project.model.PrimaryKey;
import com.dbms.project.model.Table;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.json.simple.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

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
        return true;
    }

    public boolean deleteTable(String databaseName, String tableName) throws Exception{
        logger.info("Delete table: {} from database: {}", tableName, databaseName);
        TableUtils.deleteTable(databaseName, tableName);
        MongoTemplate mongoTemplate = mongoUtils.mongoTemplate(databaseName);
        mongoTemplate.dropCollection(tableName);
        return true;
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

    public void insertRow(String databaseName, String tableName, PrimaryKey key, String row) throws Exception{
        MongoTemplate mongoTemplate = mongoUtils.mongoTemplate(databaseName);
        Document document = new Document("_id", key.getPk())
                .append("value", row);
        mongoTemplate.getCollection(tableName).insertOne(document);
    }

    public void deleteRow(String databaseName, String tableName, String rowId) throws Exception{
        MongoTemplate mongoTemplate = mongoUtils.mongoTemplate(databaseName);
        mongoTemplate.getCollection(tableName).deleteOne(Filters.eq("_id", rowId));

    }
}
