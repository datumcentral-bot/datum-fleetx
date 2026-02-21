package com.datum.fleetx.repository;

import com.datum.fleetx.entity.Company;
import com.datum.fleetx.entity.StaffMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StaffMemberRepository extends JpaRepository<StaffMember, UUID> {
    
    Optional<StaffMember> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    List<StaffMember> findByCompany(Company company);
    
    List<StaffMember> findByCompanyId(UUID companyId);
    
    List<StaffMember> findByCompanyAndRoleType(Company company, StaffMember.RoleType roleType);
    
    List<StaffMember> findByCompanyIdAndActiveTrue(UUID companyId);
}
