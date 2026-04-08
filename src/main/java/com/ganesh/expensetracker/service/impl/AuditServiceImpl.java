package com.ganesh.expensetracker.service.impl;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.ganesh.expensetracker.entity.AuditLog;
import com.ganesh.expensetracker.repository.AuditLogRepository;
import com.ganesh.expensetracker.service.AuditService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuditServiceImpl implements AuditService{
	
	private final AuditLogRepository auditRepo;


	@Override
	@Async
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void log(String action, String user, String endpoint) {
		System.out.println("Audit calling ");
		AuditLog logs = new AuditLog();
		logs.setUserName(user);
		logs.setAction(action);
		logs.setEndPoint(endpoint);
		logs.setTimeStamp(LocalDateTime.now());
		
		log.info("Creating logs for auditing ");
		auditRepo.save(logs);
	}

}
