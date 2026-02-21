package com.datum.fleetx.dto.driver;

import com.datum.fleetx.entity.Driver;
import javax.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Driver Request DTO with validation
 */
@Data
public class DriverRequest {
    
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;
    
    @NotBlank(message = "License number is required")
    @Size(min = 5, max = 20, message = "License number must be between 5 and 20 characters")
    private String licenseNumber;
    
    @NotNull(message = "License expiry is required")
    @Future(message = "License expiry must be in the future")
    private LocalDate licenseExpiry;
    
    @Size(max = 50, message = "License state must not exceed 50 characters")
    private String licenseState;
    
    @Size(max = 50, message = "License country must not exceed 50 characters")
    private String licenseCountry;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[+]?[(]?[0-9]{1,4}[)]?[-\\s./0-9]*$", message = "Invalid phone number format")
    private String phoneNumber;
    
    @Email(message = "Invalid email format")
    private String email;
    
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
    
    private Driver.EmploymentType employmentType;
    private Driver.PayType payType;
    
    @Positive(message = "Pay rate must be positive")
    private BigDecimal payRate;
    
    private Driver.DriverStatus status = Driver.DriverStatus.AVAILABLE;
    
    private LocalDate hireDate;
    
    @Size(max = 100, message = "Emergency contact name must not exceed 100 characters")
    private String emergencyContactName;
    
    @Pattern(regexp = "^[+]?[(]?[0-9]{1,4}[)]?[-\\s./0-9]*$", message = "Invalid emergency contact phone format")
    private String emergencyContactPhone;
}
