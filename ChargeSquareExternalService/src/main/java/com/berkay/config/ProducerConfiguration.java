package com.berkay.config;

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

import java.util.HashMap;
import java.util.Map;
@EnableKafka
@Configuration
public class ProducerConfiguration {

    @Value("${spring.kafka.consumer.bootstrap-servers}")
    private String serverAddress;

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                serverAddress);

        configs.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);

        configs.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(configs);

    }
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
