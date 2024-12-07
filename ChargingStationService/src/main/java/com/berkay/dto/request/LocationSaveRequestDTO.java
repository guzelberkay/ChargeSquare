package com.berkay.dto.request;

import jakarta.validation.constraints.NotNull;

public record LocationSaveRequestDTO(
        @NotNull String address,
        @NotNull String city,
        @NotNull String country
) {
}
