package com.ganesh.expensetracker.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "tbl_expenses", indexes = 
		{
		@Index(name ="idx_user_id" ,columnList ="user_id" ),
		@Index(name = "idx_category_id", columnList = "category_id")
		})
@EntityListeners(AuditingEntityListener.class)
public class Expense {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "expense_id")
	private Long id;
	
	@Column(name = "expense_name", nullable = false)
	private String name;
	
	@Column(name = "expense_description")
	private String description;
	
	@Column(name = "amount", precision = 10, scale = 2)
	private BigDecimal amount;
	
	@Column(name ="created_at")
	@CreationTimestamp
	private LocalDateTime createdDate;
	
	@Column(name = "updated_at")
	@UpdateTimestamp
	private LocalDateTime updatedDate;
	
	 /**
     * Many-to-One relationship with User entity.
     * Many expense belongs to one user.
     * 
     * FetchType.LAZY → User data loads only when explicitly accessed.
     * nullable = false → Expense must always belong to a user.
     * OnDelete CASCADE → If a user is deleted, their expenses are also deleted.
     * JsonIgnore → Prevents user data from being serialized in API responses.
     */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name="user_id", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private User user;
	
	/**
	 * ManyToOne Relationship : Many expenses belong to one Category 
	 * OnDeleteAction. RESTRICT : Means first we need to delete all expenses before we delete category.
	 * */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "category_id", nullable = false)
	@OnDelete(action = OnDeleteAction.RESTRICT)
	private Category category;
}
