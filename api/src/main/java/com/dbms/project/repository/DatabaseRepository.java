package com.dbms.project.repository;
import com.dbms.project.model.Database;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.List;

@SuppressWarnings("unchecked")
@Repository
public class DatabaseRepository {
    private final static Logger logger = LoggerFactory.getLogger(DatabaseRepository.class);
    private final static String CATALOG_PATH = "catalog.json";

    public boolean createDatabase(String name) throws Exception{
        logger.info("Create database with name: {}", name);
        JSONParser parser = new JSONParser();
        JSONObject databases = (JSONObject) parser.parse(new FileReader(CATALOG_PATH));
        JSONArray databasesArray = (JSONArray) databases.get("databases");

        Object db = databasesArray.stream().filter((database) -> ((JSONObject) database).get("databaseName").equals(name))
                .findFirst().orElse(null);
        if (db != null) {
            logger.error("Invalid name. There is already a database with the same name.");
            return false;
        }

        logger.info("Write database to file...");
        Database database = Database.createEmptyDatabase(name);
        databasesArray.add(database.toJSON());
        databases.put("databases", databasesArray);

        try (FileWriter fileWriter = new FileWriter("catalog.json")) {
            fileWriter.write(databases.toJSONString());
            fileWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean deleteDatabase(String databaseName) throws Exception {
        logger.info("Delete database with name: {}", databaseName);
        JSONParser parser = new JSONParser();
        JSONObject databases = (JSONObject) parser.parse(new FileReader(CATALOG_PATH));
        JSONArray databasesArray = (JSONArray) databases.get("databases");

        Object db = databasesArray.stream().filter((database) -> ((JSONObject) database).get("databaseName").equals(databaseName))
                .findFirst().orElse(null);
        if (db == null) {
            return false;
        }

        for (int i = 0; i < databasesArray.size(); i++) {
            if (((JSONObject) databasesArray.get(i)).get("databaseName").equals(databaseName)) {
                databasesArray.remove(databasesArray.get(i));
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

    public JSONObject getAllDatabases() throws Exception {
        logger.info("Get all databases called.");
        JSONParser parser = new JSONParser();
        JSONObject databases = (JSONObject) parser.parse(new FileReader(CATALOG_PATH));

        return databases;
    }
}
