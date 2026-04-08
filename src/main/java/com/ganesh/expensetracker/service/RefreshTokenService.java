package com.ganesh.expensetracker.service;

import com.ganesh.expensetracker.entity.RefreshToken;

public interface RefreshTokenService {
	
	public RefreshToken createRefreshToken(String email);
	
	public RefreshToken verifyRefreshToken(String token);

}
