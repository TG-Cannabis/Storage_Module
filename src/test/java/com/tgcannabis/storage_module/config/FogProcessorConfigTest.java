package com.tgcannabis.storage_module.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FogProcessorConfigTest {
    @Test
    void configLoadsFromDotenvWhenNoSystemEnvPresent() {
        Dotenv dotenv = mock(Dotenv.class);
        when(dotenv.get("KAFKA_BROKERS")).thenReturn("dotenv-broker:9092");
        when(dotenv.get("KAFKA_TOPIC")).thenReturn("dotenv-topic");
        when(dotenv.get("KAFKA_GROUP_ID")).thenReturn("dotenv-group");
        when(dotenv.get("MONGO_URI")).thenReturn("dotenv-uri");
        when(dotenv.get("MONGO_DATABASE")).thenReturn("dotenv-db");
        when(dotenv.get("MONGO_COLLECTION")).thenReturn("dotenv-coll");

        FogProcessorConfig config = new FogProcessorConfig(dotenv);

        assertEquals("dotenv-broker:9092", config.getKafkaBrokers());
        assertEquals("dotenv-topic", config.getKafkaTopic());
        assertEquals("dotenv-group", config.getKafkaGroupId());
        assertEquals("dotenv-uri", config.getMongoUri());
        assertEquals("dotenv-db", config.getMongoDatabase());
        assertEquals("dotenv-coll", config.getMongoCollection());
    }
}
