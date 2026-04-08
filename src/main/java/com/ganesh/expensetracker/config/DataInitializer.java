package com.ganesh.expensetracker.config;

import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.ganesh.expensetracker.entity.Role;
import com.ganesh.expensetracker.entity.User;
import com.ganesh.expensetracker.repository.RoleRepository;
import com.ganesh.expensetracker.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * This class runs automatically when the Spring Boot application starts.
 * 
 * Purpose:
 * - Ensures a default admin user exists in the database.
 * - Prevents duplicate admin creation on every restart.
 * 
 * Why CommandLineRunner?
 * - It executes after the application context is loaded,
 *   making it ideal for initializing required data.
 */

@RequiredArgsConstructor
@Component
@Slf4j
public class DataInitializer implements CommandLineRunner{
	
	private final UserRepository userRepo;
	private final PasswordEncoder passwordEncoder;
	private final RoleRepository roleRepo;

	@Value("${app.admin.email}")
	String adminEmail;
	
	@Value("${app.admin.password}")
	String adminPassword;
	
	@Value("${app.init.enabled:true}")
	private boolean initEnabled;
	
	@Override
	public void run(String... args) throws Exception {

		if(!initEnabled) {
			log.info("Data initialization is diabled ");
			return;
		}
		
		// Avoid duplication email creation
		if(userRepo.existsByEmail(adminEmail)) {
			log.info("admin email already exists ..");
			return;
		}
		
		
		// Fetch or Create role "ROLE_ADMIN"
		Role adminRole = roleRepo.findByName("ADMIN")
				.orElseGet(()->{
					log.warn("ROLE_ADMIN not found. creating its");
					Role role = new Role();
					role.setId((long) 2);
					role.setName("ROLE_ADMIN");
					return roleRepo.save(role);
				});
		
		
		// Creating "admin" role
		User admin = new User();
		admin.setEmail(adminEmail);
		admin.setPassword(passwordEncoder.encode(adminPassword));
		admin.setRole(Set.of(adminRole));
		admin.setUserName("Admin");
		admin.setAge(35);
		userRepo.save(admin);
        log.info("Admin user created successfully with email: {}", adminEmail);
	}

}
