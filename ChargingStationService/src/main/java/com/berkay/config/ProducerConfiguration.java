package com.berkay.config;

import com.berkay.dto.request.ChargingStationCreateRequestDTO;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;
@EnableKafka
@Configuration
public class ProducerConfiguration {

    @Value("${spring.kafka.consumer.bootstrap-servers}")
    private String serverAddress;

    // ChargingStationCreateRequestDTO için ProducerFactory tanımı
    @Bean
    public ProducerFactory<String, ChargingStationCreateRequestDTO> producerFactoryForDTO() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, serverAddress);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configs);
    }

    @Bean
    public KafkaTemplate<String, ChargingStationCreateRequestDTO> kafkaTemplateForDTO() {
        return new KafkaTemplate<>(producerFactoryForDTO());
    }

    // Genel amaçlı KafkaTemplate tanımı (Object türünde mesajlar için)
    @Bean
    public ProducerFactory<String, Object> producerFactoryForObject() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, serverAddress);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configs);
    }

    @Bean
    public KafkaTemplate<String, Object> genericKafkaTemplate() {
        return new KafkaTemplate<>(producerFactoryForObject());
    }
}
