package com.datum.fleetx.repository;

import com.datum.fleetx.entity.Company;
import com.datum.fleetx.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    
    List<Customer> findByCompany(Company company);
    
    List<Customer> findByCompanyId(UUID companyId);
    
    List<Customer> findByCompanyIdAndActiveTrue(UUID companyId);
    
    Optional<Customer> findByTrackingToken(String trackingToken);
    
    boolean existsByEmailAndCompanyId(String email, UUID companyId);
    
    boolean existsByCompanyNameAndCompanyId(String companyName, UUID companyId);
}
