package com.berkay.controller;

import com.berkay.service.ExternalApiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IntegrationController {

    private final ExternalApiService externalApiService;

    public IntegrationController(ExternalApiService externalApiService) {
        this.externalApiService = externalApiService;
    }

    // Harici API'den veri almak için endpoint
    @GetMapping("/external/charging-stations")
    public String getChargingStationsData() {
        return externalApiService.getChargingStationsData();  // Harici API'den veri alınır
    }
}
