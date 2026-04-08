package com.ganesh.expensetracker.service.impl;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.ganesh.expensetracker.service.RedisTokenService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;


/*
/**
 * Service responsible for:
 * 1. Generating JWT tokens
 * 2. Extracting data (username, authorities) from JWT
 * 3. Validating JWT tokens
 *
 * This is the core of your stateless authentication system.
 *
 * A JWT token consists of:
 * - Header  : algorithm and token type
 * - Payload : claims (user information like Username, role, timestamps)
 * - Signature : ensures token integrity and prevents tampering
 *
 *  generateToken() method creates JWT
 *  extractUsername() method extracts email/username  
 *  validateToken() method used to validate the Jwt Token
 *  isTokenExpired() method checks whether the present Token is expired or not.
 *  extractAllClaims() method, extract all claims from Jwt Token.
 *  
 * IMPORTANT:
 * We store ROLE inside token to:
 * 1. Avoid DB hit on every request
 * 2. Enable fast authorization checks

 */
@Service
public class JwtService {
	
	// Secret key used to sign and verify JWT (must be strong and Base64 encoded)
	@Value("${jwt.secret}")
	private String secretKey;

	// Token validity duration (in milliseconds)
	@Value("${jwt.expiration}")
	private Long jwtExpiration;
	
	
	 /**
     * Generates a JWT token for the authenticated user.
     *
     * What goes inside the token:
     * - subject (username/email)
     * - issued time
     * - expiration time
     * - authorities (roles + permissions)
     */
	public String generateToken(UserDetails userDetails) {
		
		// Claims = custom data you want to store inside JWT
		Map<String,Object> claims = new HashMap<String, Object>();
		
		// Extract ALL authorities (both ROLE_* and Permissions)
		List<String> authorities = userDetails.getAuthorities()
				.stream()
				.map(a-> a.getAuthority()) // Convert GrantedAuthority -> String
				.collect(Collectors.toList());
		
		// Store authorities inside token payload
		claims.put("authorities", authorities);
		
		return Jwts.builder()
				.claims(claims)  // attach custom claims
				.subject(userDetails.getUsername()) // Username (Email) stored as subject
				.issuedAt(new Date(System.currentTimeMillis())) // Token creation time
				.expiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Token Expiration time
				.signWith(getSigningKey()) // Sign token with secret key
				.compact(); // generate final token string
	}
	
	
	 /**
     * Extracts authorities (roles + permissions) from JWT.
     *
     * @return raw List (type safety issue — should be handled carefully)
     */
	public List<String> extractAuthorities(String token){
		
		Claims claims = extractAllClaims(token);
		
		Object authoritiesObj = claims.get("authorities");
		
		if(!(authoritiesObj instanceof List<?> list)) {
			throw new IllegalStateException("Invalid authorities format in token");
		}
		   return list.stream()
		            .map(obj -> {
		                if (!(obj instanceof String)) {
		                    throw new IllegalStateException("Authority is not a String");
		                }
		                return (String) obj;
		            })
		            .collect(Collectors.toList());
	}
		
	
	
	 /**
     * Extracts username (subject) from JWT.
     */
	public String extractUsername(String token) {
		return extractAllClaims(token).getSubject();
	}
	
    /**
     * Validates token against user details.
     *
     * Checks:
     * 1. Username matches
     * 2. Token is not expired
     *
     * ⚠️ Does NOT check:
     * - token tampering beyond signature
     * - user status (locked/disabled)
     */
	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername())) && !isTokenExpired(token) ;
	}
	
	// Check whether token has expired or not.
	public boolean isTokenExpired(String token) {
		return extractAllClaims(token)
				.getExpiration()
				.before(new Date());
	}

	
	
	/***
     * Parses JWT and extracts all claims.
     *
     * This method also verifies the token signature using the secret key.
     * If signature is invalid → exception is thrown.
     * 
	 *  Claim includes and contains: subject(username), issuedAt, Expiration
	 */
	public Claims extractAllClaims(String token) {
		return Jwts
				.parser()
				.verifyWith((SecretKey)getSigningKey())  // Verify signature
				.build()
				.parseSignedClaims(token)  // Parse token
				.getPayload(); // extract payload (Claims)
	}
	

	/*
	 *  Converts secretkey string into secure signing key. 
	 *  It is used for signing and validating JWT.
	 *  
     *=> Generates signing key from Base64 encoded secret.
     *
     * Secret must be:
     * - sufficiently long (at least 256 bits for HS256)
     * - securely stored (NOT hardcoded in production)
     */
	private Key getSigningKey() {
		return Keys.hmacShaKeyFor(
				Decoders.BASE64.decode(secretKey));
	}


	
}