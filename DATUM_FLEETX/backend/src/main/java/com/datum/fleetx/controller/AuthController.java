package com.datum.fleetx.controller;

import com.datum.fleetx.dto.ApiResponse;
import com.datum.fleetx.dto.auth.AuthRequest;
import com.datum.fleetx.dto.auth.AuthResponse;
import com.datum.fleetx.dto.auth.RegisterRequest;
import com.datum.fleetx.entity.Company;
import com.datum.fleetx.entity.StaffMember;
import com.datum.fleetx.repository.CompanyRepository;
import com.datum.fleetx.repository.StaffMemberRepository;
import com.datum.fleetx.security.CustomUserDetails;
import com.datum.fleetx.security.JwtService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Map;

/**
 * Authentication Controller - handles login and registration
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final CompanyRepository companyRepository;
    private final StaffMemberRepository staffMemberRepository;
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        // Check if email already exists
        if (staffMemberRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.ok(ApiResponse.error("Email already registered"));
        }
        
        // Create company with trial subscription
        Company company = new Company();
        company.setCompanyName(request.getCompanyName());
        company.setRegistrationNumber(request.getRegistrationNumber());
        company.setTaxNumber(request.getTaxNumber());
        company.setCountry(request.getCountry());
        company.setTimezone(request.getTimezone());
        company.setBaseCurrency(request.getBaseCurrency());
        company.setSubscriptionPlan(Company.SubscriptionPlan.TRIAL);
        company.setTrialEndDate(LocalDate.now().plusDays(30));
        company.setTruckLimit(5);
        company.setActive(true);
        company = companyRepository.save(company);
        
        // Create admin user
        StaffMember admin = new StaffMember();
        admin.setCompany(company);
        admin.setFirstName(request.getFirstName());
        admin.setLastName(request.getLastName());
        admin.setEmail(request.getEmail());
        admin.setPhoneNumber(request.getPhoneNumber());
        admin.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        admin.setRoleType(StaffMember.RoleType.COMPANY_OWNER);
        admin.setActive(true);
        admin = staffMemberRepository.save(admin);
        
        // Generate token
        CustomUserDetails userDetails = new CustomUserDetails(admin);
        String token = jwtService.generateToken(userDetails);
        
        AuthResponse response = AuthResponse.builder()
                .token(token)
                .userId(admin.getId())
                .email(admin.getEmail())
                .firstName(admin.getFirstName())
                .lastName(admin.getLastName())
                .role(admin.getRoleType())
                .companyId(company.getId())
                .companyName(company.getCompanyName())
                .subscriptionPlan(company.getSubscriptionPlan().name())
                .build();
        
        return ResponseEntity.ok(ApiResponse.success("Registration successful", response));
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);
        
        AuthResponse response = AuthResponse.builder()
                .token(token)
                .userId(userDetails.getId())
                .email(userDetails.getEmail())
                .firstName(userDetails.getFirstName())
                .lastName(userDetails.getLastName())
                .role(userDetails.getRole())
                .companyId(userDetails.getCompanyId())
                .companyName(userDetails.getCompanyName())
                .subscriptionPlan("ACTIVE")
                .build();
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        if (token != null) {
            String email = jwtService.extractUsername(token);
            StaffMember staffMember = staffMemberRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            CustomUserDetails userDetails = new CustomUserDetails(staffMember);
            String newToken = jwtService.generateToken(userDetails);
            
            AuthResponse response = AuthResponse.builder()
                    .token(newToken)
                    .userId(staffMember.getId())
                    .email(staffMember.getEmail())
                    .firstName(staffMember.getFirstName())
                    .lastName(staffMember.getLastName())
                    .role(staffMember.getRoleType())
                    .companyId(staffMember.getCompany().getId())
                    .companyName(staffMember.getCompany().getCompanyName())
                    .subscriptionPlan(staffMember.getCompany().getSubscriptionPlan().name())
                    .build();
            
            return ResponseEntity.ok(response);
        }
        
        return ResponseEntity.badRequest().build();
    }
}
