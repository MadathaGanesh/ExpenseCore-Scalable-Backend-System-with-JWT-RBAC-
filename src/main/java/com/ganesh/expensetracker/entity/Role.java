package com.ganesh.expensetracker.entity;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbl_role")
public class Role {
	
	@Id
	@Column(name = "role_id")
	private Long id;
	
	@Column(unique = true,nullable = false)
	private String name;
	
	@ManyToMany(fetch = FetchType.EAGER)
	private Set<Permission> permissions;
}



