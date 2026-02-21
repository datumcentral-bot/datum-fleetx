package com.datum.fleetx.repository;

import com.datum.fleetx.entity.Company;
import com.datum.fleetx.entity.Truck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TruckRepository extends JpaRepository<Truck, UUID> {
    
    List<Truck> findByCompany(Company company);
    
    List<Truck> findByCompanyId(UUID companyId);
    
    List<Truck> findByCompanyAndStatus(Company company, Truck.TruckStatus status);
    
    List<Truck> findByCompanyIdAndActiveTrue(UUID companyId);
    
    boolean existsByTruckNumberAndCompanyId(String truckNumber, UUID companyId);
    
    boolean existsByVin(String vin);
    
    @Query("SELECT COUNT(t) FROM Truck t WHERE t.company.id = :companyId AND t.active = true")
    Long countActiveByCompanyId(UUID companyId);
    
    @Query("SELECT t FROM Truck t WHERE t.company.id = :companyId AND t.status = 'AVAILABLE' AND t.active = true")
    List<Truck> findAvailableByCompanyId(UUID companyId);
    
    @Query("SELECT t FROM Truck t WHERE t.company.id = :companyId AND t.status = :status AND t.active = true")
    List<Truck> findByCompanyIdAndStatus(UUID companyId, Truck.TruckStatus status);
}
