package com.ganesh.expensetracker.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;

import com.ganesh.expensetracker.dto.common.PageResponse;
import com.ganesh.expensetracker.dto.expense.ExpensePatchDTO;
import com.ganesh.expensetracker.dto.expense.ExpenseRequestDto;
import com.ganesh.expensetracker.dto.expense.ExpenseResponseDto;

public interface ExpenseService {

	public ExpenseResponseDto saveExpense(ExpenseRequestDto requestData);

	PageResponse<ExpenseResponseDto> getAllExpensesByUserID(Pageable pageable);
	
	public ExpenseResponseDto getSingleExpenseByExpenseIdAndUserId(Long expenseId);
	
	public void deleteExpense(Long expenseId);
	
	public ExpenseResponseDto updatePutExpense(ExpenseRequestDto updatedDtoData, Long expenseId);
	
	public ExpenseResponseDto patchUpdateExpense( Long expenseId, ExpensePatchDTO dto);
		
	public PageResponse<ExpenseResponseDto> filterByExpenseName(String keyword, Pageable pageable);
	
	public PageResponse<ExpenseResponseDto> filterByDate(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}
