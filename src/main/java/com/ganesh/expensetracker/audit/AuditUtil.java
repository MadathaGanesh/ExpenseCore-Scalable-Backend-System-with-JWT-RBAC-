package com.ganesh.expensetracker.audit;

import org.springframework.stereotype.Component;

import com.ganesh.expensetracker.entity.User;
import com.ganesh.expensetracker.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;

/****
 * Utility class to extract "current logged-in user" details for audit logging.
 * 
 * Why this class ?
 * - Avoid using security context logic everywhere
 * - central place to handle anonymous error
 */

@Component
@RequiredArgsConstructor
public class AuditUtil {

	private final UserServiceImpl userService;

	 /**
     * Fetch current logged-in user's email.
     * 
     * @return email if authenticated, otherwise "ANONYMOUS"
     */
	public String getCurrentLoggedinUser() {
		try {
			User user = userService.getCurrentUserDetails();
			return user.getEmail();
		}catch(Exception ex) {
			return "ANONYMOUS";
		}
	}
	
	
	
	
}
