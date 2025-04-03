package com.fog.kafka;

import com.fog.config.FogProcessorConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Objects;
import java.util.Properties;

/**
 * Handles consuming messages from the configured Apache Kafka topic.
 */
public class KafkaConsumerService implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerService.class);
    private final FogProcessorConfig config;
    private KafkaConsumer<String, String> consumer;

    /**
     * Constructs the Kafka Service.
     *
     * @param config The application configuration. Must not be null.
     */
    public KafkaConsumerService(FogProcessorConfig config) {
        this.config = Objects.requireNonNull(config, "Configuration cannot be null");
        initializeConsumer();
    }

    /**
     * Initializes the KafkaConsumer instance based on configuration.
     */
    private void initializeConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getKafkaBrokers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, config.getKafkaGroupId());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        
        
        
        try {
            LOGGER.info("Initializing Kafka Consumer for topic: {}", config.getKafkaTopic());
            this.consumer = new KafkaConsumer<>(props);
            this.consumer.subscribe(Collections.singletonList(config.getKafkaTopic()));
        } catch (Exception e) {
            LOGGER.error("Failed to initialize Kafka Consumer: {}", e.getMessage(), e);
            this.consumer = null;
        }
    }

    /**
     * Listens and processes messages from the Kafka topic.
     */
    public void listen() {
        if (this.consumer == null) {
            LOGGER.warn("Kafka consumer is not initialized. Cannot listen to topic '{}'.", config.getKafkaTopic());
            return;
        }
        
        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    LOGGER.info("Received Kafka message: Key=[{}], Value=[{}], Partition=[{}], Offset=[{}]",
                            record.key(), record.value(), record.partition(), record.offset());
                    
                    // Process the message here
                }
                consumer.commitAsync(); // Commit offsets asynchronously
            }
        } catch (Exception e) {
            LOGGER.error("Error during Kafka message processing: {}", e.getMessage(), e);
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
