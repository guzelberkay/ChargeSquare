package com.berkay.dto;
import java.util.List;


public record ChargingStationCreateRequestDTO(
        String name,            // Şarj istasyonunun adı
        Double chargeSpeed,     // Şarj hızı (kW cinsinden)
        List<LocationSaveRequestDTO> locationDtos
) {
}
