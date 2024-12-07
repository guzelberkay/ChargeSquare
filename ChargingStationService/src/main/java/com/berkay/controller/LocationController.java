package com.berkay.controller;

import com.berkay.dto.request.LocationSaveRequestDTO;
import com.berkay.dto.response.LocationResponseDTO;
import com.berkay.dto.response.ResponseDTO;
import com.berkay.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.berkay.constants.EndPoints.*;

@RestController
@RequestMapping(LOCATIONS)
public class
LocationController {

    @Autowired
    private LocationService locationService;

    @Operation(
            summary = "Add a new Location",
            description = "Creates a new Location with the provided address, city, and country information."
    )
    @PostMapping(CREATE_LOCATION)
    public ResponseEntity<ResponseDTO<Boolean>> addLocation(@RequestBody LocationSaveRequestDTO dto) {
        try {
            Boolean success = locationService.addLocation(dto);
            return ResponseEntity.ok(ResponseDTO.<Boolean>builder()
                    .data(success)
                    .code(200)
                    .message("Location successfully registered")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseDTO.<Boolean>builder()
                            .data(false)
                            .code(400)
                            .message("Error saving location: " + e.getMessage())
                            .build()
            );
        }
    }

    @Operation(summary = "Get all Locations", description = "Returns a list of all locations with their id, address, city, and country.")
    @Cacheable(value = "locationsCache", key = "'allLocations'")  // Cache for all locations
    @GetMapping(GET_ALL_LOCATIONS)
    public ResponseEntity<ResponseDTO<List<LocationResponseDTO>>> getAllLocations() {
        try {
            List<LocationResponseDTO> locations = locationService.findAllLocations();
            return ResponseEntity.ok(ResponseDTO.<List<LocationResponseDTO>>builder()
                    .data(locations)
                    .code(200)
                    .message("Locations successfully retrieved")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseDTO.<List<LocationResponseDTO>>builder()
                            .data(null)
                            .code(400)
                            .message("Error retrieving locations: " + e.getMessage())
                            .build()
            );
        }
    }
}
