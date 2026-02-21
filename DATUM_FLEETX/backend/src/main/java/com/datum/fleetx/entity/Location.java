package com.datum.fleetx.entity;

import com.datum.fleetx.entity.base.BaseEntity;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Location - Represents a pickup or delivery location
 */
@Entity
@Table(name = "locations")
@Data
@EqualsAndHashCode(callSuper = true)
public class Location extends BaseEntity {
    
    @Column(name = "address_line_1")
    private String addressLine1;
    
    @Column(name = "address_line_2")
    private String addressLine2;
    
    @Column(name = "city", nullable = false)
    private String city;
    
    @Column(name = "state_province")
    private String stateProvince;
    
    @Column(name = "postal_code")
    private String postalCode;
    
    @Column(name = "country", nullable = false)
    private String country;
    
    @Column(name = "latitude")
    private Double latitude;
    
    @Column(name = "longitude")
    private Double longitude;
    
    @Column(name = "timezone")
    private String timezone;
    
    @Column(name = "location_name")
    private String locationName;
    
    @Column(name = "contact_person")
    private String contactPerson;
    
    @Column(name = "contact_phone")
    private String contactPhone;
    
    @Column(name = "instructions")
    private String instructions;
    
    @Column(name = "is_warehouse")
    private Boolean isWarehouse = false;
    
    @Column(name = "opening_hours")
    private String openingHours;
}
