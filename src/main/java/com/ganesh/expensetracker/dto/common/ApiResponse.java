package com.ganesh.expensetracker.dto.common;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ApiResponse<T> {
	
	private boolean success;
	private String message;
	private T data;
	private LocalDateTime timestamp;
	private String correlationId;

}
