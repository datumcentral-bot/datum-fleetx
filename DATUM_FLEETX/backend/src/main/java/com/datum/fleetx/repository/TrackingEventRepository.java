package com.datum.fleetx.repository;

import com.datum.fleetx.entity.TrackingEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface TrackingEventRepository extends JpaRepository<TrackingEvent, UUID> {
    
    List<TrackingEvent> findByLoadId(UUID loadId);
    
    Page<TrackingEvent> findByLoadId(UUID loadId, Pageable pageable);
    
    List<TrackingEvent> findByTruckId(UUID truckId);
    
    List<TrackingEvent> findByDriverId(UUID driverId);
    
    @Query("SELECT t FROM TrackingEvent t WHERE t.load.id = :loadId ORDER BY t.eventTime DESC")
    List<TrackingEvent> findLatestByLoadId(UUID loadId);
    
    @Query("SELECT t FROM TrackingEvent t WHERE t.truck.id = :truckId AND t.eventTime > :since ORDER BY t.eventTime DESC")
    List<TrackingEvent> findRecentByTruckId(UUID truckId, Instant since);
    
    @Query("SELECT t FROM TrackingEvent t WHERE t.load.id = :loadId AND t.eventTime > :since ORDER BY t.eventTime DESC")
    List<TrackingEvent> findRecentByLoadId(UUID loadId, Instant since);
    
    @Query("SELECT t FROM TrackingEvent t WHERE t.load.id = :loadId ORDER BY t.eventTime DESC LIMIT 1")
    TrackingEvent findLatestLocationByLoadId(UUID loadId);
    
    @Query("SELECT t FROM TrackingEvent t WHERE t.truck.id = :truckId ORDER BY t.eventTime DESC LIMIT 1")
    TrackingEvent findLatestLocationByTruckId(UUID truckId);
}
