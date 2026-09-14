package com.example.demoSecurity.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {
    // Lấy secretkey trong application.properties
    @Value("${jwt.secret-key}")
    private String secretKey;

    // Lấy thời gian hết hạn trong application.properties
    @Value("${jwt.expiration}")
    private long expiration;

    // Sinh secretkey
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(secretKey)
        );
    }

    // 1. TẠO JWT TOKEN
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()

                // thông tin người dùng
                .subject(userDetails.getUsername())

                // custom claim
                // lấy role
                .claim(
                        "role", userDetails.getAuthorities().stream().findFirst()
                                .map(GrantedAuthority::getAuthority).orElse("")
                )
                // ngày tạo
                .issuedAt(new Date())
                // ngày hết hạn
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSecretKey())
                .compact();
    }


    // 2. LẤY USERNAME TỪ TOKEN
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 3. LẤY CLAIM
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claimsResolver.apply(claims);
    }

    // 4. KIỂM TRA TOKEN HỢP LỆ
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    // 5. KIỂM TRA TOKEN HẾT HẠN
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // 6. LẤY THỜI GIAN HẾT HẠN
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration
        );
    }
}
