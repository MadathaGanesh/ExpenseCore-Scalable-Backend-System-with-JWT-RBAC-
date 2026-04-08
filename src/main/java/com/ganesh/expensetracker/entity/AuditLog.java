package com.ganesh.expensetracker.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tbl_auditlogs")
@Setter
@Getter
public class AuditLog {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String action;
	
	private String userName;
	
	private String endPoint;
	
	private LocalDateTime timeStamp;
	

}
