package com.lreas.database;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class MongoDBCleaner {
    private static final Logger logger = LoggerFactory.getLogger(MongoDBCleaner.class);

    private final MongoTemplate mongoTemplate;

    @Autowired
    public MongoDBCleaner(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @PostConstruct
    public void clearDatabase() {
        mongoTemplate.getDb().drop(); // clear all
        logger.info("Database dropped");
    }
}
