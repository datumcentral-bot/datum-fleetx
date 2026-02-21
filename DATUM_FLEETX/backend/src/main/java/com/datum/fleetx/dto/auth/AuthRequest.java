package com.datum.fleetx.dto.auth;

import lombok.Data;

/**
 * Authentication Request DTO
 */
@Data
public class AuthRequest {
    private String email;
    private String password;
}
