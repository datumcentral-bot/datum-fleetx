package com.datum.fleetx.entity;

import com.datum.fleetx.entity.base.BaseEntity;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

/**
 * TrackingEvent - Represents GPS location updates for loads
 */
@Entity
@Table(name = "tracking_events")
@Data
@EqualsAndHashCode(callSuper = true)
public class TrackingEvent extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "load_id")
    private Load load;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "truck_id")
    private Truck truck;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver;
    
    @Column(name = "latitude", nullable = false)
    private Double latitude;
    
    @Column(name = "longitude", nullable = false)
    private Double longitude;
    
    @Column(name = "altitude")
    private Double altitude;
    
    @Column(name = "speed")
    private Double speed;
    
    @Column(name = "heading")
    private Double heading;
    
    @Column(name = "accuracy")
    private Double accuracy;
    
    @Column(name = "event_time", nullable = false)
    private Instant eventTime;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type")
    private TrackingEventType eventType;
    
    @Column(name = "address")
    private String address;
    
    @Column(name = "city")
    private String city;
    
    @Column(name = "state")
    private String state;
    
    @Column(name = "country")
    private String country;
    
    @Column(name = "odometer")
    private Double odometer;
    
    @Column(name = "engine_hours")
    private Double engineHours;
    
    @Column(name = "fuel_level")
    private Double fuelLevel;
    
    @Column(name = "driver_phone")
    private String driverPhone;
    
    @Column(name = "device_id")
    private String deviceId;
    
    @Column(name = "device_type")
    private String deviceType;
    
    @Column(name = "raw_data", columnDefinition = "TEXT")
    private String rawData;
    
    public enum TrackingEventType {
        LOCATION_UPDATE,
        ENGINE_START,
        ENGINE_STOP,
        SPEEDING_ALERT,
        GEO_FENCE_ENTER,
        GEO_FENCE_EXIT,
        IDLE_START,
        IDLE_STOP,
        HARD_BRAKE,
        HARD_ACCELERATION,
        LOW_FUEL,
        MAINTENANCE_DUE,
        ERROR_CODE
    }
}
