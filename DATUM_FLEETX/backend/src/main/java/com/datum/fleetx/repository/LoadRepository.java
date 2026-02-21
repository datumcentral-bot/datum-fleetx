package com.datum.fleetx.repository;

import com.datum.fleetx.entity.Company;
import com.datum.fleetx.entity.Load;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LoadRepository extends JpaRepository<Load, UUID> {
    
    List<Load> findByCompany(Company company);
    
    List<Load> findByCompanyId(UUID companyId);
    
    Page<Load> findByCompanyId(UUID companyId, Pageable pageable);
    
    Optional<Load> findByTrackingToken(String trackingToken);
    
    Optional<Load> findByLoadNumber(String loadNumber);
    
    boolean existsByLoadNumber(String loadNumber);
    
    @Query("SELECT l FROM Load l WHERE l.company.id = :companyId AND l.status = :status")
    List<Load> findByCompanyIdAndStatus(UUID companyId, Load.LoadStatus status);
    
    @Query("SELECT l FROM Load l WHERE l.company.id = :companyId AND l.driver.id = :driverId AND l.status NOT IN ('COMPLETED', 'CANCELLED')")
    List<Load> findActiveByDriverId(UUID companyId, UUID driverId);
    
    @Query("SELECT l FROM Load l WHERE l.company.id = :companyId AND l.truck.id = :truckId AND l.status NOT IN ('COMPLETED', 'CANCELLED')")
    List<Load> findActiveByTruckId(UUID companyId, UUID truckId);
    
    @Query("SELECT l FROM Load l WHERE l.customer.id = :customerId AND l.status NOT IN ('COMPLETED', 'CANCELLED')")
    List<Load> findActiveByCustomerId(UUID customerId);
    
    @Query("SELECT COUNT(l) FROM Load l WHERE l.company.id = :companyId AND l.status = :status")
    Long countByCompanyIdAndStatus(UUID companyId, Load.LoadStatus status);
    
    @Query("SELECT l FROM Load l WHERE l.company.id = :companyId AND l.pickupDateTime BETWEEN :startDate AND :endDate")
    List<Load> findByCompanyIdAndDateRange(UUID companyId, ZonedDateTime startDate, ZonedDateTime endDate);
}
