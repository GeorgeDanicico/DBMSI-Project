package com.dbms.project.db;

import com.dbms.project.exceptions.DBMSException;
import com.dbms.project.model.Index;
import com.dbms.project.model.Table;
import lombok.Data;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TableUtils {
    private final static Logger logger = LoggerFactory.getLogger(TableUtils.class);

    public static JSONArray getAllTablesForDatabase(String databaseName) {
        JSONObject databases = DatabaseUtils.getAllDatabases();
        JSONObject db = DatabaseUtils.getDatabase(databaseName);
        if (db == null) {
            logger.error("Invalid name. There is no database with the name: {}", databaseName);
            throw new DBMSException("400", "Invalid name. There is no database with this name.");
        }
        return (JSONArray) db.get("tables");
    }

    public static JSONObject getTableFromDatabase(String databaseName, String tableName) {
        JSONObject databases = DatabaseUtils.getAllDatabases();
        JSONObject db = DatabaseUtils.getDatabase(databaseName);
        if (db == null) {
            logger.error("Invalid name. There is no database with the name: {}", databaseName);
            throw new DBMSException("400", "Invalid name. There is no database with this name.");
        }
        JSONArray databaseTables = (JSONArray) db.get("tables");
        JSONObject tbl = (JSONObject) databaseTables.stream().filter((t) -> ((JSONObject) t).get("tableName").equals(tableName))
                .findFirst().orElse(null);

        return tbl;
    }

    public static void createTable(String databaseName, Table table) {
        JSONObject databases = DatabaseUtils.getAllDatabases();
        JSONObject db = DatabaseUtils.getDatabase(databaseName);
        JSONArray databasesArray = (JSONArray) databases.get("databases");
        JSONArray databaseTables = (JSONArray) db.get("tables");
        JSONObject tbl = TableUtils.getTableFromDatabase(databaseName, table.getTableName());

        if (tbl != null) {
            logger.error("Invalid table name.");
            throw new DBMSException("400", "Invalid table name. There is already a table with this name.");
        }

        databaseTables.add(table.toJSON());
        db.put("tables", databaseTables);
        for (int i = 0; i < databasesArray.size(); i++) {
            if (((JSONObject) databasesArray.get(i)).get("databaseName").equals(databaseName)) {
                databasesArray.remove(databasesArray.get(i));
                databasesArray.add(i, db);
            }
        }
        databases.put("databases", databasesArray);
        DatabaseUtils.writeDatabaseToFile(databases);
    }

    public static void deleteTable(String databaseName, String tableName) {
        JSONObject databases = DatabaseUtils.getAllDatabases();
        JSONObject db = DatabaseUtils.getDatabase(databaseName);
        JSONArray databasesArray = (JSONArray) databases.get("databases");
        JSONObject tbl = TableUtils.getTableFromDatabase(databaseName, tableName);
        JSONArray databaseTables = (JSONArray) db.get("tables");

        if (tbl == null) {
            logger.error("Invalid table name.");
            throw new DBMSException("400", "Invalid table name. There is no table with this name.");
        }

        for (int i = 0; i < databaseTables.size(); i++) {
            if (((JSONObject) databaseTables.get(i)).get("tableName").equals(tableName)) {
                databaseTables.remove(databaseTables.get(i));
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
        DatabaseUtils.writeDatabaseToFile(databases);
    }

    public static void createIndex(String databaseName, String tableName, Index index) {
        JSONObject databases = DatabaseUtils.getAllDatabases();
        JSONObject db = DatabaseUtils.getDatabase(databaseName);
        JSONArray databasesArray = (JSONArray) databases.get("databases");
        JSONArray databaseTables = (JSONArray) db.get("tables");
        JSONObject tbl = TableUtils.getTableFromDatabase(databaseName, tableName);

        if (tbl == null) {
            logger.error("Invalid table name.");
            throw new DBMSException("400", "Invalid table name. There is no table with this name.");
        }

        JSONArray tableIndexes = (JSONArray) tbl.get("indexes");
        Object idx = tableIndexes.stream().filter((t) -> ((JSONObject) t).get("indexName").equals(index.getIndexName()))
                .findFirst().orElse(null);
        if (idx != null) {
            logger.error("Invalid index name. There is already one index with the name: {}", index.getIndexName());
            throw new DBMSException("400", "Invalid index name. There is already an index with this name.");
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
        DatabaseUtils.writeDatabaseToFile(databases);
    }
}
