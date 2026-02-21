package com.datum.fleetx.entity;

import com.datum.fleetx.entity.base.BaseEntity;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Driver - Represents a driver in the fleet
 */
@Entity
@Table(name = "drivers")
@Data
@EqualsAndHashCode(callSuper = true)
public class Driver extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
    
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @Column(name = "last_name", nullable = false)
    private String lastName;
    
    @Column(name = "license_number", nullable = false)
    private String licenseNumber;
    
    @Column(name = "license_expiry", nullable = false)
    private LocalDate licenseExpiry;
    
    @Column(name = "license_state")
    private String licenseState;
    
    @Column(name = "license_country")
    private String licenseCountry;
    
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type")
    private EmploymentType employmentType = EmploymentType.FULL_TIME;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "pay_type")
    private PayType payType = PayType.PER_MILE;
    
    @Column(name = "pay_rate")
    private BigDecimal payRate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DriverStatus status = DriverStatus.AVAILABLE;
    
    @Column(name = "hire_date")
    private LocalDate hireDate;
    
    @Column(name = "termination_date")
    private LocalDate terminationDate;
    
    @Column(name = "emergency_contact_name")
    private String emergencyContactName;
    
    @Column(name = "emergency_contact_phone")
    private String emergencyContactPhone;
    
    @Column(name = "current_latitude")
    private Double currentLatitude;
    
    @Column(name = "current_longitude")
    private Double currentLongitude;
    
    @Column(name = "last_location_update")
    private java.time.Instant lastLocationUpdate;
    
    @Column(name = "safety_score")
    private Integer safetyScore = 100;
    
    @Column(name = "total_miles")
    private Double totalMiles = 0.0;
    
    @Column(name = "profile_image_url")
    private String profileImageUrl;
    
    public enum EmploymentType {
        FULL_TIME,
        PART_TIME,
        CONTRACTOR,
        OWNER_OPERATOR
    }
    
    public enum PayType {
        PER_MILE,
        HOURLY,
        PER_LOAD,
        SALARY
    }
    
    public enum DriverStatus {
        AVAILABLE,
        ON_DUTY,
        OFF_DUTY,
        ON_LEAVE,
        TERMINATED
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
