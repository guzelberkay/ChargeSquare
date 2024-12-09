package com.berkay.controller;

import com.berkay.service.ExternalApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


import static com.berkay.constants.EndPoints.EXTERNALAPI;
import static com.berkay.constants.EndPoints.FETCH_AND_SEND_STATIONS;

@RequiredArgsConstructor
@RestController(EXTERNALAPI)
public class ExternalApiController {

    private final ExternalApiService externalApiService;

    @GetMapping(FETCH_AND_SEND_STATIONS)
    public ResponseEntity<String> createChargingStations() {
        boolean isCreated = externalApiService.create();
        if (isCreated) {
            return ResponseEntity.ok("Charging stations created successfully!");
        } else {
            return ResponseEntity.status(500).body("Failed to create charging stations!");
        }
    }
}
