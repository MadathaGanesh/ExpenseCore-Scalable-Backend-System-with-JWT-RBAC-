package com.ganesh.expensetracker.exception;

import java.io.IOException;
import java.time.LocalDateTime;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.ganesh.expensetracker.dto.common.ApiErrorResponse;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

@Component
public class CustomAuthenticationException implements AuthenticationEntryPoint{
	
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		
		ApiErrorResponse errorResponse = ApiErrorResponse.builder()
				.status(HttpStatus.UNAUTHORIZED.value())
				.error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
				.message("Authentication Required")
				.path(request.getRequestURI())
				.correlationId(MDC.get("correlationId"))
				.timestamp(LocalDateTime.now())
				.build();
		
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		
		objectMapper.writeValue(response.getOutputStream(), errorResponse);
	}
}
