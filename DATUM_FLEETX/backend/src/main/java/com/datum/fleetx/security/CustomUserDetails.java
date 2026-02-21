package com.datum.fleetx.security;

import com.datum.fleetx.entity.StaffMember;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Custom UserDetails implementation for Spring Security
 */
@Getter
public class CustomUserDetails implements UserDetails {
    
    private final UUID id;
    private final String email;
    private final String password;
    private final String firstName;
    private final String lastName;
    private final String companyName;
    private final StaffMember.RoleType role;
    private final UUID companyId;
    private final boolean active;
    
    public CustomUserDetails(StaffMember staffMember) {
        this.id = staffMember.getId();
        this.email = staffMember.getEmail();
        this.password = staffMember.getPasswordHash();
        this.firstName = staffMember.getFirstName();
        this.lastName = staffMember.getLastName();
        this.companyName = staffMember.getCompany().getCompanyName();
        this.role = staffMember.getRoleType();
        this.companyId = staffMember.getCompany().getId();
        this.active = staffMember.getActive();
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    
    @Override
    public String getPassword() {
        return password;
    }
    
    @Override
    public String getUsername() {
        return email;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return active;
    }
}
