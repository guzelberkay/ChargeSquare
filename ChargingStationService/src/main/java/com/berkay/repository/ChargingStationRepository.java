package com.berkay.repository;

import com.berkay.entity.ChargingStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChargingStationRepository extends JpaRepository<ChargingStation, Long> {
    @Query("SELECT DISTINCT cs FROM ChargingStation cs LEFT JOIN FETCH cs.locations")
    List<ChargingStation> findAllWithLocations();
    Optional<ChargingStation> findByName(String name);

}
