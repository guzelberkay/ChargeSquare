package com.berkay.dto.response;

import java.util.List;

public record ChargingStationDetailResponseDTO(
        Long id,
        String name,
        Double chargeSpeed,
        List<Long> locationIds  // Bu, LocationDTO yerine sadece location'ların ID'lerini tutacak
) {
}
