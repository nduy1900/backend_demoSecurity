package com.example.demoSecurity.config;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.SecretKey;

@Configuration
public class JwtConfig {
    @Value("${jwt.secret-key}")
    private String secretkey;


    // JWT Decoder dùng để xác thực accessToken để thực hiện request tiếp theo
    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretkey));
        return NimbusJwtDecoder
                .withSecretKey(key)
                .build();
    }
}
