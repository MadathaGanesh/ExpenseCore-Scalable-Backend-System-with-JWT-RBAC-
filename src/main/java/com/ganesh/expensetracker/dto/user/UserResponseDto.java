package com.ganesh.expensetracker.dto.user;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/***
 *  DTO for sending User Data to client as response.
 */


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
	
	private Long userId;
	
	private String userName;
	
	private String email;
	
	private Integer age;
	
	private LocalDateTime createdAt;
	
	private LocalDateTime updatedAt;
}
