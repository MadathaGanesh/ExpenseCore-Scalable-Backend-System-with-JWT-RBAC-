package com.ganesh.expensetracker.logging;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Request logging filter.
 *
 * Features:
 * - Adds Correlation ID (traceId) for tracking requests across logs
 * - Logs incoming requests and outgoing responses
 * - Measures execution time
 * - Handles and logs exceptions
 *
 * Why this matters:
 * - Debugging without traceId = nightmare in real systems
 * - Helps track a single request across multiple services
 */


@Component
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter{
	
	private static final String TRACE_ID = "traceId";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
	
		// Generate random trace ID for each request
		String traceId = UUID.randomUUID().toString();
		
		// Put "traceId" into MDC
		MDC.put(TRACE_ID, traceId);
		
		long startTime = System.currentTimeMillis();
		
		try {
			// log incoming request
			log.info("Incoming request | Trace id={} | method={} | URL={} | IP={} ",traceId,request.getMethod(),request.getRequestURI(),request.getRemoteAddr());
			
			// Continue request processing
			filterChain.doFilter(request, response);
		}catch (Exception ex) {
			log.error("Unexpected Error occured | Trace id={} | message={} ",traceId, ex.getMessage(),ex);
			throw ex;
		}finally {
			long timeTaken = System.currentTimeMillis() - startTime;
			
			// log output response
			log.info("Output response | Trace Id={} | status ={} | Time Taken ={} ",traceId,response.getStatus(),timeTaken);
			
			// clean up MDC
			MDC.remove(TRACE_ID);
		}
		
		
		
		
		
	}

}
