package com.tgcannabis.storage_module;

import com.tgcannabis.storage_module.config.FogProcessorConfig;
import com.tgcannabis.storage_module.kafka.KafkaConsumerService;
import com.tgcannabis.storage_module.mongo.MongoStorageService;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

class StorageModuleApplicationTest {

    @Test
    void testRun_initializesAndRunsKafkaConsumer() {
        try (
                MockedConstruction<FogProcessorConfig> mockedConfig = Mockito.mockConstruction(FogProcessorConfig.class,
                        (mock, context) -> when(mock.getKafkaTopic()).thenReturn("test-topic"));

                MockedConstruction<MongoStorageService> mockedMongoService = Mockito.mockConstruction(MongoStorageService.class,
                        (mock, context) -> {
                        });

                MockedConstruction<KafkaConsumerService> mockedKafkaConsumer = Mockito.mockConstruction(KafkaConsumerService.class,
                        (mock, context) -> doNothing().when(mock).listen())
        ) {
            // Act
            new StorageModuleApplication().run();

            // Verify that all components were constructed and used
            KafkaConsumerService constructedConsumer = mockedKafkaConsumer.constructed().get(0);
            verify(constructedConsumer).listen();
        }
    }
}
