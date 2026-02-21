package com.datum.fleetx.repository;

import com.datum.fleetx.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {
    
    Optional<Company> findByRegistrationNumber(String registrationNumber);
    
    Optional<Company> findByTaxNumber(String taxNumber);
    
    boolean existsByRegistrationNumber(String registrationNumber);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT c FROM Company c WHERE c.subscriptionPlan = 'TRIAL' AND c.trialEndDate < CURRENT_DATE")
    java.util.List<Company> findExpiredTrials();
}
