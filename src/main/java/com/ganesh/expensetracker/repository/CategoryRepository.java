package com.ganesh.expensetracker.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ganesh.expensetracker.entity.Category;

import java.util.Optional;

// Repository for Category Entity

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>{
	
   // Get all categories for a specific user
	Page<Category> findByUserId(Long userId, Pageable pageable);
	
	// Get all categories for a specific user
	Optional<Category> findByIdAndUserId(Long categoryId,Long userId);
	
	// Check duplicate category name per user
	boolean existsByCategoryNameAndUserId(String categoryName, Long userId);
	
	//  Fetch category by name + user (SAFE VERSION)
	Optional<Category> findByCategoryNameAndUserId(String categoryName, Long userId);	
}


