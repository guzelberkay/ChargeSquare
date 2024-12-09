package com.berkay.dto;

import jakarta.validation.constraints.NotNull;

public record LocationSaveRequestDTO(
         String address,
         String city,
         String country
) {
}
