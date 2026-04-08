package com.ganesh.expensetracker.dto.expense;

import java.math.BigDecimal;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/***
 *  DTO for partial Expense Update
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpensePatchDTO {

	// Expense name (optional for PATCH)
	private String expenseName;
	
	// Expense description (optional for PATCH)
	private String description;
	
	// Expense amount (optional for PATCH)
	@PositiveOrZero(message ="amount should not be negative")
	private BigDecimal amount;
	
	// Expense category (optional for PATCH)
	private Long categoryId;
	
	
    /*
     * Checks whether PATCH request body is empty.
     *
     * Returns:
     * --------
     * true  -> No fields provided by client (invalid PATCH request)
     * false -> At least one field is present for update
     *
     * Usage:
     * ------
     * if(dto.isEmpty()) {
     *     throw new EmptyPatchException("Patch body cannot be empty");
     * }
     */
	public boolean isPatchEmpty() {
		return expenseName == null 
			 && description == null
			 && amount == null 
			 && categoryId == null;
	}
}
