package com.datum.fleetx.service;

import com.datum.fleetx.dto.driver.DriverRequest;
import com.datum.fleetx.entity.Company;
import com.datum.fleetx.entity.Driver;
import com.datum.fleetx.exception.ResourceNotFoundException;
import com.datum.fleetx.repository.CompanyRepository;
import com.datum.fleetx.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private CompanyRepository companyRepository;

    public List<Driver> getAllDriversByCompany(UUID companyId) {
        return driverRepository.findByCompanyId(companyId);
    }

    public Driver getDriverById(UUID id, UUID companyId) {
        return driverRepository.findById(id)
            .filter(driver -> driver.getCompany().getId().equals(companyId))
            .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", id));
    }

    public Driver createDriver(DriverRequest request, UUID companyId) {
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new ResourceNotFoundException("Company", "id", companyId));

        Driver driver = new Driver();
        driver.setFirstName(request.getFirstName());
        driver.setLastName(request.getLastName());
        driver.setLicenseNumber(request.getLicenseNumber());
        driver.setLicenseExpiry(request.getLicenseExpiry());
        driver.setLicenseState(request.getLicenseState());
        driver.setLicenseCountry(request.getLicenseCountry());
        driver.setPhoneNumber(request.getPhoneNumber());
        driver.setEmail(request.getEmail());
        driver.setDateOfBirth(request.getDateOfBirth());
        driver.setEmploymentType(request.getEmploymentType());
        driver.setPayType(request.getPayType());
        driver.setPayRate(request.getPayRate());
        driver.setStatus(Driver.DriverStatus.AVAILABLE);
        driver.setHireDate(request.getHireDate());
        driver.setEmergencyContactName(request.getEmergencyContactName());
        driver.setEmergencyContactPhone(request.getEmergencyContactPhone());
        driver.setCompany(company);
        
        return driverRepository.save(driver);
    }

    public Driver updateDriver(UUID id, DriverRequest request, UUID companyId) {
        Driver driver = getDriverById(id, companyId);
        
        if (request.getFirstName() != null) driver.setFirstName(request.getFirstName());
        if (request.getLastName() != null) driver.setLastName(request.getLastName());
        if (request.getLicenseNumber() != null) driver.setLicenseNumber(request.getLicenseNumber());
        if (request.getLicenseExpiry() != null) driver.setLicenseExpiry(request.getLicenseExpiry());
        if (request.getLicenseState() != null) driver.setLicenseState(request.getLicenseState());
        if (request.getLicenseCountry() != null) driver.setLicenseCountry(request.getLicenseCountry());
        if (request.getPhoneNumber() != null) driver.setPhoneNumber(request.getPhoneNumber());
        if (request.getEmail() != null) driver.setEmail(request.getEmail());
        if (request.getDateOfBirth() != null) driver.setDateOfBirth(request.getDateOfBirth());
        if (request.getEmploymentType() != null) driver.setEmploymentType(request.getEmploymentType());
        if (request.getPayType() != null) driver.setPayType(request.getPayType());
        if (request.getPayRate() != null) driver.setPayRate(request.getPayRate());
        if (request.getEmergencyContactName() != null) driver.setEmergencyContactName(request.getEmergencyContactName());
        if (request.getEmergencyContactPhone() != null) driver.setEmergencyContactPhone(request.getEmergencyContactPhone());
        
        return driverRepository.save(driver);
    }

    public void deleteDriver(UUID id, UUID companyId) {
        Driver driver = getDriverById(id, companyId);
        driver.setActive(false);
        driverRepository.save(driver);
    }

    public Driver updateDriverStatus(UUID id, Driver.DriverStatus status, UUID companyId) {
        Driver driver = getDriverById(id, companyId);
        driver.setStatus(status);
        return driverRepository.save(driver);
    }

    public Driver updateDriverLocation(UUID id, Double latitude, Double longitude, UUID companyId) {
        Driver driver = getDriverById(id, companyId);
        driver.setCurrentLatitude(latitude);
        driver.setCurrentLongitude(longitude);
        driver.setLastLocationUpdate(java.time.Instant.now());
        return driverRepository.save(driver);
    }

    public List<Driver> getAvailableDrivers(UUID companyId) {
        return driverRepository.findByCompanyIdAndStatus(companyId, Driver.DriverStatus.AVAILABLE);
    }

    public long getTotalDriversCount(UUID companyId) {
        return driverRepository.countByCompanyId(companyId);
    }

    public long getAvailableDriversCount(UUID companyId) {
        return driverRepository.findByCompanyIdAndStatus(companyId, Driver.DriverStatus.AVAILABLE).size();
    }

    public long getOnDutyDriversCount(UUID companyId) {
        return driverRepository.findByCompanyIdAndStatus(companyId, Driver.DriverStatus.ON_DUTY).size();
    }
}
