package com.datum.fleetx.dto.auth;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;

/**
 * Registration Request DTO
 */
@Data
public class RegisterRequest {
    // Company details
    @NotBlank(message = "Company name is required")
    private String companyName;
    
    private String registrationNumber;
    private String taxNumber;
    
    @NotBlank(message = "Country is required")
    private String country;
    
    private String timezone = "UTC";
    private String baseCurrency = "USD";
    
    // Admin user details
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}
