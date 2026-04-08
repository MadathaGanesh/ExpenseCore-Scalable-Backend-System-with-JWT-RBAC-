package com.ganesh.expensetracker.dto.category;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *  DTO for returning category Response
 */


@AllArgsConstructor
@NoArgsConstructor
@Data
@Setter
@Getter
public class CategoryResponse {
	
	private Long categoryId;
	private String categoryName;
	private String categoryDescription;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}



