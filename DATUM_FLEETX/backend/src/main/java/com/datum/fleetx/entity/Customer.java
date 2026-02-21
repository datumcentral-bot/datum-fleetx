package com.datum.fleetx.entity;

import com.datum.fleetx.entity.base.BaseEntity;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Customer - Represents a shipper/customer of the dispatch company
 */
@Entity
@Table(name = "customers")
@Data
@EqualsAndHashCode(callSuper = true)
public class Customer extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
    
    @Column(name = "company_name", nullable = false)
    private String companyName;
    
    @Column(name = "contact_person")
    private String contactPerson;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "billing_address")
    private String billingAddress;
    
    @Column(name = "city")
    private String city;
    
    @Column(name = "state_province")
    private String stateProvince;
    
    @Column(name = "postal_code")
    private String postalCode;
    
    @Column(name = "country")
    private String country;
    
    @Column(name = "credit_limit")
    private BigDecimal creditLimit = BigDecimal.ZERO;
    
    @Column(name = "current_balance")
    private BigDecimal currentBalance = BigDecimal.ZERO;
    
    @Column(name = "payment_terms")
    private Integer paymentTerms = 30;
    
    @Column(name = "tax_id")
    private String taxId;
    
    @Column(name = "notes")
    private String notes;
    
    @Column(name = "tracking_portal_enabled")
    private Boolean trackingPortalEnabled = true;
    
    @Column(name = "tracking_token", unique = true)
    private String trackingToken;
    
    @Column(name = "primary_color")
    private String primaryColor;
    
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Load> loads = new ArrayList<>();
}
