package com.datum.fleetx.entity;

import com.datum.fleetx.entity.base.BaseEntity;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Load - Represents a shipment/delivery
 * This is the core entity in the dispatch system
 */
@Entity
@Table(name = "loads")
@Data
@EqualsAndHashCode(callSuper = true)
public class Load extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "truck_id")
    private Truck truck;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver;
    
    @Column(name = "load_number", nullable = false, unique = true)
    private String loadNumber;
    
    @Column(name = "reference_number")
    private String referenceNumber;
    
    // Pickup Location
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pickup_location_id")
    private Location pickupLocation;
    
    @Column(name = "pickup_date_time")
    private ZonedDateTime pickupDateTime;
    
    // Delivery Location
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_location_id")
    private Location deliveryLocation;
    
    @Column(name = "delivery_date_time")
    private ZonedDateTime deliveryDateTime;
    
    // Cargo Details
    @Column(name = "commodity")
    private String commodity;
    
    @Column(name = "weight")
    private Double weight;
    
    @Column(name = "weight_unit")
    private String weightUnit = "LBS";
    
    @Column(name = "volume")
    private Double volume;
    
    @Column(name = "pieces")
    private Integer pieces;
    
    @Column(name = " pallets")
    private Integer pallets;
    
    @Column(name = "is_hazmat")
    private Boolean isHazmat = false;
    
    @Column(name = "is_overdimensional")
    private Boolean isOverdimensional = false;
    
    // Rate & Payment
    @Column(name = "rate", precision = 12, scale = 2)
    private BigDecimal rate;
    
    @Column(name = "currency", nullable = false)
    private String currency = "USD";
    
    @Column(name = "fuel_surcharge", precision = 12, scale = 2)
    private BigDecimal fuelSurcharge = BigDecimal.ZERO;
    
    @Column(name = "accessorials", precision = 12, scale = 2)
    private BigDecimal accessorials = BigDecimal.ZERO;
    
    @Column(name = "total_rate", precision = 12, scale = 2)
    private BigDecimal totalRate;
    
    // Distance & Time
    @Column(name = "distance_miles")
    private Double distanceMiles;
    
    @Column(name = "estimated_duration_hours")
    private Double estimatedDurationHours;
    
    @Column(name = "estimated_fuel_cost")
    private BigDecimal estimatedFuelCost;
    
    // Status
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LoadStatus status = LoadStatus.CREATED;
    
    // Tracking
    @Column(name = "tracking_token", unique = true)
    private String trackingToken;
    
    @Column(name = "current_latitude")
    private Double currentLatitude;
    
    @Column(name = "current_longitude")
    private Double currentLongitude;
    
    @Column(name = "last_location_update")
    private Instant lastLocationUpdate;
    
    @Column(name = "estimated_arrival")
    private ZonedDateTime estimatedArrival;
    
    // Documents & Notes
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "special_instructions")
    private String specialInstructions;
    
    // Timestamps
    @Column(name = "dispatched_at")
    private Instant dispatchedAt;
    
    @Column(name = "picked_up_at")
    private Instant pickedUpAt;
    
    @Column(name = "delivered_at")
    private Instant deliveredAt;
    
    @Column(name = "cancelled_at")
    private Instant cancelledAt;
    
    @Column(name = "cancellation_reason")
    private String cancellationReason;
    
    // Documents relationship
    @OneToMany(mappedBy = "load", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Document> documents = new ArrayList<>();
    
    // Tracking events
    @OneToMany(mappedBy = "load", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TrackingEvent> trackingEvents = new ArrayList<>();
    
    public enum LoadStatus {
        CREATED,
        QUOTED,
        BOOKED,
        DISPATCHED,
        EN_ROUTE,
        AT_PICKUP,
        PICKED_UP,
        IN_TRANSIT,
        AT_DELIVERY,
        DELIVERED,
        COMPLETED,
        CANCELLED
    }
    
    public void calculateTotalRate() {
        BigDecimal total = (rate != null ? rate : BigDecimal.ZERO)
            .add(fuelSurcharge != null ? fuelSurcharge : BigDecimal.ZERO)
            .add(accessorials != null ? accessorials : BigDecimal.ZERO);
        this.totalRate = total;
    }
}
