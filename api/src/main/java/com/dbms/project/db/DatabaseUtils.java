package com.dbms.project.db;

import com.dbms.project.exceptions.DBMSException;
import com.dbms.project.model.Database;
import com.dbms.project.repository.DatabaseRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

@Component
public class DatabaseUtils {
    private final static Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);
    private final static String CATALOG_PATH = "catalog.json";

    public static JSONObject getAllDatabases() {
        JSONParser parser = new JSONParser();
        try {
            JSONObject databases = (JSONObject) parser.parse(new FileReader(CATALOG_PATH));
            return databases;
        } catch (Exception e) {
            logger.error("The catalog file is missing...");
            throw new DBMSException("400", "There was an error while reading the file");
        }
    }

    public static void writeDatabaseToFile(JSONObject databases) {
        try (FileWriter fileWriter = new FileWriter(CATALOG_PATH)) {
            fileWriter.write(databases.toJSONString());
            fileWriter.flush();

        } catch (IOException e) {
            throw new DBMSException("400", "Unexpected error occured");
        }
    }

    public static JSONObject getDatabase(String databaseName) {
        JSONObject databases = DatabaseUtils.getAllDatabases();
        JSONArray databasesArray = (JSONArray) databases.get("databases");

        JSONObject db = (JSONObject) databasesArray.stream().filter((database) -> ((JSONObject) database).get("databaseName").equals(databaseName))
                .findFirst().orElse(null);
        return db;
    }

    public static void createDatabase(String databaseName) {
        JSONObject databases = DatabaseUtils.getAllDatabases();
        JSONObject db = DatabaseUtils.getDatabase(databaseName);

        if (db != null) {
            logger.error("Invalid name. There is already a database with the same name.");
            throw new DBMSException("400", "Invalid name. There is already a database with the same name.");
        }

        logger.info("Write database to file...");
        Database database = Database.createEmptyDatabase(databaseName);
        JSONArray databasesArray = (JSONArray) databases.get("databases");
        databasesArray.add(database.toJSON());
        databases.put("databases", databasesArray);
        DatabaseUtils.writeDatabaseToFile(databases);
    }

    public static void deleteDatabase(String databaseName) {
        JSONObject databases = DatabaseUtils.getAllDatabases();
        JSONObject db = DatabaseUtils.getDatabase(databaseName);
        JSONArray databasesArray = (JSONArray) databases.get("databases");

        if (db == null) {
            logger.error("Invalid name. There is no database with the name: {}", databaseName);
            throw new DBMSException("400", "Invalid name. There is no database with this name.");
        }

        for (int i = 0; i < databasesArray.size(); i++) {
            if (((JSONObject) databasesArray.get(i)).get("databaseName").equals(databaseName)) {
                databasesArray.remove(databasesArray.get(i));
            }
        }

        databases.put("databases", databasesArray);
        DatabaseUtils.writeDatabaseToFile(databases);
    }
}
