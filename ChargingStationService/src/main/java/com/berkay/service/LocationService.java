package com.berkay.service;

import com.berkay.dto.request.LocationSaveRequestDTO;
import com.berkay.dto.response.LocationResponseDTO;
import com.berkay.entity.Location;
import com.berkay.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    public boolean addLocation(LocationSaveRequestDTO dto) {
        try {
            Location location = new Location();
            location.setAddress(dto.address());
            location.setCity(dto.city());
            location.setCountry(dto.country());
            locationRepository.save(location);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<LocationResponseDTO> findAllLocations() {
        List<Location> locations = locationRepository.findAll();
        return locations.stream()
                .map(location -> new LocationResponseDTO(location.getId(), location.getAddress(), location.getCity(), location.getCountry()))
                .collect(Collectors.toList());
    }
}
