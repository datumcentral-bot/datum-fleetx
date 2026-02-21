package com.datum.fleetx.controller;

import com.datum.fleetx.dto.ApiResponse;
import com.datum.fleetx.dto.truck.TruckRequest;
import com.datum.fleetx.entity.Truck;
import com.datum.fleetx.security.CustomUserDetails;
import com.datum.fleetx.service.TruckService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Truck Controller - CRUD operations for trucks
 * Now uses TruckService for business logic
 */
@RestController
@RequestMapping("/api/v1/trucks")
@RequiredArgsConstructor
public class TruckController {
    
    private final TruckService truckService;
    
    /**
     * Get all trucks for the company
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Truck>>> getAllTrucks(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<Truck> trucks = truckService.getAllTrucksByCompany(userDetails.getCompanyId());
        return ResponseEntity.ok(ApiResponse.success(trucks));
    }
    
    /**
     * Get a specific truck by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Truck>> getTruck(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id
    ) {
        Truck truck = truckService.getTruckById(id, userDetails.getCompanyId());
        return ResponseEntity.ok(ApiResponse.success(truck));
    }
    
    /**
     * Create a new truck
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Truck>> createTruck(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody TruckRequest request
    ) {
        Truck truck = truckService.createTruck(request, userDetails.getCompanyId());
        return ResponseEntity.ok(ApiResponse.success("Truck created successfully", truck));
    }
    
    /**
     * Update an existing truck
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Truck>> updateTruck(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id,
            @RequestBody TruckRequest request
    ) {
        Truck truck = truckService.updateTruck(id, request, userDetails.getCompanyId());
        return ResponseEntity.ok(ApiResponse.success("Truck updated successfully", truck));
    }
    
    /**
     * Delete a truck (soft delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTruck(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id
    ) {
        truckService.deleteTruck(id, userDetails.getCompanyId());
        return ResponseEntity.ok(ApiResponse.success("Truck deleted successfully", null));
    }
    
    /**
     * Get all available trucks
     */
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<Truck>>> getAvailableTrucks(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<Truck> trucks = truckService.getAvailableTrucks(userDetails.getCompanyId());
        return ResponseEntity.ok(ApiResponse.success(trucks));
    }
    
    /**
     * Update truck status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Truck>> updateTruckStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id,
            @RequestParam Truck.TruckStatus status
    ) {
        Truck truck = truckService.updateTruckStatus(id, status, userDetails.getCompanyId());
        return ResponseEntity.ok(ApiResponse.success("Truck status updated", truck));
    }
    
    /**
     * Update truck location (for GPS tracking)
     */
    @PatchMapping("/{id}/location")
    public ResponseEntity<ApiResponse<Truck>> updateTruckLocation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id,
            @RequestParam Double latitude,
            @RequestParam Double longitude
    ) {
        Truck truck = truckService.updateTruckLocation(id, latitude, longitude, userDetails.getCompanyId());
        return ResponseEntity.ok(ApiResponse.success("Location updated", truck));
    }
}
