package com.datum.fleetx.dto.auth;

import com.datum.fleetx.entity.StaffMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Authentication Response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private UUID userId;
    private String email;
    private String firstName;
    private String lastName;
    private StaffMember.RoleType role;
    private UUID companyId;
    private String companyName;
    private String subscriptionPlan;
}
