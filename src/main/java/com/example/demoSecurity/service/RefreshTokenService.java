package com.example.demoSecurity.service;

import com.example.demoSecurity.entity.RefreshToken;
import com.example.demoSecurity.exception.ResourceNotFoundException;
import com.example.demoSecurity.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    @Value("${jwt.expiration-refresh}")
    private long expirationRefresh;

    public RefreshToken save(String token, String username, String family_id) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUsername(username);
        refreshToken.setExpiryDate(LocalDateTime.now().plusSeconds(expirationRefresh / 1000));
        refreshToken.setFamilyId(family_id);
        return refreshTokenRepository.save(refreshToken);
    }

    // tìm refreshToken
    public RefreshToken findByToken(String tokenRefresh) {
        return refreshTokenRepository.findByToken(tokenRefresh).orElseThrow(
                () -> new ResourceNotFoundException("Không tìm thấy refresh token")
        );
    }

    // verify token (kiểm tra thời gian sống của Refresh Token)
    public void verifyToken(RefreshToken refreshToken) {
        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new ResourceNotFoundException("Refresh token đã hết hạn");
        }
    }


    // Vô hiệu hóa Refresh Token
    public RefreshToken revoke(RefreshToken refreshToken) {
        refreshToken.setRevoked(true);
        return refreshTokenRepository.save(refreshToken);
    }

    // REVOKE TOÀN BỘ TOKEN TRONG FAMILY
    public void revokeFamily(String familyId) {
        List<RefreshToken> tokens = refreshTokenRepository.findByFamilyId(familyId);

        tokens.forEach(token ->
                token.setRevoked(true)
        );
        refreshTokenRepository.saveAll(tokens);
    }
}
