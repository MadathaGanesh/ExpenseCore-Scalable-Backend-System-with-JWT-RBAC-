package com.ganesh.expensetracker.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ganesh.expensetracker.dto.auth.AuthRequestDto;
import com.ganesh.expensetracker.dto.auth.AuthResponseDto;
import com.ganesh.expensetracker.dto.common.ApiResponse;
import com.ganesh.expensetracker.dto.user.UserRequestDto;
import com.ganesh.expensetracker.dto.user.UserResponseDto;
import com.ganesh.expensetracker.entity.RefreshToken;
import com.ganesh.expensetracker.entity.User;
import com.ganesh.expensetracker.security.CustomUserDetails;
import com.ganesh.expensetracker.service.RefreshTokenService;
import com.ganesh.expensetracker.service.UserService;
import com.ganesh.expensetracker.service.impl.JwtService;
import com.ganesh.expensetracker.service.impl.LogoutService;
import com.ganesh.expensetracker.util.ApiResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/* This controller will:
		- Accept login credentials
		- Authenticate user using AuthenticationManager
		- Generate JWT token
		- Return token to client
 */

// Controller responsible for handling authentication requests

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@Slf4j
public class AuthController {

	// AuthenticationManager Interface is used to authenticate user credentials.
	private final AuthenticationManager authenticationManager;
	
	private final JwtService jwtservice;
	private final UserService userService;
	private final LogoutService logoutService;
	private final ApiResponseUtil responseUtil;
	private final RefreshTokenService refreshTokenService;
	

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<AuthResponseDto>> login(@Valid @RequestBody AuthRequestDto authRequest){
		
	log.info("Login attempt for email={}",authRequest.getEmail());
	
	String email = authRequest.getEmail().trim().toLowerCase();
	
	// Autenticating the user credentials.
	Authentication authentication= authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(email, authRequest.getPassword()));
	
	// Load user from Database
		UserDetails userDetails = (UserDetails)authentication.getPrincipal();
	
	// Generate JWT for user
		String jwtToken = jwtservice.generateToken(userDetails);
		
		// Create refresh Token
		String refreshToken = refreshTokenService.createRefreshToken(authRequest.getEmail()).getToken();
		
		log.info("Login successful for email={}",authRequest.getEmail());
	
		return ResponseEntity.ok(responseUtil.success(new AuthResponseDto(jwtToken,refreshToken), "Login successful"));
	}
	

	
	/*
	 * Register API: Creates a new user Account
	 * 
	 * Steps:
	 * 1. Receive user regsitration data from request body.
	 * 2. Validate request using @Valid annotation.
	 * 3. Call service layer to save user data in db.(As service layer internally call userRepo.save() method"
	 * 4. Return created user Details with HTTP 201 status.
	 * */
	@PostMapping("/register")
	public ResponseEntity<ApiResponse<UserResponseDto>> registerUser(@Valid @RequestBody UserRequestDto dto){
		log.info("Registering user email={}",dto.getEmail());
		UserResponseDto user = userService.saveUser(dto);
		return ResponseEntity.status(201).body(responseUtil.success(user, "User registered successfully"));
	}
	
	
	/*
	 *  Logout API Endpoint
	 */
	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<String>> handleLogout(HttpServletRequest request){
		
		// Extract Authorization Header
		String authHeader =  request.getHeader("Authorization");
		
		// Validate Header format
		if(authHeader != null && authHeader.startsWith("Bearer ")) {
			
			// Extract Jwt Token from authHeader
			String jwtToken =authHeader.substring(7);
			
			 if (jwtToken != null && !jwtToken.isBlank() && jwtToken.contains(".")) {
			        logoutService.logout(jwtToken);
			    }
			
			log.info("User logged out ");
		}
		
		return ResponseEntity.ok(responseUtil.success("Logout successful", "Token Invalidated"));
	}

	
	
	@PostMapping("/refreshtoken")
	public ResponseEntity<ApiResponse<AuthResponseDto>> refresh(@RequestBody Map<String, String> request ){
		
		String refreshToken = request.get("refreshToken");
		
		RefreshToken token = refreshTokenService.verifyRefreshToken(refreshToken);
		
		User user = token.getUser();
		
		UserDetails userDetails = new CustomUserDetails(user);
		
		String newAccessToken = jwtservice.generateToken(userDetails);
		
		return ResponseEntity.ok(responseUtil.success(new AuthResponseDto(newAccessToken,refreshToken), "Token refreshed"));
	}
}




