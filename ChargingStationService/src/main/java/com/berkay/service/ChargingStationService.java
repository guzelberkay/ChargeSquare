package com.berkay.service;

import com.berkay.dto.request.ChargingStationCreateRequestDTO;
import com.berkay.dto.request.LocationSaveRequestDTO;
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

import java.util.*;


@Service
@RequiredArgsConstructor
@EnableKafka
public class ChargingStationService {

    private final ChargingStationRepository chargingStationRepository;
    private final LocationRepository locationRepository;



    @Transactional
    public boolean createChargingStation(ConsumerCreateChargeStation dto) {
        ChargingStation existingStation = chargingStationRepository.findByName(dto.getName())
                .orElse(null); // Burada Optional yerine null döndürüyoruz.

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
        // Veritabanından sonuçları alıyoruz
        List<Object[]> rawResults = locationRepository.findStationLocationDetailsByStationId(stationId);

        // Eğer sonuçlar boşsa, bir hata fırlatıyoruz
        if (rawResults.isEmpty()) {
            // Detaylı hata mesajı veriyoruz
            throw new IllegalArgumentException("No Charging Station found with ID: " + stationId + ". Please check if the ID exists in the database.");
        }

        // Station bilgilerini tutacak değişkenler
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

        // Tüm bilgileri içeren ChargingStationLocationDTO döndürüyoruz
        return new ChargingStationLocationDTO(stationId, stationName, chargeSpeed, locations);
    }






    @Cacheable(value = "station-find-all-case")
    public List<ChargingStationLocationDTO> findAll() {
        // Repository'den ham sonuçları çekiyoruz
        List<Object[]> rawResults = locationRepository.findAllStationLocationDetails();
        System.out.println(rawResults);
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
            stationDTO.getLocations().add(new LocationResponseDTO(locationId, locationCountry, locationCity, locationAddress));
        }

        // stationMap'teki tüm stationları döndürüyoruz
        return new ArrayList<>(stationMap.values());
    }


}
