package com.dbms.project.db;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoUtils {

    public MongoClient mongo(String databaseName) {
        String connection = String.format("mongodb://localhost:27017/%s", databaseName);
        ConnectionString connectionString = new ConnectionString(connection);
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();

        return MongoClients.create(mongoClientSettings);
    }

    public MongoTemplate mongoTemplate(String databaseName) throws Exception {
        return new MongoTemplate(mongo(databaseName), databaseName);
    }
}
