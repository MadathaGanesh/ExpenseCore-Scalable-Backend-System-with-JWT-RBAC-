package com.ganesh.expensetracker.audit;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.ganesh.expensetracker.service.AuditService;

import lombok.RequiredArgsConstructor;

/***
 *-- Audit Aspect and AOP Logging 
 * 
 * AOP Aspect for capturing BUSINESS LEVEL audit logs.
 * 
 * what it logs ?
 * - Says which controller method executed.
 * - success or failure of method
 * 
 * Why AOP?
 * - Avoid manual logging in every controller
 * - centralized logging logic
 */

@Component
@Aspect
@RequiredArgsConstructor
public class AuditAspect {
	
	private final AuditService auditService;
	private final AuditUtil auditUtil;
	
	
	/**
	 * Pointcut to target all controller methods
	 */
	@Pointcut("execution(* com.ganesh.expensetracker.controller..*(..))")
	public void controllerLayer() {}
	
	
	/**
	 * Log success execution of every controller methods
	 */
	@AfterReturning(pointcut = "controllerLayer()")
	public void logSuccess(JoinPoint joinPoint) {
		String methodName = joinPoint.getSignature().getName();
		auditService.log("SUCCESS_"+methodName, 
				auditUtil.getCurrentLoggedinUser(),
				joinPoint.getSignature().toShortString());
	}

	
	/**
	 * Log failure execution of every controller method
	 **/
	@AfterThrowing(pointcut = "controlllerLayer()",throwing = "ex")
	public void logFailure(JoinPoint joinPoint, Exception ex) {
		String methodName = joinPoint.getSignature().getName();
		auditService.log("ERROR_"+methodName +" : "+ex.getMessage(), 
				auditUtil.getCurrentLoggedinUser(), 
				joinPoint.getSignature().toShortString());
	}
}
