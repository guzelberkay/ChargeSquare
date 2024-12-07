package com.berkay.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExternalApiService {

    private final RestTemplate restTemplate;

    @Value("${external.api.url}")  // API URL'ini dışarıdan almak için
    private String externalApiUrl;

    public ExternalApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Harici API'den veri alma
    public String getChargingStationsData() {
        String url = externalApiUrl;  // Burada URL dışarıdan verilecek
        return restTemplate.getForObject(url, String.class);  // JSON veri dönecek
    }
}
