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

/***
 *  This filter ensures every request has a unique Correlation ID.
 *  
 *  Why?
 *  - Helps trace logs across microservices / Project
 *  - Useful for debugging in Distributed Systems
 *  - Allows tracking a request end-to-end.
 */

@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

	
	// Header used to pass Correlation ID between services.
	private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
	
	// Key used to store Correlation ID in MDC (logging context)
	private static final String MDC_KEY = "correlationId";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		try {

		/***
		 *  Step 1: Extract Correlation ID from Header.
		 *  
		 *  If client already sent one, we reuse the same to maintain trace continuity.
		 */
		String correlationId = request.getHeader(CORRELATION_ID_HEADER);
		
		/**
		 *  Step 2: Generate a new Correlation ID if it is missing.
		 *  
		 *  This ensures every request has a unique Id
		 */
		if(correlationId==null || correlationId.trim().isBlank()) {
			correlationId = UUID.randomUUID().toString();
		}
		
		/***
		 * Step 3: Store correlationId in MDC (Mapped Diagnostic Context)
		 * 
		 * MDC is a thread-local storage used by logging frameworks.
		 * Once set, all logs in this thread can include this Id automatically.
		 */
		MDC.put(MDC_KEY, correlationId);
		
		/***
		 * Step 4: Add correlationId to the response headers
		 * 
		 *  This helps:
		 *  - Clients log in
		 *  - Debug issues using response logs
		 */
		response.setHeader(CORRELATION_ID_HEADER, correlationId);
		
		/***
		 *  Step 5: Continue filter chain
		 *  
		 *  This passes control to :
		 *  - Next Filter (or) Controller
		 */
		filterChain.doFilter(request, response);
		}
		finally {
			
			/**
			 *  Step 6: Clear MDC (Mapped Diagnostic Context)
			 *  
			 *  Since MDC is thread-local and threads are reused in pools, If we fail to clear. It can cause:
			 *  - Memory Leaks
			 *  - Wrong correlation IDs in unrelated request.
			 */
			MDC.remove(MDC_KEY);			
		}
	} 

}



/***
Without this correlationId: (Messy and we can't find it properly)

User fetched
Expense created
Error occurred

With this correlationId: (we have unique id)

[CID:123] User fetched
[CID:123] Expense created
[CID:123] Error occurred

 */




