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
            return true;
        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
    }
}
