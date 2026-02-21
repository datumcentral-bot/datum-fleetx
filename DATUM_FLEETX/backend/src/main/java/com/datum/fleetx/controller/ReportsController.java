package com.datum.fleetx.controller;

import com.datum.fleetx.dto.ApiResponse;
import com.datum.fleetx.security.CustomUserDetails;
import com.datum.fleetx.service.ReportsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Map;

/**
 * Reports Controller - Analytics and reporting endpoints
 */
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportsController {

    private final ReportsService reportsService;

    /**
     * Get executive summary dashboard
     */
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getExecutiveSummary(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Map<String, Object> summary = reportsService.getExecutiveSummary(userDetails.getCompanyId());
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    /**
     * Get revenue by truck report
     */
    @GetMapping("/revenue-by-truck")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRevenueByTruck(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        ZonedDateTime start = startDate.atStartOfDay(ZonedDateTime.now().getZone());
        ZonedDateTime end = endDate.plusDays(1).atStartOfDay(ZonedDateTime.now().getZone());
        
        Map<String, Object> report = reportsService.getRevenueByTruck(
            userDetails.getCompanyId(), start, end);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    /**
     * Get revenue by driver report
     */
    @GetMapping("/revenue-by-driver")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRevenueByDriver(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        ZonedDateTime start = startDate.atStartOfDay(ZonedDateTime.now().getZone());
        ZonedDateTime end = endDate.plusDays(1).atStartOfDay(ZonedDateTime.now().getZone());
        
        Map<String, Object> report = reportsService.getRevenueByDriver(
            userDetails.getCompanyId(), start, end);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    /**
     * Get customer profitability report
     */
    @GetMapping("/customer-profitability")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCustomerProfitability(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        ZonedDateTime start = startDate.atStartOfDay(ZonedDateTime.now().getZone());
        ZonedDateTime end = endDate.plusDays(1).atStartOfDay(ZonedDateTime.now().getZone());
        
        Map<String, Object> report = reportsService.getCustomerProfitability(
            userDetails.getCompanyId(), start, end);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    /**
     * Get on-time delivery statistics
     */
    @GetMapping("/on-time-delivery")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOnTimeDeliveryStats(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        ZonedDateTime start = startDate.atStartOfDay(ZonedDateTime.now().getZone());
        ZonedDateTime end = endDate.plusDays(1).atStartOfDay(ZonedDateTime.now().getZone());
        
        Map<String, Object> stats = reportsService.getOnTimeDeliveryStats(
            userDetails.getCompanyId(), start, end);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    /**
     * Get fleet utilization report
     */
    @GetMapping("/fleet-utilization")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFleetUtilization(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        ZonedDateTime start = startDate.atStartOfDay(ZonedDateTime.now().getZone());
        ZonedDateTime end = endDate.plusDays(1).atStartOfDay(ZonedDateTime.now().getZone());
        
        Map<String, Object> report = reportsService.getFleetUtilization(
            userDetails.getCompanyId(), start, end);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    /**
     * Get monthly revenue trend
     */
    @GetMapping("/monthly-trend")
    public ResponseEntity<ApiResponse<Object>> getMonthlyRevenueTrend(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam int year) {
        
        Object trend = reportsService.getMonthlyRevenueTrend(userDetails.getCompanyId(), year);
        return ResponseEntity.ok(ApiResponse.success(trend));
    }
}
