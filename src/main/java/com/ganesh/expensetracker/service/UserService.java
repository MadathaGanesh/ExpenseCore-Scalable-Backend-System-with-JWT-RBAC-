
package com.ganesh.expensetracker.service;

import org.springframework.data.domain.Pageable;

import com.ganesh.expensetracker.dto.common.PageResponse;
import com.ganesh.expensetracker.dto.user.UserPatchDto;
import com.ganesh.expensetracker.dto.user.UserRequestDto;
import com.ganesh.expensetracker.dto.user.UserResponseDto;

public interface UserService {
	
	public UserResponseDto saveUser(UserRequestDto dto);
	
	public UserResponseDto getUserByID();

	public PageResponse<UserResponseDto> getAllUsers( Pageable pageable);
	
	public UserResponseDto updateUser(UserRequestDto updatedData);

	public UserResponseDto patchUpdateUser(UserPatchDto updatedData);
	
	public void deleteUser(Long userid);
	
}
