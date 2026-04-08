package com.ganesh.expensetracker.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ganesh.expensetracker.entity.Permission;
import com.ganesh.expensetracker.entity.Role;
import com.ganesh.expensetracker.entity.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * Custom implementation of Spring Security's UserDetails interface.
 * 
 * This class acts as an adapter between your application's User entity
 * and Spring Security's authentication/authorization system.
 */
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetails implements UserDetails{
	
	// The actual "user entity" fetched from database
	private final User user;	

    /**
     * Converts user's roles and permissions into Spring Security authorities.
     * 
     * Authorities are used by Spring Security to decide what a user can access.
     * 
     * Example:
     * ROLE_ADMIN
     * READ_PRIVILEGE
     * WRITE_PRIVILEGE
     */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		// Using "set Data structure" to avoid duplicate authorities
		Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
		

        // 🚨 Defensive check
        if (user.getRole() == null || user.getRole().isEmpty()) {
            log.warn("User has NO roles assigned: {}", user.getEmail());
            return authorities; // empty → will cause 403 (correct behavior)
        }

        for (Role role : user.getRole()) {

            // ✅ Add ROLE
            if (role.getName() != null && !role.getName().isBlank()) {
//                String roleName = "ROLE_" + role.getName().trim();
            	String roleName = role.getName().trim();
                authorities.add(new SimpleGrantedAuthority(roleName));
            } else {
                log.warn("Role name is null/blank for user: {}", user.getEmail());
            }

            // ✅ Add PERMISSIONS
            if (role.getPermissions() != null) {
                for (Permission permission : role.getPermissions()) {

                    if (permission.getName() != null && !permission.getName().isBlank()) {
                        authorities.add(new SimpleGrantedAuthority(permission.getName().trim()));
                    } else {
                        log.warn("Permission name is null/blank for role: {}", role.getName());
                    }
                }
            }
        }

        // 🔥 Debug (REMOVE in production)
        log.info("Authorities for user {} : {}", user.getEmail(), authorities);

        return authorities;
    }

	/**
     * Returns user's encrypted password (used during authentication)
     */
	@Override
	public String getPassword() {
		return user.getPassword();
	}

    /**
     * Returns the username used for login.
     * In this case, email is used as username.
     */	@Override
	public String getUsername() {
		return user.getEmail();  // login using email
	}
      
    public boolean isAccountNonExpired() {
    	return true;
    }
	
    public boolean isAccountNonLocked() {
    	return true;
    }
    
    public boolean isCredentialsNonExpired() {
    	return true;
    }
    
    public boolean isEnabled() {
    	return true;
    }
}
