package com.datum.fleetx.repository;

import com.datum.fleetx.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LocationRepository extends JpaRepository<Location, UUID> {
    
    List<Location> findByCity(String city);
    
    List<Location> findByCountry(String country);
    
    @Query("SELECT l FROM Location l WHERE LOWER(l.city) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(l.addressLine1) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Location> searchLocations(String query);
    
    @Query("SELECT l FROM Location l WHERE l.latitude IS NOT NULL AND l.longitude IS NOT NULL")
    List<Location> findAllWithCoordinates();
}
