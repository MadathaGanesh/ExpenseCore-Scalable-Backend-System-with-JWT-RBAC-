package com.ganesh.expensetracker.mapper;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.ganesh.expensetracker.dto.user.UserRequestDto;
import com.ganesh.expensetracker.dto.user.UserResponseDto;
import com.ganesh.expensetracker.entity.User;

import lombok.AllArgsConstructor;

/***
 *  Mapper for User. Entity <-> DTO
 */

@Component
@AllArgsConstructor
public class UserMapper {
	
	private final PasswordEncoder passwordEncoder;

	
	// Converting RequestDTO -> entity
	public User toEntity(UserRequestDto dto) {
		User user = new User();
		user.setUserName(dto.getUserName());
		user.setEmail(dto.getEmail());
		user.setAge(dto.getAge());
		user.setPassword(passwordEncoder.encode(dto.getPassword()));
		return user;
	}
	
	
	// Converting Entity -> Response DTO
	public UserResponseDto toDto(User userData) {
		UserResponseDto response = new UserResponseDto();		
		response.setUserId(userData.getId());
		response.setUserName(userData.getUserName());
		response.setEmail(userData.getEmail());
		response.setAge(userData.getAge());
		response.setCreatedAt(userData.getCreateAt());
		response.setUpdatedAt(userData.getUpdatedAt());
		
		return response;
	}

}
