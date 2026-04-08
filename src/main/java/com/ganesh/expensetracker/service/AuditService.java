package com.ganesh.expensetracker.service;

public interface AuditService {
	
	public void log(String action, String user, String endpoint);

}
