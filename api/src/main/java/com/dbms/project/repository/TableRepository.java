package com.dbms.project.repository;

import com.dbms.project.db.TableUtils;
import com.dbms.project.model.Database;
import com.dbms.project.model.Index;
import com.dbms.project.model.Table;
import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

@SuppressWarnings("unchecked")
@Repository
public class TableRepository {
    private final static Logger logger = LoggerFactory.getLogger(TableRepository.class);
    private final static String CATALOG_PATH = "catalog.json";

    public boolean createTable(String databaseName, Table table) throws Exception {
        logger.info("Create table with name: {} for database: {}", table.getTableName(), databaseName);
        TableUtils.createTable(databaseName, table);
        return true;
    }

    public boolean deleteTable(String databaseName, String tableName) throws Exception{
        logger.info("Delete table: {} from database: {}", tableName, databaseName);
        TableUtils.deleteTable(tableName, databaseName);
        return true;
    }

    public boolean createIndex(String databaseName, String tableName, Index index) throws Exception {
        logger.info("Create index: {} for table: {} from database: {}", index.getIndexName(), tableName, databaseName);
        TableUtils.createIndex(databaseName, tableName, index);
        return true;
    }

    public JSONArray getAllTables(String databaseName) throws Exception {
        logger.info("Get all tables called.");
        return TableUtils.getAllTablesForDatabase(databaseName);
    }
}
