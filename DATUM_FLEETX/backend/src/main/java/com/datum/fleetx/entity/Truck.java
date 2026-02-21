package com.datum.fleetx.entity;

import com.datum.fleetx.entity.base.BaseEntity;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Truck - Represents a truck/vehicle in the fleet
 */
@Entity
@Table(name = "trucks")
@Data
@EqualsAndHashCode(callSuper = true)
public class Truck extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
    
    @Column(name = "truck_number", nullable = false)
    private String truckNumber;
    
    @Column(name = "vin", unique = true)
    private String vin;
    
    @Column(name = "make")
    private String make;
    
    @Column(name = "model")
    private String model;
    
    @Column(name = "year")
    private Integer year;
    
    @Column(name = "plate_number")
    private String plateNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "truck_type", nullable = false)
    private TruckType truckType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TruckStatus status = TruckStatus.AVAILABLE;
    
    @Column(name = "capacity_weight")
    private Double capacityWeight;
    
    @Column(name = "capacity_volume")
    private Double capacityVolume;
    
    @Column(name = "fuel_type")
    private String fuelType;
    
    @Column(name = "fuel_efficiency")
    private Double fuelEfficiency;
    
    @Column(name = "current_mileage")
    private Double currentMileage;
    
    @Column(name = "insurance_expiry")
    private java.time.LocalDate insuranceExpiry;
    
    @Column(name = "registration_expiry")
    private java.time.LocalDate registrationExpiry;
    
    @Column(name = "last_maintenance_date")
    private java.time.LocalDate lastMaintenanceDate;
    
    @Column(name = "next_maintenance_date")
    private java.time.LocalDate nextMaintenanceDate;
    
    @Column(name = "notes")
    private String notes;
    
    @Column(name = "current_latitude")
    private Double currentLatitude;
    
    @Column(name = "current_longitude")
    private Double currentLongitude;
    
    @Column(name = "last_location_update")
    private java.time.Instant lastLocationUpdate;
    
    public enum TruckType {
        DRY_VAN,
        REEFER,
        FLATBED,
        TANKER,
        BOX_TRUCK,
        SPRINTER_VAN,
        LIMOUSINE,
        CAR_CARRIER
    }
    
    public enum TruckStatus {
        AVAILABLE,
        ASSIGNED,
        IN_TRANSIT,
        MAINTENANCE,
        OUT_OF_SERVICE
    }
}
