package com.ganesh.expensetracker.service.impl;


import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ganesh.expensetracker.dto.common.PageResponse;
import com.ganesh.expensetracker.dto.user.UserPatchDto;
import com.ganesh.expensetracker.dto.user.UserRequestDto;
import com.ganesh.expensetracker.dto.user.UserResponseDto;
import com.ganesh.expensetracker.entity.Role;
import com.ganesh.expensetracker.entity.User;
import com.ganesh.expensetracker.exception.EmailAlreadyExistsException;
import com.ganesh.expensetracker.exception.EmptyPatchException;
import com.ganesh.expensetracker.exception.ItemNotFound;
import com.ganesh.expensetracker.mapper.UserMapper;
import com.ganesh.expensetracker.repository.RoleRepository;
import com.ganesh.expensetracker.repository.UserRepository;
import com.ganesh.expensetracker.service.UserService;
import static com.ganesh.expensetracker.Constants.RoleConstants.ROLE_USER;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service layer for handling business logic related to Users.
 * 
 * @Service → Marks this class as a Spring Service component.
 * @Transactional(readOnly = true) → By default, all methods are read-only, unless explicitly marked with @Transactional.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService{

	private final UserRepository userRepo;
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;
	private final RoleRepository roleRepo;
	
	/**
	 * Create and save a new user.
	 * 
	 * Steps:
	 * 1. Check if email already exists
	 * 2. Convert DTO → Entity
	 * 3. Encode password
	 * 4. Save entity to database
	 * 5. Convert Entity → ResponseDTO
	 */
	@Transactional
	public UserResponseDto saveUser(UserRequestDto dto) {
		
		if(userRepo.existsByEmail(dto.getEmail())) {
			throw new EmailAlreadyExistsException("Email already exists."+ dto.getEmail() +" .Pls enter different email");
		}
		
		// convert RequestDTO to User entity
		User user = userMapper.toEntity(dto);
		
		// Encoding the password given by user
		user.setPassword(passwordEncoder.encode(dto.getPassword()));
		
		Role role = roleRepo.findByName(ROLE_USER).orElseThrow(()-> new ItemNotFound("Role USER not found"));
		user.setRole(Set.of(role));
		
		// Persist (or) save UserData to entity
		User savedUser= userRepo.saveAndFlush(user);
		
		log.info("Creating user email = {}",dto.getEmail());
		
		// Convert saved_Entity to Dto
		return userMapper.toDto(savedUser);
		
	} 
	
	@PreAuthorize("hasAuthority('READ_USER')")
	public UserResponseDto getUserByID() {
		log.info("current Logged in user Details : ");
		User user = getCurrentUserDetails();
		return userMapper.toDto(user);
	}
	

	@PreAuthorize("hasRole('ADMIN')")
	public PageResponse<UserResponseDto> getAllUsers( Pageable pageable){
		Page<User> page = userRepo.findAll(pageable);
		log.info("Fetching all user Details ..");
		return new PageResponse<UserResponseDto>(
				page.map(userMapper::toDto).toList(),
				 page.getNumber()+1, page.getSize(), page.getTotalElements()
				 ,page.getTotalPages(),page.isLast(),page.hasNext(),page.hasPrevious());
	}
	
	
	// Put Mapping (Full Update)
	@Transactional
	@PreAuthorize("#id == authentication.principal.user.id or hasRole('ADMIN')")
	public UserResponseDto updateUser(UserRequestDto updatedData) {
				
		User user = getCurrentUserDetails();
		
		// Check if the new email already exists in the database and ensure it does not belong to the same user who is being updated.
		// This prevents duplicate email addresses across different users.
		if(userRepo.existsByEmail(updatedData.getEmail()) && !user.getEmail().equals(updatedData.getEmail())) {
			throw new EmailAlreadyExistsException("Email already exists : "+updatedData.getEmail());
		}
		
		user.setUserName(updatedData.getUserName());
		user.setPassword(passwordEncoder.encode(updatedData.getPassword()));  // Encoding password
		user.setAge(updatedData.getAge());
		user.setEmail(updatedData.getEmail());
		
		userRepo.save(user);
		
		log.info("Used updated userId = {} ",user.getId());
		
		return userMapper.toDto(user);
	}
	
	
	/**
	 * Partial update of a user (PATCH request).
	 * 
	 * Only updates fields that are provided in the request.
	 */
	@Transactional
	@PreAuthorize("#id == authentication.principal.user.id or hasRole('ADMIN')")	
	public UserResponseDto patchUpdateUser(UserPatchDto updatedData) {			
		if(updatedData.isPatchEmpty()) {
			throw new EmptyPatchException("Atleast one field required for patch update");
		}
		
		User user = getCurrentUserDetails();
		
		if(updatedData.getEmail() != null &&
				userRepo.existsByEmail(updatedData.getEmail()) &&
				!updatedData.getEmail().equals(user.getEmail())) {
			throw new EmailAlreadyExistsException("Email already Exists ! ");
		}
		
		// Update fields only if new value is provided
		user.setUserName(updatedData.getUserName() != null ? updatedData.getUserName():user.getUserName());
		user.setEmail(updatedData.getEmail()!=null ? updatedData.getEmail(): user.getEmail());
		user.setAge(updatedData.getAge()!=null ? updatedData.getAge():user.getAge());
		user.setPassword(updatedData.getPassword() != null ? passwordEncoder.encode(updatedData.getPassword()):user.getPassword());
		
		userRepo.save(user);
		log.info("User updated userId = {}",user.getId());
		return userMapper.toDto(user);
	} 
	
	
	@Transactional
	@PreAuthorize("hasAuthority('DELETE_USER')")
	public void deleteUser(Long id) {

	    User currentUser = getCurrentUserDetails();

	    if (!currentUser.isAdmin() && !currentUser.getId().equals(id)) {
	        throw new RuntimeException("You are not allowed to delete this user");
	    }

	    User user = userRepo.findById(id)
	        .orElseThrow(() -> new ItemNotFound("User not found"));

	    userRepo.delete(user);
	}
	
	
	
	/**
	 * Fetch details of the currently authenticated user / Currently LoggedIn user details.
	 * 
	 * Steps:
	 * 1. Get Authentication object from SecurityContext
	 * 2. Extract username/email
	 * 3. Fetch user from database and check whether the email is there in DB or not, and throw error if not found.
	 */
	public User getCurrentUserDetails() {
		
	// Get authentication object from Spring Security context
	 Authentication authenticatedUser =	SecurityContextHolder.getContext().getAuthentication();
		
	 	String email = authenticatedUser.getName(); // Here we are fetching by "Email" in our case
	 
		Optional<User> optionalUser = userRepo.findByEmail(email);
					
		if(optionalUser.isEmpty()) {
			log.warn("User not found : = {}",email);
			throw new ItemNotFound("User not found = "+email);
		}

		return optionalUser.get();
	}

	

}
