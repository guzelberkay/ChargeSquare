package com.berkay.kafka.model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ConsumerCreateChargeStation implements Serializable {
    String name;            // Şarj istasyonunun adı
    Double chargeSpeed;     // Şarj hızı (kW cinsinden)
    List<ConsumerLocations> locationDtos;

}
