package com.berkay.service;

import com.berkay.kafka.producer.ChargeSquareProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ExternalApiService {

    private final ChargeSquareProducer chargeSquareProducer;

    public boolean create() {
        try {
            // ChargeSquareProducer'dan sendCreate metodunu çağırarak Kafka'ya mesaj gönder
            chargeSquareProducer.sendCreate();
            return true; // İşlem başarılıysa true döner
        } catch (Exception e) {
            // Hata durumunda loglama ve false döndürme
            e.printStackTrace();
            return false;
        }
    }
}
