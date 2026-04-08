package com.ganesh.expensetracker.dto.user;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *  DTO for partial Update (PATCH Method)
 */

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserPatchDto {
	
	private String userName;
	
	@Size(min = 5, message = "password must be atleast 5 characters")
	private String password;
	
	@Email(message = "Invalid Email format")
	private String email;
	
	private Integer age;
	
	
	/***
	 *  Check if all fields are null
	 * 
	 * @return boolean
	 */
	public boolean isPatchEmpty(){
		return userName == null &&
				password == null &&
				email == null &&
				age == null;
	}
}
	
		
