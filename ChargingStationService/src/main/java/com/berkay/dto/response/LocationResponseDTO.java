package com.berkay.dto.response;

public record LocationResponseDTO(
        Long locationId,
        String locationCountry,
        String locationCity,
        String locationAddress
) {
}
