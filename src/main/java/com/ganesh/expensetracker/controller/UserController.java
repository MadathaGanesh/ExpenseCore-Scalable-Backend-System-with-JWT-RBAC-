package com.ganesh.expensetracker.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ganesh.expensetracker.dto.common.ApiResponse;
import com.ganesh.expensetracker.dto.common.PageResponse;
import com.ganesh.expensetracker.dto.user.UserPatchDto;
import com.ganesh.expensetracker.dto.user.UserRequestDto;
import com.ganesh.expensetracker.dto.user.UserResponseDto;
import com.ganesh.expensetracker.service.UserService;
import com.ganesh.expensetracker.util.ApiResponseUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
@Slf4j
public class UserController {
	
	private final UserService userService;
	private final ApiResponseUtil responseUtil;
	
	@GetMapping
	public ResponseEntity<ApiResponse<PageResponse<UserResponseDto>>> getAllUsers(@PageableDefault(size = 10) Pageable pageable){
		validateSortFields(pageable);
		log.info("Fetching all users details");
		PageResponse<UserResponseDto> response = userService.getAllUsers(pageable);
		return ResponseEntity.status(HttpStatus.OK).body(responseUtil.success(response, "Fetched all users"));	
	}
	
	
	private void validateSortFields(Pageable pageable) {
	    List<String> allowedFields = List.of("userName", "email", "age", "createAt", "updatedAt");

	    pageable.getSort().forEach(order -> {
	        if (!allowedFields.contains(order.getProperty())) {
	            throw new IllegalArgumentException("Invalid sort field: " + order.getProperty());
	        }
	    });
	}
	
	@GetMapping("/me")
	public ResponseEntity<ApiResponse<UserResponseDto>> getUserByUserId(){
		log.info("Fecthing logged-in user details");
		UserResponseDto response= userService.getUserByID();
		return ResponseEntity.ok(responseUtil.success(response, "User fetched"));
	}
	
	
	@PatchMapping("/user")
	public ResponseEntity<ApiResponse<UserResponseDto>> partialUserUpdate( @RequestBody UserPatchDto userData){
		log.info("Updating user data partially");
		UserResponseDto response = userService.patchUpdateUser(userData);
		return ResponseEntity.ok(responseUtil.success(response, "User updated partially"));
	}
	
	@PutMapping("/user")
	public ResponseEntity<ApiResponse<UserResponseDto>> completeUserUpdate(@Valid @RequestBody UserRequestDto userData){
		log.info("Updating user data completely");
		UserResponseDto response = userService.updateUser(userData);
		return ResponseEntity.ok(responseUtil.success(response, "User data updated successfully"));
	}
	
	@DeleteMapping("/deactivate/{userId}")
	public ResponseEntity<ApiResponse<String>> deleteUserById(@PathVariable Long userId){
		log.warn("deleting user");
		userService.deleteUser(userId);
		return ResponseEntity.ok(responseUtil.success("Deleted", "user Deleted"));
	}
}
