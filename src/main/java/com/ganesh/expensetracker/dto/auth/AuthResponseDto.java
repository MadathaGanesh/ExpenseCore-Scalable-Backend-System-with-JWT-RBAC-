package com.ganesh.expensetracker.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *  DTO for returning JWT Token after login
 * */
@Getter
@AllArgsConstructor
public class AuthResponseDto {
	
	private String jwtToken;
	
	private String refreshToken;

}



