package com.ganesh.expensetracker.controller;


import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ganesh.expensetracker.dto.category.CategoryRequest;
import com.ganesh.expensetracker.dto.category.CategoryResponse;
import com.ganesh.expensetracker.dto.common.ApiResponse;
import com.ganesh.expensetracker.dto.common.PageResponse;
import com.ganesh.expensetracker.service.CategoryService;
import com.ganesh.expensetracker.util.ApiResponseUtil;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/categories")
@AllArgsConstructor
@Slf4j
public class CategoryController {

	private final CategoryService categoryService;
	private final ApiResponseUtil responseUtil;
	
	@GetMapping
	public ResponseEntity<ApiResponse<PageResponse<CategoryResponse>>> getAllCategories(Pageable pageable){
		log.info("Fetching all categories");
		return ResponseEntity.ok(responseUtil.success(categoryService.getAllCategories(pageable), "Categories Fetched"));
	}
	
	@PostMapping
	public ResponseEntity<ApiResponse<CategoryResponse>> saveNewCategory(@Valid @RequestBody CategoryRequest categoryData) {
		log.info("creating category {} ",categoryData.getCategoryName());
		CategoryResponse savedCategory = categoryService.saveCategory(categoryData);
		return ResponseEntity.status(201).body(responseUtil.success(savedCategory, "Category created"));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<String>> deleteCategory(@PathVariable Long id){
		log.warn("Deleting category id ={}",id);
		categoryService.deleteCategory(id);
		return ResponseEntity.ok(responseUtil.success("Deleted", "Category Deleted"));
	}
	
	@PutMapping("/{categoryId}")
	public ResponseEntity<ApiResponse<CategoryResponse>> updateCompleteCategory(@PathVariable Long categoryId ,
			@RequestBody @Valid CategoryRequest categoryData ){
			log.info("Updating category id={}",categoryId);
		
			CategoryResponse updatedCategory = categoryService.updateCategory(categoryId, categoryData);
		
		return ResponseEntity.status(200).body(responseUtil.success(updatedCategory, "category updated successfully"));
	}
}
