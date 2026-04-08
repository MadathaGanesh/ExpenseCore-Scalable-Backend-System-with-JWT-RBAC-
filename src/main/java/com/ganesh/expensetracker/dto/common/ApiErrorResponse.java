package com.ganesh.expensetracker.dto.common;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

// This is standard format for Global Exception


@Getter
@Builder
public class ApiErrorResponse<T> {

	private int status;
	
	private String error;
		
	private String message;
	
	private String path;

	private String correlationId;
	
	private LocalDateTime timestamp;
	
}
