package com.berkay.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.cache.annotation.EnableCaching;

import java.time.Duration;

@Configuration
@EnableCaching  // Bu anotasyon cache'i etkinleştirir
public class RedisConfig {

    // RedisCacheManager Bean'i için senkron bağlantı kullanımı (Lettuce veya Jedis)
    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        // Varsayılan cache yapılandırmasını tanımlıyoruz
        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                // Anahtarların serileştirilmesi (String olarak)
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                // Değerlerin serileştirilmesi (JSON formatında)
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                // Varsayılan TTL (Time to Live), 1 saat olarak belirlenebilir
                .entryTtl(Duration.ofHours(1))  // Cache'deki veriler 1 saat sonra otomatik silinir
                // Cache'deki null değerlerin saklanmaması
                .disableCachingNullValues();

        // CacheManager'ı RedisConnectionFactory ile oluşturuyoruz
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(cacheConfig)  // Varsayılan cache yapılandırması
                .build();
    }

    // RedisTemplate Bean'i (CRUD işlemleri için)
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        // RedisTemplate oluşturuluyor
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());  // Key'ler String formatında
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());  // Değerler JSON formatında
        template.setHashKeySerializer(new StringRedisSerializer());  // Hash key'ler String formatında
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());  // Hash value'lar JSON formatında
        return template;
    }
}
