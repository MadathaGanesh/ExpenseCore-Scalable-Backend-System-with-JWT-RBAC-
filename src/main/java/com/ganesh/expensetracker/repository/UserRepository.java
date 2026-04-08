package com.ganesh.expensetracker.repository;


import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ganesh.expensetracker.entity.User;

/**
 * Repository for User Entity
 *
 * Responsibilities:
 * - Database interaction for User
 * - Used by Service layer for authentication and user operations
 */

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	 
    // Check if email already exists (used during registration)
	 boolean existsByEmail(String email);
	 	
	 // Fetch user by email (used in authentication)
	 Optional<User> findByEmail(String email);

}


