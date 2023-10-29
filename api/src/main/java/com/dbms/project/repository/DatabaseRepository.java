package com.dbms.project.repository;
import com.dbms.project.db.DatabaseUtils;
import com.dbms.project.db.MongoUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@SuppressWarnings("unchecked")
@Repository
public class DatabaseRepository {
    private final MongoUtils mongoUtils;
    private final static Logger logger = LoggerFactory.getLogger(DatabaseRepository.class);
    private final static String CATALOG_PATH = "catalog.json";

    public DatabaseRepository(MongoUtils mongoUtils) {
        this.mongoUtils = mongoUtils;
    }

    public boolean createDatabase(String databaseName) throws Exception{
        logger.info("Create database with name: {}", databaseName);
        DatabaseUtils.createDatabase(databaseName);

        return true;
    }

    public boolean deleteDatabase(String databaseName) throws Exception {
        logger.info("Delete database with name: {}", databaseName);
        DatabaseUtils.deleteDatabase(databaseName);
        MongoTemplate mongoTemplate = mongoUtils.mongoTemplate(databaseName);
        if (mongoTemplate != null) {
            mongoTemplate.getDb().drop();
        }
        return true;
    }

    public JSONObject getAllDatabases() throws Exception {
        logger.info("Get all databases called.");
        return DatabaseUtils.getAllDatabases();
    }
}
