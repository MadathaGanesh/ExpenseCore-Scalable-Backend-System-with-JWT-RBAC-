package com.ganesh.expensetracker.service.impl;


import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ganesh.expensetracker.dto.common.PageResponse;
import com.ganesh.expensetracker.dto.expense.ExpensePatchDTO;
import com.ganesh.expensetracker.dto.expense.ExpenseRequestDto;
import com.ganesh.expensetracker.dto.expense.ExpenseResponseDto;
import com.ganesh.expensetracker.entity.Category;
import com.ganesh.expensetracker.entity.Expense;
import com.ganesh.expensetracker.entity.User;
import com.ganesh.expensetracker.exception.EmptyPatchException;
import com.ganesh.expensetracker.exception.ItemNotFound;
import com.ganesh.expensetracker.mapper.ExpenseMapper;
import com.ganesh.expensetracker.repository.CategoryRepository;
import com.ganesh.expensetracker.repository.ExpenseRepository;
import com.ganesh.expensetracker.service.ExpenseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/*
 * ExpenseService 
 * -------------------
 * Responsibilities:
 * - Business logic
 * - Ownership enforcement
 * - Role-based access (USER vs ADMIN)
 * - DTO ↔ Entity conversion
 */


@Service
@Slf4j
@AllArgsConstructor
@Transactional(readOnly = true)  // By Default, we apply "readOnly" for all methods. (No Data Modification now)
public class ExpenseServiceImpl implements ExpenseService{
	
    private final UserServiceImpl userService;
	private final CategoryRepository categoryRepo;
	private final ExpenseRepository expenseRepo;
	private final ExpenseMapper expenseMapper;


