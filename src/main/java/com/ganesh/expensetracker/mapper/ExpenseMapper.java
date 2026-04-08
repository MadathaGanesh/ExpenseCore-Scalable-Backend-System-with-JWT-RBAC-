package com.ganesh.expensetracker.mapper;


import org.springframework.stereotype.Component;

import com.ganesh.expensetracker.dto.expense.ExpensePatchDTO;
import com.ganesh.expensetracker.dto.expense.ExpenseRequestDto;
import com.ganesh.expensetracker.dto.expense.ExpenseResponseDto;
import com.ganesh.expensetracker.entity.Expense;

/**
 * Mapper for Expense Entity <-> DTO
 *
 * IMPORTANT:
 * - Does NOT handle relationships (User, Category)
 * - That is handled in Service Layer
 */

@Component 
public class ExpenseMapper {

	// Convert Request DTO -> Entity
	public Expense ConvertToEntity(ExpenseRequestDto dto) {
		
		Expense expense = new Expense();
		expense.setName(dto.getExpenseName());
		expense.setDescription(dto.getDescription());
		expense.setAmount(dto.getAmount());
		return expense;
	}
	

	// Converts Entity -> Response DTO
	public ExpenseResponseDto convertToDto(Expense expenseData) {
		ExpenseResponseDto expenseResponse = new ExpenseResponseDto();
		expenseResponse.setId(expenseData.getId());
		expenseResponse.setExpenseName(expenseData.getName());
		expenseResponse.setAmount(expenseData.getAmount());
		expenseResponse.setDescription(expenseData.getDescription());
		expenseResponse.setCreatedDate(expenseData.getCreatedDate());
		expenseResponse.setUpdatedDate(expenseData.getUpdatedDate());		
		return expenseResponse;
	}
	
	
	
	/*
	 *  Partial Update (Patch Mapping)
	 *   - Update only the fields provided by client
	 */
	public void updateEntityfromDto(ExpensePatchDTO dto, Expense expense) {
		
		if(dto.getExpenseName() != null) expense.setName(dto.getExpenseName());
		
		if(dto.getAmount() != null) expense.setAmount(dto.getAmount());
		
		if(dto.getDescription() != null) expense.setDescription(dto.getDescription());
	}
	
}
