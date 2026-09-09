package com.example.demoSecurity.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {
    // Lấy secretkey trong application.properties
    @Value("${jwt.secret-key}")
    private String secretKey;

    // Lấy thời gian hết hạn trong application.properties
    @Value("${jwt.expiration}")
    private long expiration;

    // Sinh JWT
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
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSecretKey())
                .compact();
    }

    // Sinh secretkey
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(secretKey)
        );
    }
}
