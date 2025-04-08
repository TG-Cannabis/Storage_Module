package com.tgcannabis.storage_module.config;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loads and holds configuration parameters for the Fog Processor application.
 * Reads configuration from environment variables or a .env file.
 */
@Getter
public class FogProcessorConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(FogProcessorConfig.class);

    // Kafka Configuration
    private final String kafkaBrokers;
    private final String kafkaTopic;
    private final String kafkaGroupId;

    /**
     * Loads configuration using Dotenv library, looking for a .env file
     * in the classpath or project root, and falling back to environment variables.
     */
    public FogProcessorConfig() {
        // Configure Dotenv to search in standard places and ignore missing file
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing() // Don't fail if .env is not present
                .load();
        // Load Kafka settings
        kafkaBrokers = getEnv(dotenv, "KAFKA_BROKERS", "localhost:9093");
        kafkaTopic = getEnv(dotenv, "KAFKA_TOPIC", "sensores_cloud");
        kafkaGroupId = getEnv(dotenv, "KAFKA_GROUP_ID", "Fog-processor-group"); // Se cambia de Client ID a Group ID

        logConfiguration();
    }

    /**
     * Gets a value from System env variables (Or Dotenv file as fallback), returning a default if not found.
     * @param dotenv Dotenv instance
     * @param varName Environment variable name
     * @param defaultValue Default value if not found
     * @return The value found or the default value
     */
    private String getEnv(Dotenv dotenv, String varName, String defaultValue) {
        String value = System.getenv(varName);
        if (value != null) return value;

        value = dotenv.get(varName);
        return value != null ? value : defaultValue;
    }

    /**
     * Logs the loaded configuration (except sensitive tokens).
     */
    private void logConfiguration() {
        LOGGER.info("Fog Processor Configuration Loaded:");
        LOGGER.info("  Kafka Brokers: {}", kafkaBrokers);
        LOGGER.info("  Kafka Topic: {}", kafkaTopic);
        LOGGER.info("  Kafka Group ID: {}", kafkaGroupId);
    }
}
