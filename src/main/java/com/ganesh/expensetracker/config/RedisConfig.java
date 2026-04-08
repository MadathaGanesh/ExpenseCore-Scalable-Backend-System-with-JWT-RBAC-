package com.ganesh.expensetracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/***
 *  Redis configuration class:
 *  
 *  Purpose:
 *  - Defines how spring interact with Redis
 *  - Configures Key/Value serialization
 *  
 *  Why String serializer?
 *  - JWT Tokens are Strings
 *  - Avoid unnecessary object serialization overhead
 * 
 */

@Configuration
public class RedisConfig {
	
	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory){
		
		  if (factory == null) {
	            throw new IllegalStateException("RedisConnectionFactory is NULL → Redis not configured properly");
	        }
		
		RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
		
		template.setConnectionFactory(factory);
				
	    // set "Keys" as plain strings
		template.setKeySerializer(new StringRedisSerializer());
		
		// set "values" as plain strings
		template.setValueSerializer(new StringRedisSerializer());
		
		template.afterPropertiesSet();
		
		return template;
	}

}

