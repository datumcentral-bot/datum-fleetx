package com.datum.fleetx.entity;

import com.datum.fleetx.entity.base.BaseEntity;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Company - Represents a dispatch company using the platform
 * This is the core of multi-tenancy in Datum FleetX
 */
@Entity
@Table(name = "companies")
@Data
@EqualsAndHashCode(callSuper = true)
public class Company extends BaseEntity {
    
    @Column(name = "company_name", nullable = false)
    private String companyName;
    
    @Column(name = "registration_number", unique = true)
    private String registrationNumber;
    
    @Column(name = "tax_number")
    private String taxNumber;
    
    @Column(name = "country", nullable = false)
    private String country;
    
    @Column(name = "timezone", nullable = false)
    private String timezone = "UTC";
    
    @Column(name = "base_currency", nullable = false)
    private String baseCurrency = "USD";
    
    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_plan")
    private SubscriptionPlan subscriptionPlan = SubscriptionPlan.STARTER;
    
    @Column(name = "subscription_start_date")
    private LocalDate subscriptionStartDate;
    
    @Column(name = "subscription_end_date")
    private LocalDate subscriptionEndDate;
    
    @Column(name = "trial_end_date")
    private LocalDate trialEndDate;
    
    @Column(name = "truck_limit")
    private Integer truckLimit = 5;
    
    @Column(name = "current_truck_count")
    private Integer currentTruckCount = 0;
    
    @Column(name = "billing_email")
    private String billingEmail;
    
    @Column(name = "address")
    private String address;
    
    @Column(name = "city")
    private String city;
    
    @Column(name = "phone")
    private String phone;
    
    @Column(name = "logo_url")
    private String logoUrl;
    
    @Column(name = "primary_color")
    private String primaryColor = "#06b6d4";
    
    @Column(name = "stripe_customer_id")
    private String stripeCustomerId;
    
    // Company relationships
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StaffMember> staffMembers = new ArrayList<>();
    
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Truck> trucks = new ArrayList<>();
    
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Driver> drivers = new ArrayList<>();
    
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Customer> customers = new ArrayList<>();
    
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Load> loads = new ArrayList<>();
    
    public enum SubscriptionPlan {
        STARTER,    // $49/month - up to 5 trucks
        GROWTH,     // $149/month - up to 25 trucks
        ENTERPRISE, // Custom - unlimited trucks
        TRIAL       // 30-day free trial
    }
    
    public boolean isSubscriptionActive() {
        if (subscriptionPlan == SubscriptionPlan.TRIAL && trialEndDate != null) {
            return LocalDate.now().isBefore(trialEndDate);
        }
        if (subscriptionEndDate != null) {
            return LocalDate.now().isBefore(subscriptionEndDate);
        }
        return false;
    }
    
    public boolean canAddTruck() {
        return currentTruckCount < truckLimit;
    }
}
