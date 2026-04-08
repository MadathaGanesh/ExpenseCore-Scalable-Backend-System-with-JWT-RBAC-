package com.ganesh.expensetracker.service;


import org.springframework.data.domain.Pageable;

import com.ganesh.expensetracker.dto.category.CategoryRequest;
import com.ganesh.expensetracker.dto.category.CategoryResponse;
import com.ganesh.expensetracker.dto.common.PageResponse;

public interface CategoryService {
	
	public PageResponse<CategoryResponse> getAllCategories(Pageable pageable);

	public CategoryResponse saveCategory(CategoryRequest requestcategory);
	
	public void deleteCategory(Long categoryId); 
	
	public CategoryResponse updateCategory(Long categoryId, CategoryRequest updatedCategoryData);
	
}



