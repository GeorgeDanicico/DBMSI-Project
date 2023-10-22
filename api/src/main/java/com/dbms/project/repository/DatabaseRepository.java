package com.dbms.project.repository;
import com.dbms.project.db.DatabaseUtils;
import com.dbms.project.model.Database;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.xml.crypto.Data;
import java.io.*;
import java.util.List;

@SuppressWarnings("unchecked")
@Repository
public class DatabaseRepository {
    private final static Logger logger = LoggerFactory.getLogger(DatabaseRepository.class);
    private final static String CATALOG_PATH = "catalog.json";

    public boolean createDatabase(String databaseName) throws Exception{
        logger.info("Create database with name: {}", databaseName);
        DatabaseUtils.createDatabase(databaseName);
        return true;
    }

    public boolean deleteDatabase(String databaseName) throws Exception {
        logger.info("Delete database with name: {}", databaseName);
        DatabaseUtils.deleteDatabase(databaseName);
        return true;
    }

    public JSONObject getAllDatabases() throws Exception {
        logger.info("Get all databases called.");
        return DatabaseUtils.getAllDatabases();
    }
}
