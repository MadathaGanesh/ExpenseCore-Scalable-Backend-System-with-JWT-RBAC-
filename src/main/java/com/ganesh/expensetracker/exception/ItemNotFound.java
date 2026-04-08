package com.ganesh.expensetracker.exception;

public class ItemNotFound extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public ItemNotFound(String message) {
		super(message);
	}

}
