package com.berkay.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LocationResponseDTO implements Serializable {
    Long locationId;
    String locationCountry;
    String locationCity;
    String locationAddress;
}
