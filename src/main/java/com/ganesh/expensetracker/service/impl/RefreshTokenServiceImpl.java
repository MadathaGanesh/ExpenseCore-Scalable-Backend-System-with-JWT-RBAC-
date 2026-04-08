package com.ganesh.expensetracker.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ganesh.expensetracker.entity.RefreshToken;
import com.ganesh.expensetracker.entity.User;
import com.ganesh.expensetracker.exception.ItemNotFound;
import com.ganesh.expensetracker.repository.RefreshTokenRepository;
import com.ganesh.expensetracker.repository.UserRepository;
import com.ganesh.expensetracker.service.RefreshTokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService{
	
	private final long refreshTokenDuration = 7L * 24 * 60 * 60 * 1000; // 7 days
	
	private final UserRepository userRepo;
	private final RefreshTokenRepository refreshTokenRepo;

	@Override
	public RefreshToken createRefreshToken(String email) {
		User user = userRepo.findByEmail(email).orElseThrow(()-> new ItemNotFound("Email not found : "+email));
		
		RefreshToken refreshToken = new RefreshToken();
		refreshToken.setUser(user);
		refreshToken.setToken(UUID.randomUUID().toString());
		refreshToken.setExpiryDate(LocalDateTime.now().plusSeconds(refreshTokenDuration));
		refreshTokenRepo.save(refreshToken);
		return refreshToken;
	}

	@Override
	public RefreshToken verifyRefreshToken(String token) {
		RefreshToken refreshToken = refreshTokenRepo.findByToken(token).orElseThrow(()->new ItemNotFound("Invalid refresh token"));
		
		if(refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
			refreshTokenRepo.delete(refreshToken);
			log.warn(" Refresh Token expired | Invalid Token ");
			throw new RuntimeException("Refresh Token expired");
		}
		return refreshToken;
	}

}
