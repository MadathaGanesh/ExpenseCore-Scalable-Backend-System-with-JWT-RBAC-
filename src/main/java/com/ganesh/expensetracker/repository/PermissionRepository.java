package com.ganesh.expensetracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ganesh.expensetracker.entity.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

}
