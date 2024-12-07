package com.berkay.dto.request;
import java.util.List;


public record ChargingStationCreateRequestDTO(
        String name,            // Şarj istasyonunun adı
        Double chargeSpeed,     // Şarj hızı (kW cinsinden)
        List<Long> locationIds   // İlgili konumları temsil eden Location ID'leri
) {
}
