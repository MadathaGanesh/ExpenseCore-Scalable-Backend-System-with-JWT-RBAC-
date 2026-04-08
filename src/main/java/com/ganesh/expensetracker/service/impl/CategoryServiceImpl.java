package com.ganesh.expensetracker.service.impl;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ganesh.expensetracker.dto.category.CategoryRequest;
import com.ganesh.expensetracker.dto.category.CategoryResponse;
import com.ganesh.expensetracker.dto.common.PageResponse;
import com.ganesh.expensetracker.entity.Category;
import com.ganesh.expensetracker.entity.User;
import com.ganesh.expensetracker.exception.ItemNotFound;
import com.ganesh.expensetracker.mapper.CategoryMapper;
import com.ganesh.expensetracker.repository.CategoryRepository;
import com.ganesh.expensetracker.service.CategoryService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService{
	
	private final CategoryRepository categoryRepo;
	private final UserServiceImpl userService;
	private final CategoryMapper categoryMapper;
	
	@PreAuthorize("hasAuthority('READ_CATEGORY')")
	public PageResponse<CategoryResponse> getAllCategories(Pageable pageable){
		User user= userService.getCurrentUserDetails();
		Page<Category> page;
		
		// If Role is "ADMIN" Fetch all categories
		if(user.isAdmin()) {
			page = categoryRepo.findAll(pageable);
			log.info("ADMIN fetching ALL categories | page={} | size={}",
	                pageable.getPageNumber(), pageable.getPageSize());
		}else {	
		// If Role is "USER" fetch only their own categories
		page = categoryRepo.findByUserId(user.getId(),pageable);
		log.info("User fetching categories userId={} | page={} | size={}", user.getId(), pageable.getPageNumber(), pageable.getPageSize());
		}
		return new PageResponse<CategoryResponse>(
				page.getContent().stream().map(categoryMapper::toDTO).toList()
				, page.getNumber()+1, page.getSize(), page.getTotalElements(),
				page.getTotalPages(), page.isLast(), page.hasNext(), page.hasPrevious());
	}
	
	@PreAuthorize("hasAuthority('CREATE_CATEGORY')")
	@Transactional(propagation =Propagation.REQUIRES_NEW)
	public CategoryResponse saveCategory(CategoryRequest dto) {
		var user = userService.getCurrentUserDetails();
		Category category = categoryMapper.toEntity(dto);
		category.setUser(user);
		log.info("creating category={} | userId={}",dto.getCategoryName(), user.getId());
		Category savedCategory = categoryRepo.save(category);
		return categoryMapper.toDTO(savedCategory);
	}
	
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@PreAuthorize("hasAuthority('DELETE_CATEGORY')")
	public void deleteCategory(Long categoryId ) {
		User user = userService.getCurrentUserDetails();
	    Category deletedCategory;
	    
	    if (user.isAdmin()) {
	        log.warn("Admin deleting category id = {}", categoryId);

	        deletedCategory = categoryRepo.findById(categoryId)
	            .orElseThrow(() -> new ItemNotFound("Category not found with Id: " + categoryId));

	    } else {
	        deletedCategory = categoryRepo.findByIdAndUserId(categoryId, user.getId())
	            .orElseThrow(() -> new ItemNotFound("Category not found with ID: " + categoryId));
	    }

	    categoryRepo.delete(deletedCategory);

	    log.warn("Category deleted id={} | by userId={}", categoryId, user.getId());
	}
	
	// Update (Full Category Data update (or) PUT Mapping)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@PreAuthorize("hasAuthority('UPDATE_CATEGORY')")
	public CategoryResponse updateCategory(Long categoryId, CategoryRequest dto ) {
		User user = userService.getCurrentUserDetails();
		
		Category category = categoryRepo.findByIdAndUserId(categoryId, user.getId())
				.orElseThrow(()->{ 
					log.warn("category not found id = {}", categoryId);
					return new ItemNotFound("category not found with ID : "+categoryId);
				});
		
		category.setCategoryDescription(dto.getCategoryDescription());
		category.setCategoryName(dto.getCategoryName());
		categoryRepo.save(category);
		log.info("category updated id={} | userId={}",categoryId,user.getId());
		return categoryMapper.toDTO(category);

	}

}
