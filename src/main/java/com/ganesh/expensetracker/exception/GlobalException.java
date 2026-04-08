package com.ganesh.expensetracker.exception;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
//import com.ganesh.expensetracker.config.SwaggerConfig;
//import com.ganesh.expensetracker.controller.AuthController;
import com.ganesh.expensetracker.dto.common.ApiErrorResponse;
//import com.ganesh.expensetracker.repository.BlacklistedTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
//import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * @RestControllerAdvice:
 *  - Makes this class applicable to ALL controllers
 *  - Centralized exception handling
 *  - Avoids writing try-catch blocks in every controller
 */

@RestControllerAdvice
@Slf4j
public class GlobalException {

	private String getCorrelationId() {
		return MDC.get("correlationId");
	}
	
	private ApiErrorResponse<?> buildError(HttpStatus status, String message, String path) {
		return ApiErrorResponse.builder()
				.status(status.value())
				.error(status.getReasonPhrase())
				.path(path)
				.correlationId(getCorrelationId())
				.timestamp(LocalDateTime.now())
				.build();
	}
	
	
	// Not Found Exception
	@ExceptionHandler(ItemNotFound.class)
	public ResponseEntity<ApiErrorResponse<?>> handleExpenseNotFoundException(ItemNotFound ex, HttpServletRequest request){
	
	log.error(" Item not found | path={} | message={} ",request.getRequestURI(),ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI()));
	}
	
	
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiErrorResponse<?>> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request){
		log.warn("Illegal Argument Exception occured | path={} | message={} ",request.getRequestURI(),ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI()));
	}
	
	
	   /*
     * VALIDATION EXCEPTION HANDLER
     * ----------------------------
     * Triggered when DTO validation fails.
     *
     * Example:
     * @Valid ExpenseRequestDto dto
     *
     * If any validation annotation fails:
     *  - @NotBlank
     *  - @NotNull
     *  - @Size
     *  - @Positive
     *
     * Spring throws:
     * MethodArgumentNotValidException
     */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse<?>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request){
				
		String errors = ex.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(err -> err.getField() + " : "+err.getDefaultMessage())
			.collect(Collectors.joining(" , "));
		
		log.warn("Validation failed | path={} | error={}",request.getRequestURI(),errors);	
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI()));
	}
	
	
	// Exception Handling for Patch Update
	@ExceptionHandler(EmptyPatchException.class)
	public ResponseEntity<ApiErrorResponse<?>> handleEmptyPatchException(EmptyPatchException ex, HttpServletRequest request){
		log.warn("Empty patch | path={}",request.getRequestURI());		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI()));
	}
	
	// Email Already Exists Exception: conflict
	@ExceptionHandler(EmailAlreadyExistsException.class)
	public ResponseEntity<ApiErrorResponse<?>> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex, HttpServletRequest request){
		log.warn("Email conflict | path={}, message={}", request.getRequestURI(), ex.getMessage());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(buildError(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI()));
	}	
	
	
	// Authorization Exception
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ApiErrorResponse<?>> handleBadCredentialsException(BadCredentialsException ex, HttpServletRequest request){
		log.warn("Invalid credentials | path={}",request.getRequestURI());
	    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(buildError(HttpStatus.UNAUTHORIZED,"Invaid User or Pasword", request.getRequestURI()));
	}
	
	
	// Authentication Exception
	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleUserNotFound(UsernameNotFoundException ex, HttpServletRequest request){
		log.warn("User nor found | path={}",request.getRequestURI());
	    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(buildError(HttpStatus.UNAUTHORIZED, "User not found", request.getRequestURI()));
	}
	

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleInternalServerException(Exception ex, HttpServletRequest request){
		log.warn("Something went wrong | path={}",request.getRequestURI());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(buildError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request.getRequestURI()));
	}
}
