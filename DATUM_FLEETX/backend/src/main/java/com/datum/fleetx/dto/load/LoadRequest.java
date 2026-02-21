package com.datum.fleetx.dto.load;

import com.datum.fleetx.entity.Load;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Load Request DTO
 */
@Data
public class LoadRequest {
    private String referenceNumber;
    
    @NotNull(message = "Customer is required")
    private UUID customerId;
    
    private UUID pickupLocationId;
    private ZonedDateTime pickupDateTime;
    
    private UUID deliveryLocationId;
    private ZonedDateTime deliveryDateTime;
    
    private String commodity;
    private Double weight;
    private String weightUnit = "LBS";
    private Double volume;
    private Integer pieces;
    private Integer pallets;
    private Boolean isHazmat = false;
    private Boolean isOverdimensional = false;
    
    private BigDecimal rate;
    private String currency = "USD";
    private BigDecimal fuelSurcharge = BigDecimal.ZERO;
    private BigDecimal accessorials = BigDecimal.ZERO;
    
    private Double distanceMiles;
    private String notes;
    private String specialInstructions;
    
    private UUID truckId;
    private UUID driverId;
}
