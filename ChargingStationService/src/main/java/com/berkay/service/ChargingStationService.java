package com.berkay.service;

import com.berkay.dto.request.ChargingStationCreateRequestDTO;
import com.berkay.dto.response.ChargingStationDetailResponseDTO;
import com.berkay.dto.response.ChargingStationLocationDTO;
import com.berkay.dto.response.LocationResponseDTO;
import com.berkay.entity.ChargingStation;
import com.berkay.entity.Location;
import com.berkay.repository.ChargingStationRepository;
import com.berkay.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChargingStationService {

    private final ChargingStationRepository chargingStationRepository;
    private final LocationRepository locationRepository;

    @Transactional
    public boolean createChargingStation(ChargingStationCreateRequestDTO dto) {

        // İsimle var olan bir ChargingStation olup olmadığını kontrol et
        ChargingStation existingStation = chargingStationRepository.findByName(dto.name())
                .orElse(null); // Burada Optional yerine null döndürüyoruz.

        // Eğer var olan bir istasyon varsa, hata fırlatıyoruz
        if (existingStation != null) {
            throw new IllegalArgumentException("Bu isme sahip bir ChargingStation zaten mevcut: " + dto.name());
        }

        List<Long> locationIds = dto.locationIds();

        // Geçerli Location ID'lerini alıyoruz
        List<Long> validLocationIds = locationRepository.findIdsByIds(locationIds);

        // Geçersiz ID'leri filtreliyoruz
        List<Long> invalidLocationIds = locationIds.stream()
                .filter(id -> !validLocationIds.contains(id)) // Geçersiz ID'leri buluyoruz
                .collect(Collectors.toList());

        // Geçersiz Location ID'lerini logluyoruz (isteğe bağlı)
        if (!invalidLocationIds.isEmpty()) {
            System.out.println("Geçersiz Location ID'leri: " + invalidLocationIds); // Geçersiz ID'leri kaydedebiliriz
        }

        // Geçerli Location nesnelerini alıyoruz
        List<Location> validLocations = locationRepository.findAllById(validLocationIds);

        // Yeni ChargingStation nesnesi oluşturuyoruz
        ChargingStation chargingStation = ChargingStation.builder()
                .name(dto.name())
                .chargeSpeed(dto.chargeSpeed())
                .locations(validLocations) // Yalnızca geçerli location nesneleri
                .build();

        // ChargingStation'ı repository'ye kaydediyoruz
        chargingStationRepository.save(chargingStation);

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
