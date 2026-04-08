package com.ganesh.expensetracker.exception;

import java.io.IOException;
import java.time.LocalDateTime;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.ganesh.expensetracker.dto.common.ApiErrorResponse;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

@Component
public class CustomAuthorizationException implements AccessDeniedHandler{

	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {

		ApiErrorResponse errorResponse = ApiErrorResponse.builder()
				.status(HttpStatus.FORBIDDEN.value())
				.error(HttpStatus.FORBIDDEN.getReasonPhrase())
				.message("Access Denied")
				.path(request.getRequestURI())
				.correlationId(MDC.get("correlationId"))
				.timestamp(LocalDateTime.now())
				.build();
				
				
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		
		objectMapper.writeValue(response.getOutputStream(), errorResponse);
	}
	
}
