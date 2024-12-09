package com.berkay.service;

import com.berkay.dto.request.ChargingStationCreateRequestDTO;
import com.berkay.dto.request.LocationSaveRequestDTO;
import com.berkay.dto.response.ChargingStationLocationDTO;
import com.berkay.dto.response.LocationResponseDTO;
import com.berkay.entity.ChargingStation;
import com.berkay.entity.Location;
import com.berkay.repository.ChargingStationRepository;
import com.berkay.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@EnableKafka
public class ChargingStationService {

    private final ChargingStationRepository chargingStationRepository;
    private final LocationRepository locationRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "create-charging-station";

    @Transactional
    public boolean createChargingStation(ChargingStationCreateRequestDTO dto) {
        // Var olan bir istasyon olup olmadığını kontrol et
        ChargingStation existingStation = chargingStationRepository.findByName(dto.name())
                .orElse(null); // Burada Optional yerine null döndürüyoruz.

        if (existingStation != null) {
            throw new IllegalArgumentException("Bu isme sahip bir ChargingStation zaten mevcut: " + dto.name());
        }

        // Yeni location nesneleri oluşturuluyor
        List<Location> newLocations = new ArrayList<>();
        for (LocationSaveRequestDTO locationDto : dto.locationDtos()) {
            Location location = new Location();
            location.setAddress(locationDto.address());
            location.setCity(locationDto.city());
            location.setCountry(locationDto.country());
            locationRepository.save(location);
            newLocations.add(location);
        }

        // Yeni ChargingStation nesnesi oluşturuluyor
        ChargingStation chargingStation = ChargingStation.builder()
                .name(dto.name())
                .chargeSpeed(dto.chargeSpeed())
                .locations(newLocations)
                .build();

        // ChargingStation veritabanına kaydediliyor
        chargingStationRepository.save(chargingStation);

        // Veritabanına kaydedildiğini kontrol ediyoruz
        System.out.println("ChargingStation başarıyla kaydedildi: " + chargingStation);

        // Kafka'ya mesaj gönderiliyor
        kafkaTemplate.send(TOPIC, dto);
        System.out.println("Mesaj Kafka'ya başarıyla gönderildi: " + dto);

        return true;
    }


    public ChargingStationLocationDTO findByStationId(Long stationId) {
        // Fetching raw results from the repository for the given station ID
        List<Object[]> rawResults = locationRepository.findStationLocationDetailsByStationId(stationId);

        if (rawResults.isEmpty()) {
            // Handle the case where no station is found for the given ID
            throw new IllegalArgumentException("No Charging Station found with ID: " + stationId);
        }

        // Variables to store the station information
        String stationName = null;
        Double chargeSpeed = null;
        List<LocationResponseDTO> locations = new ArrayList<>();

        // Loop through the raw results and build the response DTO
        for (Object[] result : rawResults) {
            stationName = (String) result[1];
            chargeSpeed = ((Number) result[2]).doubleValue();
            Long locationId = ((Number) result[3]).longValue();
            String locationCountry = (String) result[4];
            String locationCity = (String) result[5];
            String locationAddress = (String) result[6];

            // Add the current location to the locations list
            locations.add(new LocationResponseDTO(locationId, locationCountry, locationCity, locationAddress));
        }

        // Return the ChargingStationLocationDTO for the given station
        return new ChargingStationLocationDTO(stationId, stationName, chargeSpeed, locations);
    }






    public List<ChargingStationLocationDTO> findAll() {
        // Repository'den ham sonuçları çekiyoruz
        List<Object[]> rawResults = locationRepository.findAllStationLocationDetails();

        // Ham sonuçları DTO'ya dönüştürüyoruz
        // Grup işlemi yapılacak, her bir charging station için birden fazla location olabilir
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

            // Bu stationId için yeni bir locationDTO ekliyoruz
            stationDTO.locations().add(new LocationResponseDTO(locationId, locationCountry, locationCity, locationAddress));
        }

        // stationMap'teki tüm stationları döndürüyoruz
        return new ArrayList<>(stationMap.values());
    }


}
