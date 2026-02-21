package com.datum.fleetx.repository;

import com.datum.fleetx.entity.Company;
import com.datum.fleetx.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DriverRepository extends JpaRepository<Driver, UUID> {
    
    List<Driver> findByCompany(Company company);
    
    List<Driver> findByCompanyId(UUID companyId);
    
    List<Driver> findByCompanyAndStatus(Company company, Driver.DriverStatus status);
    
    List<Driver> findByCompanyIdAndActiveTrue(UUID companyId);
    
    List<Driver> findByCompanyIdAndStatus(UUID companyId, Driver.DriverStatus status);
    
    boolean existsByLicenseNumberAndCompanyId(String licenseNumber, UUID companyId);
    
    @Query("SELECT d FROM Driver d WHERE d.company.id = :companyId AND d.status = 'AVAILABLE' AND d.active = true")
    List<Driver> findAvailableByCompanyId(UUID companyId);
    
    @Query("SELECT COUNT(d) FROM Driver d WHERE d.company.id = :companyId AND d.active = true")
    Long countActiveByCompanyId(UUID companyId);
    
    @Query("SELECT COUNT(d) FROM Driver d WHERE d.company.id = :companyId AND d.active = true")
    Long countByCompanyId(UUID companyId);
}
