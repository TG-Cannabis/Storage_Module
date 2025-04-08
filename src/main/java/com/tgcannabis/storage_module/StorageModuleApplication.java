package com.tgcannabis.storage_module;

import com.tgcannabis.storage_module.config.FogProcessorConfig;
import com.tgcannabis.storage_module.kafka.KafkaConsumerService;
import com.tgcannabis.storage_module.mongo.MongoStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main application entry point. Initializes only the Kafka consumer.
 */
public class StorageModuleApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(StorageModuleApplication.class);

    public static void main(String[] args) {
        LOGGER.info("Starting Kafka Consumer...");

        FogProcessorConfig config = new FogProcessorConfig();

        MongoStorageService mongoStorageService = new MongoStorageService(config);

        try (KafkaConsumerService consumer = new KafkaConsumerService(config, mongoStorageService)) {
            consumer.listen();
        }

        LOGGER.info("Kafka Consumer stopped.");
    }
}

