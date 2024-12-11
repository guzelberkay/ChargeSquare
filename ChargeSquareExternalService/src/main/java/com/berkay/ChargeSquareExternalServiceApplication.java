package com.berkay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableCaching
public class ChargeSquareExternalServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChargeSquareExternalServiceApplication.class, args);  // Uygulama başlatma
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();  // RestTemplate bean'i eklenir
    }
}
