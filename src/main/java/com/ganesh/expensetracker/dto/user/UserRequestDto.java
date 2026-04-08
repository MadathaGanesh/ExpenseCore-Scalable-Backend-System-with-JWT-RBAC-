package com.ganesh.expensetracker.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/***
 * DTO for creating or Updating user (Register / PUT Method)
 */

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {

	@NotBlank(message = "UserName should not be blank")
	private String userName;
	
	@Size(min=5, message = "Password must be atleat 5 characters")
	@NotBlank(message = "Password cannot be empty")
	private String password;
	
	@NotBlank(message="Email is required")
	@Email(message = "Invalid Email format")
	private String email;
	
	@Min(value = 1, message = "Age must be greater than 0")
	@Max(value = 120, message = "Age must be less than 120")
	private Integer age;
}