	/*  Save the new expense with the currently logged-in User.
	 *  
	 *  Flow:
	 *  1. Convert "RequestDTO to Entity"
	 *  2. Save "Entity" to Repository
	 *  3. Convert "Entity" to "ResponseDTO"
	 *  4. Send "ResponseDTO" to controller
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@PreAuthorize("hasAuthority('CREATE_EXPENSE')")
	public ExpenseResponseDto saveExpense(ExpenseRequestDto dto) {
		
		User user = userService.getCurrentUserDetails();
		Category category = getCategoryOrThrow(dto.getCategoryId(), user.getId()); // We need to attach this "category" to Expense entity.
		Expense expense = expenseMapper.ConvertToEntity(dto);				
		
		// Set relations
		expense.setUser(user);
		expense.setCategory(category);
		log.info("Creating expense for userID = {}",user.getId());
		Expense savedExpense = expenseRepo.save(expense);
		return expenseMapper.convertToDto(savedExpense);		
	}
	
	
	@PreAuthorize("hasAuthority('READ_EXPENSE')")
	public PageResponse<ExpenseResponseDto> getAllExpensesByUserID(Pageable pageable){
		User user = userService.getCurrentUserDetails();
		Page<Expense> page;
		
		if(user.isAdmin()) {
			log.info("Admin fetching all expenses ..");
			page = expenseRepo.findAll(pageable);
		}
		else {
		page = expenseRepo.findByUserId(user.getId(), pageable);
		log.info("Fetching all expense of user ID = {}",user.getId());
		}
		return new PageResponse<ExpenseResponseDto>(
				page.map(expenseMapper::convertToDto).toList()
				, page.getNumber(), page.getSize(), page.getTotalElements(),
				page.getTotalPages(), page.isLast(), page.hasNext(), page.hasPrevious());
	}
	
	
	@PreAuthorize("hasAuthority('READ_EXPENSE')")
	public ExpenseResponseDto getSingleExpenseByExpenseIdAndUserId(Long expenseId) {
		User user = userService.getCurrentUserDetails();
		log.info("Fetching expense id = {} | userId = {}",expenseId,user.getId());
		Expense expense = getExpenseOrThrow(expenseId, user.getId());
		return expenseMapper.convertToDto(expense);
	}

	// Delete Expense (Only if it belongs to logged-in user)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@PreAuthorize("hasAuthority('DELETE_EXPENSE')")
	public void deleteExpense(Long expenseId) {
		User user = userService.getCurrentUserDetails();
		Expense expense;
		
		if(user.isAdmin()) {
			log.warn("Admin deleting expense id ={}",expenseId);
			expense = expenseRepo.findById(expenseId).orElseThrow(()-> new ItemNotFound("Expense not found : "+expenseId));
			expenseRepo.delete(expense);
		}else {
		expense = getExpenseOrThrow(expenseId, user.getId());
		 expenseRepo.delete(expense);
		 log.warn("Expense deleted Id = {}, by userId = {} ",expenseId, user.getId());
		}
	}
	
	// Put Mapping: User needs to send complete data to the backend server. If not, those fields will set as null values.
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@PreAuthorize("hasAuthority('UPDATE_EXPENSE')")
	public ExpenseResponseDto updatePutExpense(ExpenseRequestDto updatedDtoData, Long expenseId) {
		
		User user = userService.getCurrentUserDetails();
		Expense expense = getExpenseOrThrow(expenseId, user.getId());
		Category category = getCategoryOrThrow(updatedDtoData.getCategoryId(), user.getId());
		
		expense.setName(updatedDtoData.getExpenseName());
		expense.setAmount(updatedDtoData.getAmount());
		expense.setCategory(category);
		expense.setDescription(updatedDtoData.getDescription());
		log.info("Updating Expense id={}",expenseId);
		expenseRepo.save(expense);
		return expenseMapper.convertToDto(expense);
	}
	
	
	/*
	 * PATCH Update Expense:: Updates only the fields provided by the client
	 * 
	 * Flow:
	 * 1. Fetch existing Expense from DB
	 * 2. Throw Exception if Expense not found
	 * 3. Apply partial updates using Mapper
	 * 4. save updated entity
	 * 5. Convert Entity -> ResponseDTO
	 * 6. Return updated response
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@PreAuthorize("hasAuthority('UPDATE_EXPENSE')")
	public ExpenseResponseDto patchUpdateExpense( Long expenseId, ExpensePatchDTO dto) {
		
		if(dto.isPatchEmpty()) {
			throw new EmptyPatchException("Patch data cannot be empty...");
		}
		
		User user = userService.getCurrentUserDetails();
		Expense expense = getExpenseOrThrow(expenseId,user.getId());
		
		// Update category only if provided.
		if(dto.getCategoryId() != null) {
			Category category = getCategoryOrThrow(dto.getCategoryId(), user.getId());
			expense.setCategory(category);
		}
		
		expenseMapper.updateEntityfromDto(dto, expense);
		log.info("Updating Expense id={}",expenseId);
		Expense savedExpense = expenseRepo.save(expense);
		return expenseMapper.convertToDto(savedExpense);
	}
	
		
	// Filter Expenses by ExpenseName 
	@PreAuthorize("hasAuthority('READ_EXPENSE')")
	public PageResponse<ExpenseResponseDto> filterByExpenseName(String keyword, Pageable pageable){
		User user = userService.getCurrentUserDetails();
		Page<Expense> page = expenseRepo.findByUserIdAndNameContainingIgnoreCase(user.getId(),keyword,pageable);
		return new PageResponse<ExpenseResponseDto>(
				page.map(expenseMapper::convertToDto).toList(),
				page.getNumber()+1, page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast(), page.hasNext(), page.hasPrevious());
	}
	 
	// Filter Expenses By Date Range
	@PreAuthorize("hasAuthority('READ_EXPENSE')")
	public PageResponse<ExpenseResponseDto> filterByDate(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable){
		User user = userService.getCurrentUserDetails();
		Page<Expense> page= expenseRepo.findByUserIdAndCreatedDateBetween(user.getId(), startDate, endDate, pageable);
		return new PageResponse<ExpenseResponseDto>(page.map(expenseMapper::convertToDto).toList(),
			page.getNumber()+1, page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast(), page.hasNext(), page.hasPrevious());
	}
	
	
	// Helper Methods
	// Fetch category with Validation
	private Category getCategoryOrThrow(Long categoryId, Long userId) {
		return categoryRepo.findByIdAndUserId(categoryId, userId)
				.orElseThrow(()->{
					log.warn("Category not found id={} | userId={}",categoryId,userId);
				return new ItemNotFound("Category Not found :"+categoryId);
				});
	}
	
	
	private Expense getExpenseOrThrow(Long expenseId, Long userId) {
		return expenseRepo.findByIdAndUserId(expenseId, userId)
				.orElseThrow(()->{
					log.warn("Expense not found id = {} | userId={}",expenseId,userId);
					return new ItemNotFound("Expense not found : "+expenseId);
				});
	}

}




