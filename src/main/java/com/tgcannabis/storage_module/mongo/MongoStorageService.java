package com.tgcannabis.storage_module.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.tgcannabis.storage_module.config.FogProcessorConfig;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles MongoDB operations
 */
public class MongoStorageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoStorageService.class);
    private final MongoCollection<Document> collection;

    public MongoStorageService(FogProcessorConfig config) {
        LOGGER.info("Connecting to MongoDB at {}", config.getMongoUri());
        MongoClient client = MongoClients.create(config.getMongoUri());
        MongoDatabase database = client.getDatabase(config.getMongoDatabase());
        this.collection = database.getCollection(config.getMongoCollection());
        LOGGER.info("Connected to MongoDB - Database: {} - Collection: {}", config.getMongoDatabase(), config.getMongoCollection());
    }

    public void saveSensorData(Document document) {
        collection.insertOne(document);
        LOGGER.debug("Document saved to MongoDB");
    }
}
