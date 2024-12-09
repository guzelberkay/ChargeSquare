package com.berkay.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    private final KafkaProperties kafkaProperties;
    private final String createTopic;

    public KafkaConfig(KafkaProperties kafkaProperties,
                       @Value("${spring.kafka.topic.create-charging-station}")  String createTopic)
                       {
        this.kafkaProperties = kafkaProperties;
        this.createTopic = createTopic;
    }

    /**
     * Kullanıcı kaydetme işlemi için topic
     */
    @Bean
    public NewTopic topicCreatChargingStation() {
        return TopicBuilder.name(createTopic)
                .partitions(1)
                .compact()
                .build();
    }
}
