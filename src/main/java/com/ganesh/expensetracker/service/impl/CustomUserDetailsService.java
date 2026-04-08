package com.ganesh.expensetracker.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ganesh.expensetracker.entity.User;
import com.ganesh.expensetracker.exception.ItemNotFound;
import com.ganesh.expensetracker.repository.UserRepository;
import com.ganesh.expensetracker.security.CustomUserDetails;

import lombok.AllArgsConstructor;

/*
 * Service class to fetch UserDetails from the database.
 * This replaces InMemoryAuthentication.
 */

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService{

	private final UserRepository userRepo;
	
	 /*
     * This method is called by Spring Security during login.
     * It fetches user data from the database using email.
     */
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		
		User user = userRepo.findByEmail(email).orElseThrow(()-> new ItemNotFound("User not found with email" + email));
		
        // Wrap our User entity into Spring Security's UserDetails class (i.e: customUserDetails class)
		return new CustomUserDetails(user);
	}

}



