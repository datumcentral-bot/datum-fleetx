package com.datum.fleetx.entity;

import com.datum.fleetx.entity.base.BaseEntity;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * StaffMember - Represents internal users (Dispatchers, Accountants, etc.)
 * Each staff member belongs to a company
 */
@Entity
@Table(name = "staff_members")
@Data
@EqualsAndHashCode(callSuper = true)
public class StaffMember extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
    
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @Column(name = "last_name", nullable = false)
    private String lastName;
    
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role_type", nullable = false)
    private RoleType roleType;
    
    @Column(name = "profile_image_url")
    private String profileImageUrl;
    
    @Column(name = "email_verified")
    private Boolean emailVerified = false;
    
    @Column(name = "last_login_at")
    private java.time.Instant lastLoginAt;
    
    @Column(name = "failed_login_attempts")
    private Integer failedLoginAttempts = 0;
    
    @Column(name = "locked_until")
    private java.time.Instant lockedUntil;
    
    public enum RoleType {
        SUPER_ADMIN,      // Platform admin
        COMPANY_OWNER,    // Company owner
        DISPATCHER,       // Can manage loads and assignments
        ACCOUNTANT,       // Can manage invoices and payments
        DRIVER,           // Driver access (limited)
        CUSTOMER,         // Customer portal access
        VIEWER            // Read-only access
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public boolean isAccountLocked() {
        return lockedUntil != null && java.time.Instant.now().isBefore(lockedUntil);
    }
}
