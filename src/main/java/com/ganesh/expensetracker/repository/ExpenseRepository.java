package com.ganesh.expensetracker.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ganesh.expensetracker.entity.Expense;

/**
 * Repository for Expense Entity
 *
 * Handles all DB operations related to expenses.
 */
@Repository
public interface ExpenseRepository extends JpaRepository<Expense,Long> {
	
    // Fetch single expense for logged-in user
	Optional<Expense> findByIdAndUserId(Long expenseid, Long userId);
	
    // Filter by category for logged-in user
	Page<Expense> findByUserIdAndCategoryId(Long userId, Long categoryId, Pageable pageable);
	
	// Fetch all expenses for a user (pagination)
	Page<Expense> findByUserId(Long userId, Pageable pageable);
	
    // Filter by expense name for logged-in user
	Page<Expense> findByUserIdAndNameContainingIgnoreCase(Long userId, String keyword, Pageable pageable);
	
	// Filter expenses by date range for logged-in user
    Page<Expense> findByUserIdAndCreatedDateBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
		
}

