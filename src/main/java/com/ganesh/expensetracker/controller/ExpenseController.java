package com.ganesh.expensetracker.controller;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ganesh.expensetracker.dto.common.ApiResponse;
import com.ganesh.expensetracker.dto.common.PageResponse;
import com.ganesh.expensetracker.dto.expense.ExpensePatchDTO;
import com.ganesh.expensetracker.dto.expense.ExpenseRequestDto;
import com.ganesh.expensetracker.dto.expense.ExpenseResponseDto;
import com.ganesh.expensetracker.service.ExpenseService;
import com.ganesh.expensetracker.util.ApiResponseUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expenses")
@Slf4j
public class ExpenseController {

	private final ExpenseService expenseService;
	private final ApiResponseUtil responseUtil;
	
	@PostMapping
	public ResponseEntity<ApiResponse<ExpenseResponseDto>> saveExpense(@RequestBody @Valid ExpenseRequestDto expensedata) {
		log.info("creating expense");
		ExpenseResponseDto savedExpense = expenseService.saveExpense(expensedata);
		return ResponseEntity.status(HttpStatus.CREATED).body(responseUtil.success(savedExpense, "created Expense"));
	}
	
	@GetMapping
	public ResponseEntity<ApiResponse<PageResponse<ExpenseResponseDto>>> getAllExpensesByLoggedInUser(Pageable pageable){
		log.info("Fetching Expenses");
		PageResponse<ExpenseResponseDto> expenses = expenseService.getAllExpensesByUserID( pageable);
		return ResponseEntity.status(HttpStatus.OK).body(responseUtil.success(expenses, "Expenses fetched"));
	}
	
	
	// Using @RequestParam annotation for getting Individual Expense by Expense_ID (api/expense?id=52)
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<ExpenseResponseDto>> getExpenseByExpenseId(@PathVariable("id") Long expenseId){
		log.info("Fetching expense id={}",expenseId);
		ExpenseResponseDto expense = expenseService.getSingleExpenseByExpenseIdAndUserId(expenseId);
		return ResponseEntity.status(HttpStatus.OK).body(responseUtil.success(expense, "Expense fetched"));
	}

	// Using @PathVariable annotation for deleting Expense by Expense_ID (api/expense/52)
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<String>> deleteExpense(@PathVariable Long id) {
		log.warn("Deleting expense id={}",id);
	    expenseService.deleteExpense(id);
	    return ResponseEntity.ok(responseUtil.success("Deleted", "Expense Deleted"));
	}
	
	
	
	// For PutMappping, Client needs to send complete data
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<ExpenseResponseDto>> updateExpenseDetails(@PathVariable Long id, @Valid @RequestBody ExpenseRequestDto expense){
		log.info("Updating expense id={}",id);
		ExpenseResponseDto updatedExpense = expenseService.updatePutExpense(expense, id);
		return ResponseEntity.status(HttpStatus.OK).body(responseUtil.success(updatedExpense, "Expense updated successfully"));
	}
	
	
	/*
	 * PATCH API - Update Expense (Partial Update)
	 * -------------------------------------------
	 * This endpoint updates only the fields sent by the client.
	 *
	 * Example Request:
	 * PATCH /expense/10
	 *
	 * {
	 *   "amount": 500
	 * }
	 *
	 * Responsibilities:
	 * 1. Accept Expense ID from URL path
	 * 2. Accept partial update data in Request Body
	 * 3. Forward request to Service Layer
	 * 4. Return updated ExpenseResponseDto
	 *
	 * NOTE:
	 * Controller should NOT contain business logic.
	 * All update logic is handled inside Service Layer.
	 */
	@PatchMapping("/{id}")
	public ResponseEntity<ApiResponse<ExpenseResponseDto>> patchExpenseDetails(@PathVariable Long id, @Valid @RequestBody ExpensePatchDTO dto){
		log.info("updating expense id={}",id);
		ExpenseResponseDto response = expenseService.patchUpdateExpense(id, dto);
		return ResponseEntity.status(HttpStatus.OK).body(responseUtil.success(response, "Expense updated successfully"));
	}
	
	@GetMapping("/name")
	public ResponseEntity<ApiResponse<PageResponse<ExpenseResponseDto>>> filterByExpenseName(@RequestParam String expenseName, Pageable pageable){
		log.info("Filtering by expense name={}",expenseName);
		PageResponse<ExpenseResponseDto> response = expenseService.filterByExpenseName(expenseName, pageable);
		return ResponseEntity.status(200).body(responseUtil.success(response, "Filtered by Expense"));
	}
	
	@GetMapping("/date")
	public ResponseEntity<ApiResponse<PageResponse<ExpenseResponseDto>>> filterByDate(
			  @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
		      @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
		      Pageable pageable){
		log.info("Filtering by date | startdate={} | endDate={}",startDate,endDate);
		PageResponse<ExpenseResponseDto> expenses = expenseService.filterByDate( startDate, endDate, pageable);
		return ResponseEntity.ok(responseUtil.success(expenses, "Filtered by date"));
	}

}



