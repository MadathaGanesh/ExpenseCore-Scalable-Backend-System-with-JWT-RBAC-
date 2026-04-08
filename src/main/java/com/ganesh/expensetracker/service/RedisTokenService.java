package com.ganesh.expensetracker.service;


/**
 * Service interface for handling JWT blacklist logic using Redis
 */
public interface RedisTokenService {
	
	/***
	 * Add token to Redis blacklist
	 * 
	 * @param token JWT Token
	 * @param expireMillis remaining validity time in milliseconds.
	 */
	public void blacklistToken(String token, long expireMillis);
	
	
	/**
	 * Check if token is blacklist or not
	 * 
	 * @param token JWT Token
	 * @return true : if token is blacklisted
	 */
	public boolean isBlackListed(String token);
	

}
