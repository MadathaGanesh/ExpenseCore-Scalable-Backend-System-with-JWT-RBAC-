package com.ganesh.expensetracker.exception;

public class EmptyPatchException extends RuntimeException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EmptyPatchException(String message)
	{
		super("Patch body cannot be empty ...");
	}
}
