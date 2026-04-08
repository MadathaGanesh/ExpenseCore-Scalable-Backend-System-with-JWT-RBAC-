package com.ganesh.expensetracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ganesh.expensetracker.entity.AuditLog;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long>{

}
