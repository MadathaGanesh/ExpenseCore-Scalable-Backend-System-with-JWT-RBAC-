package com.ganesh.expensetracker.util;

import java.time.LocalDateTime;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import com.ganesh.expensetracker.dto.common.ApiResponse;

// Standard format to send response either success or failed (No messy struc

@Component
public class ApiResponseUtil {
	
	public <T>ApiResponse<T> success(T data, String message){
		return ApiResponse.<T>builder()
				.success(true)
				.message(message)
				.data(data)
				.timestamp(LocalDateTime.now())
				.correlationId(MDC.get("correlationId"))
				.build();
	}
	
	public <T>ApiResponse<T> failure(T data, String message){
		return ApiResponse.<T>builder()
				.success(false)
				.message(message)
				.data(data)
				.timestamp(LocalDateTime.now())
				.correlationId(MDC.get("correlationId"))
				.build();
	}
}
