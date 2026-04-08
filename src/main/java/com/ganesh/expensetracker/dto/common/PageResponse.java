package com.ganesh.expensetracker.dto.common;

import java.util.List;

/**
 * Custom Pagination response
 * 
 * @param <T>
 */

public record PageResponse<T>
(
		List<T> content,
		int pageNumber,  // PageNumber starts from "1" not "0"
		int size,  // getTotalElements() method
		long totalElements,
		int totalPages,
		boolean isLastPage,
		boolean hasNext,
		boolean hasPrevious
)
{}