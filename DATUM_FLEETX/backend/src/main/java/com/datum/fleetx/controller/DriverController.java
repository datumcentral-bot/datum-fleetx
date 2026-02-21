package com.datum.fleetx.controller;

import com.datum.fleetx.dto.ApiResponse;
import com.datum.fleetx.dto.driver.DriverRequest;
import com.datum.fleetx.entity.Company;
import com.datum.fleetx.entity.Driver;
import com.datum.fleetx.repository.CompanyRepository;
import com.datum.fleetx.repository.DriverRepository;
import com.datum.fleetx.security.CustomUserDetails;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Driver Controller - CRUD operations for drivers
 */
@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
public class DriverController {
    
    private final DriverRepository driverRepository;
    private final CompanyRepository companyRepository;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Driver>>> getAllDrivers(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<Driver> drivers = driverRepository.findByCompanyIdAndActiveTrue(userDetails.getCompanyId());
        return ResponseEntity.ok(ApiResponse.success(drivers));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Driver>> getDriver(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id
    ) {
        return driverRepository.findById(id)
                .filter(driver -> driver.getCompany().getId().equals(userDetails.getCompanyId()))
                .map(driver -> ResponseEntity.ok(ApiResponse.success(driver)))
                .orElse(ResponseEntity.ok(ApiResponse.error("Driver not found")));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<Driver>> createDriver(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody DriverRequest request
    ) {
        Company company = companyRepository.findById(userDetails.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));
        
        Driver driver = new Driver();
        driver.setCompany(company);
        driver.setFirstName(request.getFirstName());
        driver.setLastName(request.getLastName());
        driver.setLicenseNumber(request.getLicenseNumber());
        driver.setLicenseExpiry(request.getLicenseExpiry());
        driver.setLicenseState(request.getLicenseState());
        driver.setLicenseCountry(request.getLicenseCountry());
        driver.setPhoneNumber(request.getPhoneNumber());
        driver.setEmail(request.getEmail());
        driver.setDateOfBirth(request.getDateOfBirth());
        driver.setEmploymentType(request.getEmploymentType() != null ? request.getEmploymentType() : Driver.EmploymentType.FULL_TIME);
        driver.setPayType(request.getPayType() != null ? request.getPayType() : Driver.PayType.PER_MILE);
        driver.setPayRate(request.getPayRate());
        driver.setStatus(request.getStatus() != null ? request.getStatus() : Driver.DriverStatus.AVAILABLE);
        driver.setHireDate(request.getHireDate());
        driver.setEmergencyContactName(request.getEmergencyContactName());
        driver.setEmergencyContactPhone(request.getEmergencyContactPhone());
        driver.setActive(true);
        
        driver = driverRepository.save(driver);
        
        return ResponseEntity.ok(ApiResponse.success("Driver created successfully", driver));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Driver>> updateDriver(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id,
            @Valid @RequestBody DriverRequest request
    ) {
        return driverRepository.findById(id)
                .filter(driver -> driver.getCompany().getId().equals(userDetails.getCompanyId()))
                .map(driver -> {
                    driver.setFirstName(request.getFirstName());
                    driver.setLastName(request.getLastName());
                    driver.setLicenseNumber(request.getLicenseNumber());
                    driver.setLicenseExpiry(request.getLicenseExpiry());
                    driver.setLicenseState(request.getLicenseState());
                    driver.setLicenseCountry(request.getLicenseCountry());
                    driver.setPhoneNumber(request.getPhoneNumber());
                    driver.setEmail(request.getEmail());
                    driver.setDateOfBirth(request.getDateOfBirth());
                    if (request.getEmploymentType() != null) {
                        driver.setEmploymentType(request.getEmploymentType());
                    }
                    if (request.getPayType() != null) {
                        driver.setPayType(request.getPayType());
                    }
                    driver.setPayRate(request.getPayRate());
                    if (request.getStatus() != null) {
                        driver.setStatus(request.getStatus());
                    }
                    driver.setHireDate(request.getHireDate());
                    driver.setEmergencyContactName(request.getEmergencyContactName());
                    driver.setEmergencyContactPhone(request.getEmergencyContactPhone());
                    
                    driver = driverRepository.save(driver);
                    return ResponseEntity.ok(ApiResponse.success("Driver updated successfully", driver));
                })
                .orElse(ResponseEntity.ok(ApiResponse.error("Driver not found")));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDriver(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id
    ) {
        return driverRepository.findById(id)
                .filter(driver -> driver.getCompany().getId().equals(userDetails.getCompanyId()))
                .map(driver -> {
                    driver.setActive(false);
                    driverRepository.save(driver);
                    return ResponseEntity.ok(ApiResponse.success("Driver deleted successfully", (Void) null));
                })
                .orElse(ResponseEntity.ok(ApiResponse.error("Driver not found")));
    }
    
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<Driver>>> getAvailableDrivers(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<Driver> drivers = driverRepository.findAvailableByCompanyId(userDetails.getCompanyId());
        return ResponseEntity.ok(ApiResponse.success(drivers));
    }
}
