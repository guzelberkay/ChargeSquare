package com.berkay.controller;

import com.berkay.dto.request.ChargingStationCreateRequestDTO;
import com.berkay.dto.response.ChargingStationLocationDTO;
import com.berkay.dto.response.ResponseDTO;
import com.berkay.kafka.model.ConsumerCreateChargeStation;
import com.berkay.service.ChargingStationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.berkay.constants.EndPoints.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(STATIONS)
public class ChargingStationController {

    private final ChargingStationService chargingStationService;

    @Operation(summary = "Create a new charging station", description = "Adds a new charging station with the provided details.")
    @PostMapping(CREATE_STATION)
    public ResponseEntity<ResponseDTO<Boolean>> createChargingStation(@RequestBody ConsumerCreateChargeStation dto) {
        Boolean isCreated = chargingStationService.createChargingStation(dto);
        return ResponseEntity.ok(
                ResponseDTO.<Boolean>builder()
                        .code(isCreated ? 200 : 400)
                        .message(isCreated ? "Charging station created successfully." : "Failed to create charging station.")
                        .data(isCreated)
                        .build()
        );
    }

    @Operation(
            summary = "Get all charging stations",
            description = "Fetches a list of all available charging stations."
    )
    @Cacheable(value = "stationsCache", key = "#root.methodName")
    @GetMapping(GET_ALL_STATIONS)  // /stations/get-all-stations
    public ResponseEntity<ResponseDTO<List<ChargingStationLocationDTO>>> getAllChargingStations() {
        List<ChargingStationLocationDTO> stations = chargingStationService.findAll();

        return ResponseEntity.ok(
                new ResponseDTO<>(200, "Charging stations retrieved successfully.", stations)
        );
    }

    @Operation(
            summary = "Get charging station by ID",
            description = "Fetches detailed information about a specific charging station by its ID."
    )
    @Cacheable(value = "stationCache", key = "#stationId")
    @GetMapping(GET_STATION_BY_ID)  // /stations/get-station-by-id
    public ResponseEntity<ResponseDTO<ChargingStationLocationDTO>> getChargingStationById(@RequestParam Long stationId) {
        ChargingStationLocationDTO station = chargingStationService.findByStationId(stationId);

        if (station == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseDTO<>(404, "Charging station not found.", null)
            );
        }

        return ResponseEntity.ok(
                new ResponseDTO<>(200, "Charging station retrieved successfully.", station)
        );
    }
}
