package com.ganesh.expensetracker.audit;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.ganesh.expensetracker.service.AuditService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/***
 *  Audit for capturing HTTP REQUEST Level logs.
 *  
 *  What it logs:
 *  - HTTP Method (GET/POST/PUT/DELETE)
 *  - Rquest URL (/api/v1/users)
 *  - Response status code (200, 201,404 etc)
 *  
 *  Why Interceptor ?
 *  - works at request lifecycle level
 *  - captures detail, which AOP cannot capture
 */

@Component
@RequiredArgsConstructor
public class AuditInterceptor implements HandlerInterceptor {

	private final AuditUtil auditUtil;
	private final AuditService auditService;
	
	// This method runs after every request completion
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
			Object handler, Exception ex) {
		String action = request.getMethod() + " : " + response.getStatus();
		String endPoint = request.getRequestURI();
		auditService.log(action, auditUtil.getCurrentLoggedinUser(), endPoint);
	}
	
}
