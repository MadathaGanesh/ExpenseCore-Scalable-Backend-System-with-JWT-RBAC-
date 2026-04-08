package com.ganesh.expensetracker.security;

import java.io.IOException;


import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ganesh.expensetracker.service.RedisTokenService;
import com.ganesh.expensetracker.service.impl.CustomUserDetailsService;
import com.ganesh.expensetracker.service.impl.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * JwtAuthenticationFilter
 * ------------------------
 * This filter is responsible for validating JWT tokens for every incoming request.
 *
 * WHY THIS FILTER EXISTS:
 * - Spring Security does NOT understand JWT by default
 * - This filter plugs JWT authentication into Spring Security flow
 *
 * WHAT THIS FILTER DOES:
 * 1. Intercepts every HTTP request
 * 2. Extracts JWT token from Authorization header
 * 3. Validates token
 * 4. Checks if token is blacklisted (logout support)
 * 5. Loads user from DB
 * 6. Sets authentication in SecurityContext
 *
 * RESULT:
 * - If token is valid → user is authenticated
 * - If invalid → request proceeds without authentication
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter{
	
	private final JwtService jwtService;
	private final CustomUserDetailsService customUserDetailsService;
	private final RedisTokenService redisTokenService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
			
		// Step 1: Extract Authorization Header
		final String authHeader = request.getHeader("Authorization");
		
		// Step 2: Validate Header 
		// Expected : Authorization : Bearer <token>
		if(authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response); // Continue without auth
			return;
		} 
		
		// Step 3: Extract JWT token
		String token = authHeader.substring(7);
		
		// Step 4: Validate Token presence
		if(token==null || token.isBlank() || !token.contains(".")) {
			filterChain.doFilter(request, response);
			return;
		}
		
		
		// Step 5: Extract userName(email) from token		
		try {
			String username = jwtService.extractUsername(token);

			// Step 6: check user is already authenticated or not
			if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				
				// Step 7: Load user from database
				UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
												
				// Step 8: Validate token
				if(jwtService.isTokenValid(token, userDetails)) {
					
					// Step 9:Checking whether the token is blacklisted or not via redis
					if(redisTokenService.isBlackListed(token)) {
						log.warn("BlackListed token used");
						filterChain.doFilter(request, response);
						return;
					}
					
					
					// Step 10: create Authentication object
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
								userDetails,
								null,
								userDetails.getAuthorities()
							);
					
					// Step 10: Attach request Details
					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					
					// Step 12: set authentication (or) authToken in securityContext.
					SecurityContextHolder.getContext().setAuthentication(authToken);
				}
			}
		} catch (Exception ex) {
			log.error("JWT Authentication Error :{} ",ex.getMessage());
		}
		
		// Step 13: Continue the filter chain.
		filterChain.doFilter(request, response);
	}
}
	