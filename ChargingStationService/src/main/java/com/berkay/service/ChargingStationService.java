package com.berkay.service;

import com.berkay.dto.response.ChargingStationLocationDTO;
import com.berkay.dto.response.LocationResponseDTO;
import com.berkay.entity.ChargingStation;
import com.berkay.entity.Location;
import com.berkay.kafka.model.ConsumerCreateChargeStation;
import com.berkay.kafka.model.ConsumerLocations;
import com.berkay.repository.ChargingStationRepository;
import com.berkay.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
@EnableKafka
public class ChargingStationService {

    private final ChargingStationRepository chargingStationRepository;
    private final LocationRepository locationRepository;


    @Transactional    // tüm yapılan değişiklikler geri alınır ve veritabanındaki veri tutarlılığı korunur.
    public boolean createChargingStation(ConsumerCreateChargeStation dto) {
        ChargingStation existingStation = chargingStationRepository.findByName(dto.getName())
                .orElse(null);

        if (existingStation != null) {
            return false;
        }

        // Yeni location nesneleri oluşturuluyor
        List<Location> newLocations = new ArrayList<>();
        for (ConsumerLocations locationDto : dto.getLocationDtos()) {
            Location location = new Location();
            location.setAddress(locationDto.getAddress());
            location.setCity(locationDto.getCity());
            location.setCountry(locationDto.getCountry());
            locationRepository.save(location);
            newLocations.add(location);
        }

        ChargingStation chargingStation = ChargingStation.builder()
                .name(dto.getName())
                .chargeSpeed(dto.getChargeSpeed())
                .locations(newLocations)
                .build();


        chargingStationRepository.save(chargingStation);

        return true;
    }

    @Cacheable(value = "station-find-by-id-case")
    public ChargingStationLocationDTO findByStationId(Long stationId) {

        List<Object[]> rawResults = locationRepository.findStationLocationDetailsByStationId(stationId);


        if (rawResults.isEmpty()) {

            throw new IllegalArgumentException("No Charging Station found with ID: " + stationId + ". Please check if the ID exists in the database.");
        }


        String stationName = null;
        Double chargeSpeed = null;
        List<LocationResponseDTO> locations = new ArrayList<>();

        // Veritabanından gelen sonuçları işliyoruz
        for (Object[] result : rawResults) {
            stationName = (String) result[1]; // İstasyon ismi
            chargeSpeed = ((Number) result[2]).doubleValue(); // Şarj hızı
            Long locationId = ((Number) result[3]).longValue(); // Konum ID'si
            String locationCountry = (String) result[4]; // Konum ülke
            String locationCity = (String) result[5]; // Konum şehir
            String locationAddress = (String) result[6]; // Konum adresi

            // Konumları LocationResponseDTO nesnelerine ekliyoruz
            locations.add(new LocationResponseDTO(locationId, locationCountry, locationCity, locationAddress));
        }

        return new ChargingStationLocationDTO(stationId, stationName, chargeSpeed, locations);
    }


    @Cacheable(value = "station-find-all-case")
    public List<ChargingStationLocationDTO> findAll() {
        List<Object[]> rawResults = locationRepository.findAllStationLocationDetails();
        System.out.println(rawResults);

        // her bir charging station için birden fazla location olabilir
        Map<Long, ChargingStationLocationDTO> stationMap = new HashMap<>();

        for (Object[] result : rawResults) {
            Long stationId = ((Number) result[0]).longValue();
            String stationName = (String) result[1];
            Double chargeSpeed = ((Number) result[2]).doubleValue();
            Long locationId = ((Number) result[3]).longValue();
            String locationCountry = (String) result[4];
            String locationCity = (String) result[5];
            String locationAddress = (String) result[6];

            // Eğer stationMap'te bu stationId yoksa, yeni bir ChargingStationLocationDTO oluşturuyoruz
            ChargingStationLocationDTO stationDTO = stationMap.computeIfAbsent(stationId, id ->
                    new ChargingStationLocationDTO(
                            id,
                            stationName,
                            chargeSpeed,
                            new ArrayList<>()
                    ));


            stationDTO.getLocations().add(new LocationResponseDTO(locationId, locationCountry, locationCity, locationAddress));
        }


        return new ArrayList<>(stationMap.values());
    }


}
