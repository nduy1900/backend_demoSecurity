package com.example.demoSecurity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        //  TẮT CSRF - Bắt buộc phải có để test POST/PUT/DELETE trên Postman
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Không cần đăng nhập
                        .requestMatchers("/api/public/products").permitAll()

                        // auth api không cần đăng nhập
                        .requestMatchers("/api/auth/**").permitAll()

                        // GET /api/products
                        .requestMatchers("/api/products").hasAnyRole("USER", "STAFF", "MANAGER", "ADMIN")

                        // POST /api/products
                        .requestMatchers("/api/products").hasAnyRole("STAFF", "MANAGER", "ADMIN")

                        // PUT /api/products/{id}
                        .requestMatchers("/api/products/*").hasAnyRole("STAFF", "MANAGER", "ADMIN")

                        // DELETE /api/products/{id}
                        .requestMatchers("/api/products/*").hasAnyRole("MANAGER", "ADMIN")

                        // GET /api/reports
                        .requestMatchers("/api/reports").hasAnyRole("MANAGER", "ADMIN")

                        // GET /api/admin/users
                        .requestMatchers("/api/admin/users").hasAnyRole("ADMIN")

                        .anyRequest().authenticated()
                ).httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
