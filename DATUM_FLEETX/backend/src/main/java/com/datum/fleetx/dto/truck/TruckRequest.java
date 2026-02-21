package com.datum.fleetx.dto.truck;

import com.datum.fleetx.entity.Truck;
import javax.validation.constraints.*;
import lombok.Data;

/**
 * Truck Request DTO with validation
 */
@Data
public class TruckRequest {
    
    @NotBlank(message = "Truck number is required")
    @Size(min = 2, max = 20, message = "Truck number must be between 2 and 20 characters")
    private String truckNumber;
    
    @Size(min = 17, max = 17, message = "VIN must be exactly 17 characters")
    private String vin;
    
    @NotBlank(message = "Make is required")
    @Size(max = 50, message = "Make must not exceed 50 characters")
    private String make;
    
    @NotBlank(message = "Model is required")
    @Size(max = 50, message = "Model must not exceed 50 characters")
    private String model;
    
    @Min(value = 1990, message = "Year must be 1990 or later")
    @Max(value = 2030, message = "Year cannot be in the future")
    private Integer year;
    
    @Size(max = 20, message = "Plate number must not exceed 20 characters")
    private String plateNumber;
    
    @NotNull(message = "Truck type is required")
    private Truck.TruckType truckType;
    
    private Truck.TruckStatus status = Truck.TruckStatus.AVAILABLE;
    
    @Positive(message = "Capacity weight must be positive")
    private Double capacityWeight;
    
    @Positive(message = "Capacity volume must be positive")
    private Double capacityVolume;
    
    @Size(max = 30, message = "Fuel type must not exceed 30 characters")
    private String fuelType;
    
    @Positive(message = "Fuel efficiency must be positive")
    private Double fuelEfficiency;
    
    @Positive(message = "Current mileage must be positive")
    private Double currentMileage;
    
    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;
}
