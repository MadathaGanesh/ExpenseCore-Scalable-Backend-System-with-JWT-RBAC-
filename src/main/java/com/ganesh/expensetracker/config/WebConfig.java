package com.ganesh.expensetracker.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ganesh.expensetracker.audit.AuditInterceptor;

import lombok.RequiredArgsConstructor;

/***
 * Configuration class to register interceptors
 * 
 * - Without this configuration class, Interceptor will not work.
 */

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
	private final AuditInterceptor auditInterceptor;
	
	
	// Register AuditInterceptor for all endpoints
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(auditInterceptor)
			.addPathPatterns("/**"); // Apply to all endpoints
	}
	

}
