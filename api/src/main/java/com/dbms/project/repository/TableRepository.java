package com.dbms.project.repository;

import com.dbms.project.model.Database;
import com.dbms.project.model.Index;
import com.dbms.project.model.Table;
import org.json.simple.JSONArray;
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
        JSONParser parser = new JSONParser();
        JSONObject databases = (JSONObject) parser.parse(new FileReader(CATALOG_PATH));
        JSONArray databasesArray = (JSONArray) databases.get("databases");

        JSONObject db = (JSONObject)  databasesArray.stream().filter((database) -> ((JSONObject) database).get("databaseName").equals(databaseName))
                .findFirst().orElse(null);
        if (db == null) {
            logger.error("Invalid database name.");
            return false;
        }
        JSONArray databaseTables = (JSONArray) db.get("tables");
        Object tbl = databaseTables.stream().filter((t) -> ((JSONObject) t).get("tableName").equals(table.getTableName()))
                .findFirst().orElse(null);
        if (tbl != null) {
            logger.error("Invalid table name.");
            return false;
        }

        databaseTables.add(table.toJSON());
        db.put("tables", databaseTables);
        for (int i = 0; i < databasesArray.size(); i++) {
            if (((JSONObject) databasesArray.get(i)).get("name") == databaseName) {
                databasesArray.remove(databasesArray.get(i));
                databasesArray.add(i, db);
            }
        }
        databases.put("databases", databasesArray);


        try (FileWriter fileWriter = new FileWriter("catalog.json")) {
            fileWriter.write(databases.toJSONString());
            fileWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean deleteTable(String databaseName, String tableName) throws Exception{
        logger.info("Delete table: {} from database: {}", tableName, databaseName);
        JSONParser parser = new JSONParser();
        JSONObject databases = (JSONObject) parser.parse(new FileReader(CATALOG_PATH));
        JSONArray databasesArray = (JSONArray) databases.get("databases");

        JSONObject db = (JSONObject) databasesArray.stream().filter((database) -> ((JSONObject) database).get("databaseName").equals(databaseName))
                .findFirst().orElse(null);
        if (db == null) {
            logger.error("Database: {} does not exist", databaseName);
            return false;
        }

        JSONArray databaseTables = (JSONArray) db.get("tables");
        Object tbl = databaseTables.stream().filter((t) -> ((JSONObject) t).get("tableName").equals(tableName))
                .findFirst().orElse(null);
        if (tbl == null) {
            logger.error("Table: {} does not exist in database: {}.", tableName, databaseName);
            return false;
        }
        databaseTables.remove(tbl);

        db.put("tables", databaseTables);
        for (int i = 0; i < databasesArray.size(); i++) {
            if (((JSONObject) databasesArray.get(i)).get("databaseName").equals(databaseName)) {
                databasesArray.remove(databasesArray.get(i));
                databasesArray.add(i, db);
            }
        }
        databases.put("databases", databasesArray);

        try (FileWriter fileWriter = new FileWriter("catalog.json")) {
            fileWriter.write(databases.toJSONString());
            fileWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean createIndex(String databaseName, String tableName, Index index) throws Exception {
        logger.info("Create index: {} for table: {} from database: {}", index.getIndexName(), tableName, databaseName);
        JSONParser parser = new JSONParser();
        JSONObject databases = (JSONObject) parser.parse(new FileReader(CATALOG_PATH));
        JSONArray databasesArray = (JSONArray) databases.get("databases");

        JSONObject db = (JSONObject) databasesArray.stream().filter((database) -> ((JSONObject) database).get("databaseName").equals(databaseName))
                .findFirst().orElse(null);
        if (db == null) {
            logger.error("Database: {} does not exist", databaseName);
            return false;
        }

        JSONArray databaseTables = (JSONArray) db.get("tables");
        JSONObject tbl = (JSONObject) databaseTables.stream().filter((t) -> ((JSONObject) t).get("tableName").equals(tableName))
                .findFirst().orElse(null);
        if (tbl == null) {
            logger.error("Table: {} does not exist in database: {}.", tableName, databaseName);
            return false;
        }

        JSONArray tableIndexes = (JSONArray) tbl.get("indexes");
        Object idx = tableIndexes.stream().filter((t) -> ((JSONObject) t).get("indexName").equals(index.getIndexName()))
                .findFirst().orElse(null);
        if (idx != null) {
            logger.error("Invalid index name. There is already one index with the name: {}", index.getIndexName());
            return false;
        }
        tableIndexes.add(index.toJSON());
        tbl.put("indexes", tableIndexes);

        for (int i = 0; i < databaseTables.size(); i++) {
            if (((JSONObject) databaseTables.get(i)).get("tableName").equals(tableName)) {
                databaseTables.remove(databaseTables.get(i));
                databaseTables.add(i, tbl);
            }
        }

        db.put("tables", databaseTables);
        for (int i = 0; i < databasesArray.size(); i++) {
            if (((JSONObject) databasesArray.get(i)).get("databaseName").equals(databaseName)) {
                databasesArray.remove(databasesArray.get(i));
                databasesArray.add(i, db);
            }
        }
        databases.put("databases", databasesArray);

        try (FileWriter fileWriter = new FileWriter("catalog.json")) {
            fileWriter.write(databases.toJSONString());
            fileWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
