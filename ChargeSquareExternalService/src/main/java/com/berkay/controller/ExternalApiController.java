package com.berkay.controller;

import com.berkay.service.ExternalApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


import static com.berkay.constants.EndPoints.EXTERNALAPI;
import static com.berkay.constants.EndPoints.FETCH_AND_SEND_STATIONS;

@RequiredArgsConstructor
@RestController(EXTERNALAPI)
public class ExternalApiController {

    private final ExternalApiService externalApiService;

    // Bu endpoint, dış API'den şarj istasyonlarını çeker ve Kafka'ya gönderir.
    @GetMapping(FETCH_AND_SEND_STATIONS)
    public String fetchAndSendChargingStations() {
        try {
            // Şarj istasyonlarını dış API'den çekiyoruz ve Kafka'ya gönderiyoruz
            externalApiService.fetchAndSendChargingStations();
            return "Şarj istasyonları başarıyla gönderildi.";
        } catch (Exception e) {
            // Eğer hata meydana gelirse, hata mesajını döndürüyoruz
            return "Bir hata oluştu: " + e.getMessage();
        }
    }
}
