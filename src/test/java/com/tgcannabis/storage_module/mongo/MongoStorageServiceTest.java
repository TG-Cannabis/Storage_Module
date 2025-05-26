package com.tgcannabis.storage_module.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.tgcannabis.storage_module.config.FogProcessorConfig;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

class MongoStorageServiceTest {
    private FogProcessorConfig config;
    private MongoClient mockClient;
    private MongoCollection<Document> mockCollection;

    @BeforeEach
    void setUp() {
        // Mock the config
        config = mock(FogProcessorConfig.class);
        when(config.getMongoUri()).thenReturn("mongodb://localhost:27017");
        when(config.getMongoDatabase()).thenReturn("testDb");
        when(config.getMongoCollection()).thenReturn("testCollection");

        // Mock MongoDB classes
        mockClient = mock(MongoClient.class);
        MongoDatabase mockDatabase = mock(MongoDatabase.class);
        mockCollection = mock(MongoCollection.class);

        when(mockClient.getDatabase("testDb")).thenReturn(mockDatabase);
        when(mockDatabase.getCollection("testCollection")).thenReturn(mockCollection);
    }

    @Test
    void testSaveSensorData_insertsDocumentIntoCollection() {
        // Use a static mock to intercept MongoClients.create()
        try (MockedStatic<MongoClients> mockedMongoClients = Mockito.mockStatic(MongoClients.class)) {
            mockedMongoClients.when(() -> MongoClients.create("mongodb://localhost:27017"))
                    .thenReturn(mockClient);

            MongoStorageService service = new MongoStorageService(config);

            Document testDoc = new Document("sensorId", "abc123").append("value", 42.0);
            service.saveSensorData(testDoc);

            // Verify that insertOne was called on the mocked collection
            verify(mockCollection).insertOne(testDoc);
        }
    }
}
