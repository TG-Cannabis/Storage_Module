package com.tgcannabis.storage_module.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles MongoDB operations
 */
public class MongoStorageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoStorageService.class);
    private final MongoCollection<Document> collection;

    public MongoStorageService(String mongoUri, String dbName, String collectionName) {
        LOGGER.info("Connecting to MongoDB at {}", mongoUri);
        MongoClient client = MongoClients.create(mongoUri);
        MongoDatabase database = client.getDatabase(dbName);
        this.collection = database.getCollection(collectionName);
        LOGGER.info("Connected to MongoDB - Database: {} - Collection: {}", dbName, collectionName);
    }

    public void saveSensorData(Document document) {
        collection.insertOne(document);
        LOGGER.debug("Document saved to MongoDB");
    }
}
