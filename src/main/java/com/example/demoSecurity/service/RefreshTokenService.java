package com.example.demoSecurity.service;

import com.example.demoSecurity.entity.RefreshToken;
import com.example.demoSecurity.exception.ResourceNotFoundException;
import com.example.demoSecurity.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    @Value("${jwt.expiration-refresh}")
    private long expirationRefresh;

    public RefreshToken save(String token, String username) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUsername(username);
        refreshToken.setExpiryDate(LocalDateTime.now().plusSeconds(expirationRefresh / 1000));
        return refreshTokenRepository.save(refreshToken);
    }

    // tìm refreshToken
    public RefreshToken findByToken(String tokenRefresh) {
        return refreshTokenRepository.findByToken(tokenRefresh).orElseThrow(
                () -> new ResourceNotFoundException("Không tìm thấy refresh token")
        );
    }

    // verify token
    public void verifyToken(RefreshToken refreshToken) {
        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new ResourceNotFoundException("Refresh token đã hết hạn");
        }
    }
}
