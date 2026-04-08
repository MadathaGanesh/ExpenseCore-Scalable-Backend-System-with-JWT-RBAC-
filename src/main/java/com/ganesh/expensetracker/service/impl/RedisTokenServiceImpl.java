package com.ganesh.expensetracker.service.impl;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.ganesh.expensetracker.service.RedisTokenService;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of RedisTokenService
 * 
 * Core Logic:
 * - Store blacklisted tokens in Redis
 * - Use TTL to auto-remove expired tokens
 * 
 * Why Redis?
 * - Fast lookup (O(1))
 * - Automatic expiry handling
 * - No DB cleanup needed
 */
@Service
@RequiredArgsConstructor
public class RedisTokenServiceImpl implements RedisTokenService{

	/***
	 *  RedisTemplate  is used to interact with Redis
	 *  Key = String
	 *  Value = String
	 */
	private final RedisTemplate<String, Object> redisTemplate;
	
	// Prefix to avoid key Collision in Redis
	private static final String PREFIX = "blacklist";
	
	/****
	 *  Adds a token to the "Redis BlackList"
	 */
	@Override
	public void blacklistToken(String token, long expireMillis) {
		
		String key = PREFIX+token;
		
		// Store Token with TTL
		redisTemplate.opsForValue().set(
				key,  // Key
				"BLACKLISTED",  // Value
				expireMillis, // TTL Seconds
				TimeUnit.MILLISECONDS  // TTL Unit
				);
		}

	/***
	 * check whether a token is blacklisted or not
	 * 
	 * @param token
	 * @return true, if token exists in Redis, otherwise false
	 * 
	 * Logic:
	 * - If token exists as a key in redis -> then it is blacklisted
	 * - If not -> its valid (or never blacklisted)
	 */
	@Override
	public boolean isBlackListed(String token) {
		String key = PREFIX+token;
		return Boolean.TRUE.equals(redisTemplate.hasKey(key));
	}

}


