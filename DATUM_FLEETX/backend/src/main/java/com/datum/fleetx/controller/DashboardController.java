package com.datum.fleetx.controller;

import com.datum.fleetx.dto.ApiResponse;
import com.datum.fleetx.entity.Load;
import com.datum.fleetx.repository.*;
import com.datum.fleetx.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Dashboard Controller - Main dashboard statistics
 */
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    
    private final CompanyRepository companyRepository;
    private final TruckRepository truckRepository;
    private final DriverRepository driverRepository;
    private final CustomerRepository customerRepository;
    private final LoadRepository loadRepository;
    private final InvoiceRepository invoiceRepository;
    
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStats(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID companyId = userDetails.getCompanyId();
        
        Map<String, Object> stats = new HashMap<>();
        
        // Company info
        companyRepository.findById(companyId).ifPresent(company -> {
            stats.put("companyName", company.getCompanyName());
            stats.put("subscriptionPlan", company.getSubscriptionPlan().name());
            stats.put("truckLimit", company.getTruckLimit());
            stats.put("currentTruckCount", company.getCurrentTruckCount());
            stats.put("trialEndDate", company.getTrialEndDate());
        });
        
        // Counts
        Long activeTrucks = truckRepository.countActiveByCompanyId(companyId);
        Long activeDrivers = driverRepository.countActiveByCompanyId(companyId);
        Long activeCustomers = customerRepository.findByCompanyIdAndActiveTrue(companyId).stream().count();
        Long totalLoads = loadRepository.findByCompanyId(companyId).stream().count();
        
        stats.put("activeTrucks", activeTrucks);
        stats.put("activeDrivers", activeDrivers);
        stats.put("activeCustomers", activeCustomers);
        stats.put("totalLoads", totalLoads);
        
        // Load status breakdown
        Map<String, Long> loadStatus = new HashMap<>();
        for (Load.LoadStatus status : Load.LoadStatus.values()) {
            Long count = loadRepository.countByCompanyIdAndStatus(companyId, status);
            loadStatus.put(status.name(), count);
        }
        stats.put("loadStatus", loadStatus);
        
        // Financial summary
        BigDecimal outstanding = invoiceRepository.sumOutstanding(companyId);
        stats.put("outstandingAmount", outstanding != null ? outstanding : BigDecimal.ZERO);
        
        // Recent loads
        var recentLoads = loadRepository.findByCompanyId(companyId).stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(5)
                .toList();
        stats.put("recentLoads", recentLoads);
        
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
    
    @GetMapping("/kpis")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getKPIs(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID companyId = userDetails.getCompanyId();
        
        Map<String, Object> kpis = new HashMap<>();
        
        // This would be expanded with more complex queries
        // For now, return basic KPIs
        
        return ResponseEntity.ok(ApiResponse.success(kpis));
    }
}
