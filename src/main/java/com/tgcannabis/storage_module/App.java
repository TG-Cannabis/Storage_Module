package com.tgcannabis.storage_module;

import com.tgcannabis.storage_module.config.FogProcessorConfig;
import com.tgcannabis.storage_module.kafka.KafkaConsumerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main application entry point. Initializes only the Kafka consumer.
 */
public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        LOGGER.info("Starting Kafka Consumer...");

        // Configuración de Kafka (ajusta los valores según sea necesario)
        FogProcessorConfig config = new FogProcessorConfig(
        );

        // Iniciar el Consumidor
        try (KafkaConsumerService consumer = new KafkaConsumerService(config)) {
            consumer.listen(); // Escuchar mensajes de Kafka
        }

        LOGGER.info("Kafka Consumer stopped.");
    }
}

