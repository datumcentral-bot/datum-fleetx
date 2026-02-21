package com.datum.fleetx.controller;

import com.datum.fleetx.dto.ApiResponse;
import com.datum.fleetx.entity.Customer;
import com.datum.fleetx.entity.Load;
import com.datum.fleetx.entity.Truck;
import com.datum.fleetx.entity.Driver;
import com.datum.fleetx.repository.CustomerRepository;
import com.datum.fleetx.repository.LoadRepository;
import com.datum.fleetx.repository.TruckRepository;
import com.datum.fleetx.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Public tracking controller for customer tracking portal
 * No authentication required - customers can track their shipments using a tracking code
 */
@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class PublicTrackingController {

    private final LoadRepository loadRepository;
    private final CustomerRepository customerRepository;
    private final TruckRepository truckRepository;
    private final DriverRepository driverRepository;

    /**
     * Track a shipment by load number or tracking code
     */
    @GetMapping("/track/{trackingCode}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> trackShipment(
            @PathVariable String trackingCode) {
        
        // Try to find by load number
        Optional<Load> loadOpt = loadRepository.findByLoadNumber(trackingCode);
        
        if (loadOpt.isEmpty()) {
            // Try by ID
            try {
                UUID loadId = UUID.fromString(trackingCode);
                loadOpt = loadRepository.findById(loadId);
            } catch (IllegalArgumentException e) {
                // Invalid UUID format
            }
        }
        
        if (loadOpt.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.error("Tracking code not found"));
        }
        
        Load load = loadOpt.get();
        
        // Build tracking response
        Map<String, Object> tracking = new HashMap<>();
        tracking.put("loadNumber", load.getLoadNumber());
        tracking.put("referenceNumber", load.getReferenceNumber());
        tracking.put("status", load.getStatus());
        tracking.put("pickupLocation", load.getPickupLocation());
        tracking.put("deliveryLocation", load.getDeliveryLocation());
        tracking.put("pickupDate", load.getPickupDateTime());
        tracking.put("deliveryDate", load.getDeliveryDateTime());
        tracking.put("estimatedArrival", load.getEstimatedArrival());
        
        // Get customer info
        if (load.getCustomer() != null) {
            tracking.put("customerName", load.getCustomer().getCompanyName());
        }
        
        // Get truck info if assigned
        if (load.getTruck() != null) {
            Truck truck = load.getTruck();
            Map<String, Object> truckInfo = new HashMap<>();
            truckInfo.put("truckNumber", truck.getTruckNumber());
            truckInfo.put("currentLatitude", truck.getCurrentLatitude());
            truckInfo.put("currentLongitude", truck.getCurrentLongitude());
            truckInfo.put("lastUpdate", truck.getLastLocationUpdate());
            tracking.put("truck", truckInfo);
        }
        
        // Get driver info if assigned
        if (load.getDriver() != null) {
            Driver driver = load.getDriver();
            Map<String, Object> driverInfo = new HashMap<>();
            driverInfo.put("name", driver.getFullName());
            driverInfo.put("phone", driver.getPhoneNumber());
            tracking.put("driver", driverInfo);
        }
        
        // Calculate progress percentage
        int progress = calculateProgress(load);
        tracking.put("progress", progress);
        
        return ResponseEntity.ok(ApiResponse.success(tracking));
    }

    /**
     * Get estimated arrival based on current status
     */
    @GetMapping("/track/{trackingCode}/eta")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getETA(
            @PathVariable String trackingCode) {
        
        Optional<Load> loadOpt = loadRepository.findByLoadNumber(trackingCode);
        
        if (loadOpt.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.error("Tracking code not found"));
        }
        
        Load load = loadOpt.get();
        
        Map<String, Object> eta = new HashMap<>();
        eta.put("loadNumber", load.getLoadNumber());
        eta.put("estimatedArrival", load.getEstimatedArrival());
        eta.put("status", load.getStatus());
        
        if (load.getTruck() != null) {
            eta.put("currentLocation", Map.of(
                "lat", load.getTruck().getCurrentLatitude(),
                "lng", load.getTruck().getCurrentLongitude()
            ));
        }
        
        return ResponseEntity.ok(ApiResponse.success(eta));
    }

    /**
     * Verify customer access to tracking
     */
    @PostMapping("/track/verify")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifyTracking(
            @RequestParam String trackingCode,
            @RequestParam String email) {
        
        Optional<Load> loadOpt = loadRepository.findByLoadNumber(trackingCode);
        
        if (loadOpt.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.error("Invalid tracking code"));
        }
        
        Load load = loadOpt.get();
        
        // Check if email matches
        if (load.getCustomer() != null && 
            load.getCustomer().getEmail() != null &&
            load.getCustomer().getEmail().equalsIgnoreCase(email)) {
            
            Map<String, Object> result = new HashMap<>();
            result.put("verified", true);
            result.put("loadNumber", load.getLoadNumber());
            
            return ResponseEntity.ok(ApiResponse.success(result));
        }
        
        return ResponseEntity.ok(ApiResponse.error("Email does not match our records"));
    }

    private int calculateProgress(Load load) {
        if (load.getStatus() == null) return 0;
        
        String status = load.getStatus().name();
        switch (status) {
            case "CREATED": return 10;
            case "DISPATCHED": return 25;
            case "ASSIGNED": return 40;
            case "PICKED_UP": return 55;
            case "IN_TRANSIT": return 75;
            case "DELIVERED": return 100;
            case "CANCELLED": return 0;
            default: return 50;
        }
    }
}
