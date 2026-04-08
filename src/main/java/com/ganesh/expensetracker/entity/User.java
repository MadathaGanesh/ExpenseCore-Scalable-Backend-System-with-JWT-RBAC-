package com.ganesh.expensetracker.entity;

import java.time.LocalDateTime;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "tbl_users")
public class User {
	
	@Id
	@Column(name ="user_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "user_name", nullable = false, length = 100)
	private String userName;
	
	@Column(unique = true, name ="email")
	private String email;
	
	@Column(name = "password")
	private String password;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable( name = "user_roles",
			joinColumns = @JoinColumn(referencedColumnName = "user_id"),
			inverseJoinColumns = @JoinColumn(referencedColumnName = "role_id"))	
	private Set<Role> role;
	
	@Column(name = "age")
	private Integer age;
	
	@CreationTimestamp
	@Column(name = "create_at", updatable = false)
	private LocalDateTime createAt;
	
	@Column(name = "updated_at")
	@UpdateTimestamp
	private LocalDateTime updatedAt;
	
	@Column(name = "is_active")
	private boolean isActive;

	
	
	public boolean isAdmin() {
		return this.getRole().stream().anyMatch(role-> role.getName().equals("ROLE_ADMIN"));
	}
}
