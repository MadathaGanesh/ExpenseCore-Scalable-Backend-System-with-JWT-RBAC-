package com.ganesh.expensetracker.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for login request
 */
@Data
public class AuthRequestDto {
	
	@NotBlank(message = "Email is required")
	@Email(message = "Invalid Email format")
	private String email;
	
	@NotBlank(message = "Password is required")
	private String password;

}
