package com.ganesh.expensetracker.logging;

public class LogConstants {
	
	/***
	 *  Centralized constants for log messages used across the application.
	 *  
	 *  Why this exists:
	 *  - Avoids repeating hardcoded String ("magic Strings")
	 *  - Ensures consistency in logs
	 *  - Makes it easy to update log messages globally
	 *  - Reduces typos and duplicate variations
	 *  
	 *  Example problem without this:
	 *  - "Fetching user details"
	 *  - "Fetch user details"
	 *  - "Getting user info"
	 *  
	 *  Above all refer to same action but break log searchability.
	 */
	
	public static final String REQUEST_START = "Request started";
	public static final String REQUEST_END = "Request completed";
	
	public static final String USER_FETCH = "Fetching user Details";
	public static final String EXPENSE_CREATE = "Creating Expense";
	public static final String EXPENSE_DELETE = "Delete Expense";
	
	
	/****
	 *  Private constructor to prevent instantiation of this utility class.
	 *  
	 *  This class only contains static constants and is not meant to be instantiated.
	 *  Any attempt to create an object of this class is logically incorrect.
	 *  We can access static variable values using "class name", without creating object.
	 *  so the constructor is made private to enforce this at compile time.
	 */
	private LogConstants() {}

}
