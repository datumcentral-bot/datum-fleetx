package com.datum.fleetx.controller;

import com.datum.fleetx.dto.ApiResponse;
import com.datum.fleetx.entity.Company;
import com.datum.fleetx.entity.Customer;
import com.datum.fleetx.repository.CompanyRepository;
import com.datum.fleetx.repository.CustomerRepository;
import com.datum.fleetx.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Customer Controller - Customer/Shipper management
 */
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {
    
    private final CustomerRepository customerRepository;
    private final CompanyRepository companyRepository;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Customer>>> getAllCustomers(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<Customer> customers = customerRepository.findByCompanyIdAndActiveTrue(userDetails.getCompanyId());
        return ResponseEntity.ok(ApiResponse.success(customers));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Customer>> getCustomer(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id
    ) {
        return customerRepository.findById(id)
                .filter(customer -> customer.getCompany().getId().equals(userDetails.getCompanyId()))
                .map(customer -> ResponseEntity.ok(ApiResponse.success(customer)))
                .orElse(ResponseEntity.ok(ApiResponse.error("Customer not found")));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<Customer>> createCustomer(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CustomerRequest request
    ) {
        Company company = companyRepository.findById(userDetails.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));
        
        Customer customer = new Customer();
        customer.setCompany(company);
        customer.setCompanyName(request.getCompanyName());
        customer.setContactPerson(request.getContactPerson());
        customer.setEmail(request.getEmail());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setBillingAddress(request.getBillingAddress());
        customer.setCity(request.getCity());
        customer.setStateProvince(request.getStateProvince());
        customer.setPostalCode(request.getPostalCode());
        customer.setCountry(request.getCountry());
        customer.setCreditLimit(request.getCreditLimit() != null ? request.getCreditLimit() : BigDecimal.ZERO);
        customer.setPaymentTerms(request.getPaymentTerms() != null ? request.getPaymentTerms() : 30);
        customer.setTaxId(request.getTaxId());
        customer.setNotes(request.getNotes());
        customer.setTrackingPortalEnabled(request.getTrackingPortalEnabled() != null ? request.getTrackingPortalEnabled() : true);
        
        // Generate tracking token
        if (customer.getTrackingPortalEnabled()) {
            customer.setTrackingToken(UUID.randomUUID().toString());
        }
        
        customer.setActive(true);
        
        customer = customerRepository.save(customer);
        
        return ResponseEntity.ok(ApiResponse.success("Customer created successfully", customer));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Customer>> updateCustomer(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id,
            @RequestBody CustomerRequest request
    ) {
        return customerRepository.findById(id)
                .filter(customer -> customer.getCompany().getId().equals(userDetails.getCompanyId()))
                .map(customer -> {
                    customer.setCompanyName(request.getCompanyName());
                    customer.setContactPerson(request.getContactPerson());
                    customer.setEmail(request.getEmail());
                    customer.setPhoneNumber(request.getPhoneNumber());
                    customer.setBillingAddress(request.getBillingAddress());
                    customer.setCity(request.getCity());
                    customer.setStateProvince(request.getStateProvince());
                    customer.setPostalCode(request.getPostalCode());
                    customer.setCountry(request.getCountry());
                    if (request.getCreditLimit() != null) {
                        customer.setCreditLimit(request.getCreditLimit());
                    }
                    if (request.getPaymentTerms() != null) {
                        customer.setPaymentTerms(request.getPaymentTerms());
                    }
                    customer.setTaxId(request.getTaxId());
                    customer.setNotes(request.getNotes());
                    
                    customer = customerRepository.save(customer);
                    return ResponseEntity.ok(ApiResponse.success("Customer updated successfully", customer));
                })
                .orElse(ResponseEntity.ok(ApiResponse.error("Customer not found")));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id
    ) {
        return customerRepository.findById(id)
                .filter(customer -> customer.getCompany().getId().equals(userDetails.getCompanyId()))
                .map(customer -> {
                    customer.setActive(false);
                    customerRepository.save(customer);
                    return ResponseEntity.ok(ApiResponse.success("Customer deleted successfully", (Void) null));
                })
                .orElse(ResponseEntity.ok(ApiResponse.error("Customer not found")));
    }
    
    public static class CustomerRequest {
        private String companyName;
        private String contactPerson;
        private String email;
        private String phoneNumber;
        private String billingAddress;
        private String city;
        private String stateProvince;
        private String postalCode;
        private String country;
        private BigDecimal creditLimit;
        private Integer paymentTerms;
        private String taxId;
        private String notes;
        private Boolean trackingPortalEnabled;
        
        // Getters and setters
        public String getCompanyName() { return companyName; }
        public void setCompanyName(String companyName) { this.companyName = companyName; }
        public String getContactPerson() { return contactPerson; }
        public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public String getBillingAddress() { return billingAddress; }
        public void setBillingAddress(String billingAddress) { this.billingAddress = billingAddress; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getStateProvince() { return stateProvince; }
        public void setStateProvince(String stateProvince) { this.stateProvince = stateProvince; }
        public String getPostalCode() { return postalCode; }
        public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        public BigDecimal getCreditLimit() { return creditLimit; }
        public void setCreditLimit(BigDecimal creditLimit) { this.creditLimit = creditLimit; }
        public Integer getPaymentTerms() { return paymentTerms; }
        public void setPaymentTerms(Integer paymentTerms) { this.paymentTerms = paymentTerms; }
        public String getTaxId() { return taxId; }
        public void setTaxId(String taxId) { this.taxId = taxId; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
        public Boolean getTrackingPortalEnabled() { return trackingPortalEnabled; }
        public void setTrackingPortalEnabled(Boolean trackingPortalEnabled) { this.trackingPortalEnabled = trackingPortalEnabled; }
    }
}
