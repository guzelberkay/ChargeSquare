package com.berkay.repository;

import com.berkay.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    @Query(value = """
        SELECT 
            station.id AS stationId, 
            station.name AS stationName, 
            station.charge_speed AS chargeSpeed,
            loc.id AS locationId, 
            loc.country AS locationCountry,
            loc.city AS locationCity, 
            loc.address AS locationAddress         
        FROM tbl_locationstations ls
        JOIN tbl_locations loc ON ls.location_id = loc.id
        JOIN tbl_charging_station station ON ls.station_id = station.id
        """, nativeQuery = true)
    List<Object[]> findAllStationLocationDetails();
    @Query(value = """
    SELECT 
        station.id AS stationId, 
        station.name AS stationName, 
        station.charge_speed AS chargeSpeed,
        loc.id AS locationId, 
        loc.country AS locationCountry,
        loc.city AS locationCity, 
        loc.address AS locationAddress         
    FROM tbl_locationstations ls
    JOIN tbl_locations loc ON ls.location_id = loc.id
    JOIN tbl_charging_station station ON ls.station_id = station.id
    WHERE station.id = :stationId
    """, nativeQuery = true)
    List<Object[]> findStationLocationDetailsByStationId(@Param("stationId") Long stationId);


}
