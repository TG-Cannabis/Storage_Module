package com.tgcannabis.storage_module.kafka;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mongodb.client.MongoClient;
import com.tgcannabis.storage_module.config.FogProcessorConfig;
import com.tgcannabis.storage_module.model.SensorData;
import com.tgcannabis.storage_module.mongo.MongoStorageService;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Objects;
import java.util.Properties;

/**
 * Handles consuming messages from the configured Apache Kafka topic and saving to MongoDB.
 */
public class KafkaConsumerService implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final FogProcessorConfig config;
    private KafkaConsumer<String, String> consumer;
    private final MongoStorageService storageService;

    private MongoCollection<Document> collection;

    /**
     * Constructs the Kafka Service.
     *
     * @param config The application configuration. Must not be null.
     */
    public KafkaConsumerService(FogProcessorConfig config, MongoStorageService storageService) {
        this.config = Objects.requireNonNull(config, "Configuration cannot be null");
        this.storageService = storageService;
        this.consumer = initializeConsumer(config);
    }

    /**
     * Initializes the KafkaConsumer instance based on configuration.
     */
    private KafkaConsumer<String, String> initializeConsumer(FogProcessorConfig config) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getKafkaBrokers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, config.getKafkaGroupId());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        try {
            LOGGER.info("Initializing Kafka Consumer for topic: {}", config.getKafkaTopic());
            return new KafkaConsumer<>(props);
        } catch (Exception e) {
            LOGGER.error("Failed to initialize Kafka Consumer: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Listens and processes messages from the Kafka topic and saves them to MongoDB.
     */
    public void listen() {
        if (this.consumer == null) {
            LOGGER.warn("Kafka consumer is not initialized. Cannot listen to topic '{}'.", config.getKafkaTopic());
            return;
        }

        LOGGER.info("Starting Kafka consumer for topic: {}", config.getKafkaTopic());
        consumer.subscribe(Collections.singletonList(config.getKafkaTopic()));
        Gson gson = new Gson();

        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    try {
                        SensorData sensorData = gson.fromJson(record.value(), SensorData.class);
                        LOGGER.debug("Deserialized sensor data: {}", sensorData);

                        Document doc = new Document("sensorType", sensorData.getSensorName().getSensorType())
                                .append("location", sensorData.getSensorName().getLocation())
                                .append("sensorId", sensorData.getSensorName().getId())
                                .append("value", sensorData.getValue())
                                .append("timestamp", sensorData.getTimestamp())
                                .append("savedAt", Instant.now().getEpochSecond());

                        storageService.saveSensorData(doc);
                    } catch (JsonSyntaxException e) {
                        LOGGER.error("Invalid JSON in Kafka message: {}", record.value(), e);
                    } catch (Exception e) {
                        LOGGER.error("Failed to process Kafka message: {}", record.value(), e);
                    }
                }
                consumer.commitAsync();

                if (Thread.currentThread().isInterrupted()) {
                    LOGGER.warn("Kafka listener thread interrupted. Exiting loop.");
                    break;
                }
            }
        } catch (Exception e) {
            LOGGER.error("Kafka listener failed: {}", e.getMessage(), e);
        }
    }

    /**
     * Closes the Kafka consumer gracefully.
     */
    @Override
    public void close() {
        if (consumer != null) {
            LOGGER.info("Closing Kafka consumer...");
            consumer.close();
            LOGGER.info("Kafka consumer closed.");
            consumer = null;
        }
    }
}
