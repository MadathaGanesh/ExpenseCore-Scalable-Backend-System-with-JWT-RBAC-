package com.ganesh.expensetracker.service.impl;


import java.util.Date;

import org.springframework.stereotype.Service;

import com.ganesh.expensetracker.service.RedisTokenService;

import lombok.RequiredArgsConstructor;

/**
 *  Service class responsible for handling logout logic.
 *  
 *  Responsibilites:
 *  1) Extract JWT Token expiration
 *  2) Store JWT Token in BlackList
 *  3) Ensure previous/same JWT Token cannot be reused after logout. 
 */

@Service
@RequiredArgsConstructor
public class LogoutService {
	
	private final JwtService jwtService;
	private final RedisTokenService redisTokenService;
	
	/**
	 * Logs out a User by blackListing the JWT Token.
	 * 
	 * Flow:
	 * 1) Extract expiration Date from JWT Token.
	 * 2) Create BlackListedToken Entity
	 * 3) Save JWT Token to Repository
	 */
	
	public void logout(String token) {
	 
	 Date expiry = jwtService.extractAllClaims(token).getExpiration();
	
	 long TTL = expiry.getTime() - System.currentTimeMillis();
	 
	 redisTokenService.blacklistToken(token, TTL);
	 		
	}
	

}
