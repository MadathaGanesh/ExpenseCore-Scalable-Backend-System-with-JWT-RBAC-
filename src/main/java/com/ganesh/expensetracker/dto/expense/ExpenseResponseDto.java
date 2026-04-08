package com.ganesh.expensetracker.dto.expense;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/***
 *  DTO for returning expense Data to client
 */

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseResponseDto {
	
	private Long id;
	
	private String expenseName;
	
	private String description;
		
	private BigDecimal amount;
	
	private LocalDateTime createdDate;
	
	private LocalDateTime updatedDate;
}




