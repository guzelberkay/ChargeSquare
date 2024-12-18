package com.berkay.config;


import com.berkay.kafka.model.ConsumerCreateChargeStation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import java.util.HashMap;
import java.util.Map;
@EnableKafka
@Configuration
public class KafkaConfig {
    /**
     * Sender -> Producer
     */

    @Bean
    public ProducerFactory<String, ConsumerCreateChargeStation> producerFactory() {
        return new DefaultKafkaProducerFactory<>(senderProps());
    }

    private Map<String, Object> senderProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        props.put(ProducerConfig.LINGER_MS_CONFIG, 10);   // batching yani belirli bir süre boyunca toplu olarak işleme (10ms)
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class); //Gönderilen verileri kafkanın anlayabileceği bir byte dizisine dönüştürür.
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class); // mesaj dizilerini JSON formatına dönüştürür.
        return props;
    }

    @Bean
    public KafkaTemplate<String, ConsumerCreateChargeStation> kafkaTemplate(ProducerFactory<String, ConsumerCreateChargeStation> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
