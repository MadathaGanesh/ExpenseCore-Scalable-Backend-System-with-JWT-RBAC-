package com.ganesh.expensetracker.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating / Updating category
 */

@AllArgsConstructor
@Data
@NoArgsConstructor
public class CategoryRequest {
	
	@NotBlank(message = "Category Name cannot be blank")
	@Size(min = 3, message = "category name should have minimum 3 digits")
	private String categoryName;
	
	private String categoryDescription;

}
