package com.berkay.dto.response;

import java.util.List;

public record ChargingStationLocationDTO(
        Long stationId,
        String stationName,
        Double chargeSpeed,
        List<LocationResponseDTO> locations  // Birden fazla location'ı tutacak liste
) {}

