package com.ganesh.expensetracker.config;

// Spring Annotation for configuration and Bean Creation.
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
// OpenAPI classes used to define OpenAPI documentation metadata
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 *  Configuration class for Swagger / OpenAPI documentation.
 * 
 *  This class defines a custom OpenAPI bean, that configures the metadata(title, version, description) for the API docs.
 */
@Configuration
public class SwaggerConfig {

	/**
	 * Creates and configures the OpenAPI bean for Swagger documentation.
	 * 
	 * This includes:
	 * 1. Basic API metadata (title, version, description)
	 * 2. JWT-Based security configuration for protected endpoints.
	 * 
	 * @return Customized OpenAPI object
	 */
 
    @Bean
     OpenAPI customOpenApi() {
    	return new OpenAPI()
    			
    			// API Information shown in Swagger Documentation.
    			.info(new Info()
    					.title("Smart Expense Tracker API")
    					.description("API documentation for Expense Tracker with JWT Security")
    					.version("1.0")
    					)
    			
    			// Add Global Security Requirement (Applicable to all API endpoints)
    			// This tells swagger, that every endpoint require authentication
    			.addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
    			
    			// Define Security schema Details
    			.components(new Components()
    					.addSecuritySchemes("BearerAuth", new SecurityScheme()
    							.name("Authorization") // HTTP Header Name
    							.type(SecurityScheme.Type.HTTP)  // Type: HTTP Authentication
    							.scheme("bearer") // scheme : Bearer Token
    							.bearerFormat("JWT") // Token Format: JWT 
    							)
    					);
	}
}
	
