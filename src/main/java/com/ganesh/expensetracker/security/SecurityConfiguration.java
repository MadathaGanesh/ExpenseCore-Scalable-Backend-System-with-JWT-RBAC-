package com.ganesh.expensetracker.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ganesh.expensetracker.exception.CustomAuthenticationException;
import com.ganesh.expensetracker.exception.CustomAuthorizationException;
import com.ganesh.expensetracker.service.impl.CustomUserDetailsService;

import lombok.AllArgsConstructor;

//import static org.springframework.security.config.Customizer.withDefaults;

/*
 * SecurityConfiguration
 * ----------------------
 * Central configuration for Spring Security.
 *
 * RESPONSIBILITIES:
 * 1. Define security rules (who can access what)
 * 2. Register JWT filter
 * 3. Disable session (stateless JWT)
 * 4. Enable method-level security (RBAC)
 *
 * CRITICAL:
 * @EnableMethodSecurity → enables @PreAuthorize, @PostAuthorize
 */

@Configuration
@AllArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {
	
	private final CustomUserDetailsService customUserDetailsService;
	
	private final JwtAuthenticationFilter authenticationFilter;
	
	private final CustomAuthenticationException authenticationException;
	
	private final CustomAuthorizationException authorizationException;
	
	/* Configures the spring security filter chain:
	 * 
	 * This method defines:
	 *  - which endpoints require authentication
	 *  - Login Mechanism like httpBasic()
	 *  - Session policy
	 *  - CSRF settings
	 */
	@Bean
	public SecurityFilterChain customSecurityConfiguration(HttpSecurity http) throws Exception{
		return http
				
				// Disable CSRF token because we are using JWT Token (Stateless Authentication)
				.csrf(csrf -> csrf.disable())
				
				// Define Access Rules here (Authorization)
				.authorizeHttpRequests(					
					 auth -> auth
					 .requestMatchers(
							 "/auth/**",         // Public endpoints like login & register (no JWT required)
							 "/swagger-ui/**",   // Swagger UI static resources (Swagger frontend files)
							 "/v3/api-docs/**",  // OpenAPI JSON docs used by Swagger (Swagger Backend Docs)
							 "/swagger-ui.html") // Swagger UI main page
					 .permitAll()
					 
					 .requestMatchers("/admin/**").hasRole("ADMIN")  // Only User with ADMIN Role can access this
					 
					 // Both USER and ADMIN can access below
					 .requestMatchers("/expenses/**").hasAnyRole("USER","ADMIN")
					 .requestMatchers("/categories/**").hasAnyRole("USER","ADMIN")
					 .requestMatchers("/profiles/**").hasAnyRole("USER","ADMIN")
					.anyRequest().authenticated())
				
	            // saying to use our database-backed UserDetailsService
				.userDetailsService(customUserDetailsService)
				
				// Enable Http Basic Authentication
//				.httpBasic(withDefaults())
				
				// Making application stateless (No session created)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				
				// Add JWT filter before UsernamePasswordAuthenticationFilter
				.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
				
				// Global Exception Handling for Authentication and Authorization
				.exceptionHandling(ex -> ex
						.authenticationEntryPoint(authenticationException)
						.accessDeniedHandler(authorizationException))
				
				// Build security Filterchain
				.build();
	}
	
	
	/**
     * Password Encoder Bean
     * 
     * BCrypt is recommended by Spring Security because:
     * - hashes passwords securely
     * - includes salt automatically
     * - resistant to brute-force attacks
     */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	
	/**
     * Authentication Manager Bean : This is required because your AuthController uses AuthenticationManager.
     * Used for authenticating user credentials during login
     */
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
		return configuration.getAuthenticationManager();
	}
	
	
}

