package com.datum.fleetx.controller;

import com.datum.fleetx.dto.ApiResponse;
import com.datum.fleetx.dto.load.LoadRequest;
import com.datum.fleetx.entity.*;
import com.datum.fleetx.repository.*;
import com.datum.fleetx.security.CustomUserDetails;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Load Controller - Core dispatch operations
 */
@RestController
@RequestMapping("/api/v1/loads")
@RequiredArgsConstructor
public class LoadController {
    
    private final LoadRepository loadRepository;
    private final CompanyRepository companyRepository;
    private final CustomerRepository customerRepository;
    private final TruckRepository truckRepository;
    private final DriverRepository driverRepository;
    private final LocationRepository locationRepository;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Load>>> getAllLoads(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<Load> loads = loadRepository.findByCompanyId(userDetails.getCompanyId());
        return ResponseEntity.ok(ApiResponse.success(loads));
    }
    
    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<Page<Load>>> getLoadsPaged(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable
    ) {
        Page<Load> loads = loadRepository.findByCompanyId(userDetails.getCompanyId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(loads));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Load>> getLoad(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id
    ) {
        return loadRepository.findById(id)
                .filter(load -> load.getCompany().getId().equals(userDetails.getCompanyId()))
                .map(load -> ResponseEntity.ok(ApiResponse.success(load)))
                .orElse(ResponseEntity.ok(ApiResponse.error("Load not found")));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<Load>> createLoad(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody LoadRequest request
    ) {
        Company company = companyRepository.findById(userDetails.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));
        
        Load load = new Load();
        load.setCompany(company);
        load.setLoadNumber(generateLoadNumber(company.getId()));
        load.setReferenceNumber(request.getReferenceNumber());
        
        // Set customer
        if (request.getCustomerId() != null) {
            Customer customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
            load.setCustomer(customer);
        }
        
        // Set pickup location
        if (request.getPickupLocationId() != null) {
            Location pickup = locationRepository.findById(request.getPickupLocationId())
                    .orElseThrow(() -> new RuntimeException("Pickup location not found"));
            load.setPickupLocation(pickup);
        }
        
        // Set delivery location
        if (request.getDeliveryLocationId() != null) {
            Location delivery = locationRepository.findById(request.getDeliveryLocationId())
                    .orElseThrow(() -> new RuntimeException("Delivery location not found"));
            load.setDeliveryLocation(delivery);
        }
        
        load.setPickupDateTime(request.getPickupDateTime());
        load.setDeliveryDateTime(request.getDeliveryDateTime());
        
        load.setCommodity(request.getCommodity());
        load.setWeight(request.getWeight());
        load.setWeightUnit(request.getWeightUnit());
        load.setVolume(request.getVolume());
        load.setPieces(request.getPieces());
        load.setPallets(request.getPallets());
        load.setIsHazmat(request.getIsHazmat());
        load.setIsOverdimensional(request.getIsOverdimensional());
        
        load.setRate(request.getRate());
        load.setCurrency(request.getCurrency());
        load.setFuelSurcharge(request.getFuelSurcharge());
        load.setAccessorials(request.getAccessorials());
        load.calculateTotalRate();
        
        load.setDistanceMiles(request.getDistanceMiles());
        load.setNotes(request.getNotes());
        load.setSpecialInstructions(request.getSpecialInstructions());
        
        // Generate tracking token
        load.setTrackingToken(UUID.randomUUID().toString());
        
        load.setStatus(Load.LoadStatus.CREATED);
        load.setActive(true);
        
        load = loadRepository.save(load);
        
        return ResponseEntity.ok(ApiResponse.success("Load created successfully", load));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Load>> updateLoad(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id,
            @Valid @RequestBody LoadRequest request
    ) {
        return loadRepository.findById(id)
                .filter(load -> load.getCompany().getId().equals(userDetails.getCompanyId()))
                .map(load -> {
                    // Update load details
                    load.setReferenceNumber(request.getReferenceNumber());
                    
                    if (request.getPickupDateTime() != null) {
                        load.setPickupDateTime(request.getPickupDateTime());
                    }
                    if (request.getDeliveryDateTime() != null) {
                        load.setDeliveryDateTime(request.getDeliveryDateTime());
                    }
                    
                    load.setCommodity(request.getCommodity());
                    load.setWeight(request.getWeight());
                    load.setVolume(request.getVolume());
                    load.setPieces(request.getPieces());
                    load.setPallets(request.getPallets());
                    load.setIsHazmat(request.getIsHazmat());
                    load.setIsOverdimensional(request.getIsOverdimensional());
                    
                    load.setRate(request.getRate());
                    load.setFuelSurcharge(request.getFuelSurcharge());
                    load.setAccessorials(request.getAccessorials());
                    load.calculateTotalRate();
                    
                    load.setDistanceMiles(request.getDistanceMiles());
                    load.setNotes(request.getNotes());
                    load.setSpecialInstructions(request.getSpecialInstructions());
                    
                    load = loadRepository.save(load);
                    return ResponseEntity.ok(ApiResponse.success("Load updated successfully", load));
                })
                .orElse(ResponseEntity.ok(ApiResponse.error("Load not found")));
    }
    
    @PostMapping("/{id}/dispatch")
    public ResponseEntity<ApiResponse<Load>> dispatchLoad(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id,
            @RequestBody DispatchRequest request
    ) {
        return loadRepository.findById(id)
                .filter(load -> load.getCompany().getId().equals(userDetails.getCompanyId()))
                .map(load -> {
                    if (request.getTruckId() != null) {
                        Truck truck = truckRepository.findById(request.getTruckId())
                                .orElseThrow(() -> new RuntimeException("Truck not found"));
                        load.setTruck(truck);
                        truck.setStatus(Truck.TruckStatus.ASSIGNED);
                        truckRepository.save(truck);
                    }
                    
                    if (request.getDriverId() != null) {
                        Driver driver = driverRepository.findById(request.getDriverId())
                                .orElseThrow(() -> new RuntimeException("Driver not found"));
                        load.setDriver(driver);
                        driver.setStatus(Driver.DriverStatus.ON_DUTY);
                        driverRepository.save(driver);
                    }
                    
                    load.setStatus(Load.LoadStatus.DISPATCHED);
                    load.setDispatchedAt(Instant.now());
                    
                    load = loadRepository.save(load);
                    return ResponseEntity.ok(ApiResponse.success("Load dispatched successfully", load));
                })
                .orElse(ResponseEntity.ok(ApiResponse.error("Load not found")));
    }
    
    @PostMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Load>> updateStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id,
            @RequestBody StatusUpdateRequest request
    ) {
        return loadRepository.findById(id)
                .filter(load -> load.getCompany().getId().equals(userDetails.getCompanyId()))
                .map(load -> {
                    load.setStatus(request.getStatus());
                    
                    if (request.getStatus() == Load.LoadStatus.PICKED_UP) {
                        load.setPickedUpAt(Instant.now());
                    } else if (request.getStatus() == Load.LoadStatus.DELIVERED) {
                        load.setDeliveredAt(Instant.now());
                    } else if (request.getStatus() == Load.LoadStatus.CANCELLED) {
                        load.setCancelledAt(Instant.now());
                        load.setCancellationReason(request.getReason());
                    }
                    
                    // Free up truck and driver if completed/cancelled
                    if (request.getStatus() == Load.LoadStatus.COMPLETED || 
                        request.getStatus() == Load.LoadStatus.CANCELLED) {
                        if (load.getTruck() != null) {
                            load.getTruck().setStatus(Truck.TruckStatus.AVAILABLE);
                            truckRepository.save(load.getTruck());
                        }
                        if (load.getDriver() != null) {
                            load.getDriver().setStatus(Driver.DriverStatus.AVAILABLE);
                            driverRepository.save(load.getDriver());
                        }
                    }
                    
                    load = loadRepository.save(load);
                    return ResponseEntity.ok(ApiResponse.success("Status updated successfully", load));
                })
                .orElse(ResponseEntity.ok(ApiResponse.error("Load not found")));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLoad(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id
    ) {
        return loadRepository.findById(id)
                .filter(load -> load.getCompany().getId().equals(userDetails.getCompanyId()))
                .map(load -> {
                    load.setActive(false);
                    loadRepository.save(load);
                    return ResponseEntity.ok(ApiResponse.success("Load deleted successfully", (Void) null));
                })
                .orElse(ResponseEntity.ok(ApiResponse.error("Load not found")));
    }
    
    private String generateLoadNumber(UUID companyId) {
        return "LD-" + companyId.toString().substring(0, 8).toUpperCase() + "-" + 
               System.currentTimeMillis();
    }
    
    public static class DispatchRequest {
        private UUID truckId;
        private UUID driverId;
        
        public UUID getTruckId() { return truckId; }
        public void setTruckId(UUID truckId) { this.truckId = truckId; }
        public UUID getDriverId() { return driverId; }
        public void setDriverId(UUID driverId) { this.driverId = driverId; }
    }
    
    public static class StatusUpdateRequest {
        private Load.LoadStatus status;
        private String reason;
        
        public Load.LoadStatus getStatus() { return status; }
        public void setStatus(Load.LoadStatus status) { this.status = status; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
}
