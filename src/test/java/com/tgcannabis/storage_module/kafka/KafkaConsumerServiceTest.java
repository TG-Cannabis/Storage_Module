package com.tgcannabis.storage_module.kafka;

import com.google.gson.Gson;
import com.tgcannabis.storage_module.config.FogProcessorConfig;
import com.tgcannabis.storage_module.model.SensorData;
import com.tgcannabis.storage_module.mongo.MongoStorageService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class KafkaConsumerServiceTest {
    @Mock
    private FogProcessorConfig config;

    @Mock
    private MongoStorageService storageService;

    @Mock
    private KafkaConsumer<String, String> mockConsumer;

    @InjectMocks
    private KafkaConsumerService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(config.getKafkaBrokers()).thenReturn("localhost:9092");
        when(config.getKafkaTopic()).thenReturn("test-topic");
        when(config.getKafkaGroupId()).thenReturn("test-group");
        service = new KafkaConsumerService(config, storageService, mockConsumer);
    }

    @Test
    void testClose_closesConsumer() {
        service.close();
        verify(mockConsumer).close();
    }

    @Test
    void testListen_deserializesAndSavesSensorData() {
        String json = new Gson().toJson(new SensorData("sensor1", "temperature", "room1", 23.5, System.currentTimeMillis()));
        ConsumerRecord<String, String> record = new ConsumerRecord<>("test-topic", 0, 0L, null, json);
        ConsumerRecords<String, String> records = new ConsumerRecords<>(Collections.singletonMap(new org.apache.kafka.common.TopicPartition("test-topic", 0), List.of(record)));

        when(mockConsumer.poll(any(Duration.class))).thenAnswer(invocation -> {
            Thread.currentThread().interrupt();  // Stop loop after one poll
            return records;
        });

        service.listen();

        verify(storageService, times(1)).saveSensorData(any(Document.class));
    }

    @Test
    void testListen_logsAndSkipsInvalidJson() {
        String badJson = "{invalid_json}";
        ConsumerRecord<String, String> record = new ConsumerRecord<>("test-topic", 0, 0L, null, badJson);
        ConsumerRecords<String, String> records = new ConsumerRecords<>(Collections.singletonMap(new org.apache.kafka.common.TopicPartition("test-topic", 0), List.of(record)));

        when(mockConsumer.poll(any(Duration.class))).thenAnswer(invocation -> {
            Thread.currentThread().interrupt();
            return records;
        });

        service.listen();

        verify(storageService, never()).saveSensorData(any(Document.class));
    }

    @Test
    void testListen_retriesInitializationIfConsumerIsNull() {
        KafkaConsumer<String, String> reinitializedConsumer = mock(KafkaConsumer.class);

        // Spy on the KafkaConsumerService to intercept the initializeConsumer method
        KafkaConsumerService spyService = Mockito.spy(new KafkaConsumerService(config, storageService, null));

        // Stub the initializeConsumer method to return our mocked KafkaConsumer
        doReturn(reinitializedConsumer).when(spyService).initializeConsumer(config);

        // Call the method under test
        spyService.listen();

        // Verify that initializeConsumer was called because initial consumer was null
        verify(spyService).initializeConsumer(config);

        // Verify the consumer was used (subscribed to the topic)
        verify(reinitializedConsumer).subscribe(Collections.singletonList(config.getKafkaTopic()));
    }
}
