package com.ganesh.expensetracker.dto.expense;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/***
 *  DTO for creating or Updating Expense
 */

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseRequestDto {
	
	@NotBlank(message = "Expense Name must not be null ..")
	@Size(min = 2, message = "must contain atleast 2 characters .")
	private String expenseName;
	
	@NotBlank(message = "Description must not be empty ..")
	private String description;
	
	@NotNull(message = "amount must not be null")
	@PositiveOrZero(message ="amount should not be negative")
	private BigDecimal amount;
	
	@NotNull(message = "category ID must not be blank ")
	private Long categoryId;

}
