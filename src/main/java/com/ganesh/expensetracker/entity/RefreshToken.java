package com.ganesh.expensetracker.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "tbl_refresh_token")
@Setter
@Getter
public class RefreshToken {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "refresh_token_id")
	private Long id;
	
	private String token;
	
	private LocalDateTime expiryDate;
	
	@ManyToOne
	private User user;

}
