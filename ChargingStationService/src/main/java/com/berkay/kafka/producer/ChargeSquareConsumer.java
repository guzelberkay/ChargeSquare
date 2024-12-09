package com.berkay.kafka.producer;

import com.berkay.kafka.model.ConsumerCreateChargeStation;
import com.berkay.service.ChargingStationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChargeSquareConsumer {

    private final ChargingStationService chargingStationService;

    @KafkaListener(id = "createCharging", topics = "create-charging-station")
    public void createConsumer(ConsumerCreateChargeStation consumerCreateChargeStation) {
        try {
            chargingStationService.createChargingStation(consumerCreateChargeStation);
            log.info("Kayıt başarılı: {}", consumerCreateChargeStation);
        } catch (Exception e) {
            log.error("Kayıt sırasında bir hata oluştu: {}", consumerCreateChargeStation, e);
        }
    }
}
