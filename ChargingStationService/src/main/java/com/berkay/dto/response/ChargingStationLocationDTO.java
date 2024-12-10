package com.berkay.dto.response;

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
public class ChargingStationLocationDTO implements Serializable {
    Long stationId;
    String stationName;
    Double chargeSpeed;
    List<LocationResponseDTO> locations;
}

