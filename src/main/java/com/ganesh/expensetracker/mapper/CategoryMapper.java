package com.ganesh.expensetracker.mapper;

import org.springframework.stereotype.Component;

import com.ganesh.expensetracker.dto.category.CategoryRequest;
import com.ganesh.expensetracker.dto.category.CategoryResponse;
import com.ganesh.expensetracker.entity.Category;

/**
 * Mapper for Category Entity <-> DTO
 */

@Component
public class CategoryMapper {
	
	// Convert Request DTO -> Entity
	public Category toEntity(CategoryRequest dto) {
		Category category = new Category();
		category.setCategoryName(dto.getCategoryName());
		category.setCategoryDescription(dto.getCategoryDescription());
		return category;
	}
	

	// Convert Entity -> Request DTO
	public CategoryResponse toDTO(Category category) {
		CategoryResponse response = new CategoryResponse();
		response.setCategoryId(category.getId());
		response.setCategoryName(category.getCategoryName());
		response.setCategoryDescription(category.getCategoryDescription());
		response.setCreatedAt(category.getCreatedAt());
		response.setUpdatedAt(category.getUpdatedAt()); 
		return response;
	}
}
