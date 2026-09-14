package com.example.demoSecurity.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {

        http
                // =============================================
                // 1.  Tắt CSRF - Bắt buộc phải có để test POST/PUT/DELETE trên Postman
                // =============================================
                .csrf(csrf -> csrf.disable())


                // =============================================
                // 2. SESSION - Không ghi nhớ trạng thái
                // =============================================
                .sessionManagement(session -> session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS
                ))


                // =============================================
                // 3. AUTHORIZATION
                // =============================================
                .authorizeHttpRequests(auth -> auth
                        // Không cần đăng nhập
                        .requestMatchers("/api/public/products").permitAll()

                        // auth api không cần đăng nhập
                        .requestMatchers("/api/auth/**").permitAll()

                        // GET /api/products
                        .requestMatchers(HttpMethod.GET, "/api/products").hasAnyRole("USER", "STAFF", "MANAGER", "ADMIN")

                        // POST /api/products
                        .requestMatchers(HttpMethod.POST, "/api/products").hasAnyRole("STAFF", "MANAGER", "ADMIN")

                        // PUT /api/products/{id}
                        .requestMatchers(HttpMethod.PUT, "/api/products/*").hasAnyRole("STAFF", "MANAGER", "ADMIN")

                        // DELETE /api/products/{id}
                        .requestMatchers(HttpMethod.DELETE, "/api/products/*").hasAnyRole("MANAGER", "ADMIN")

                        // GET /api/reports
                        .requestMatchers("/api/reports").hasAnyRole("MANAGER", "ADMIN")

                        // GET /api/admin/users
                        .requestMatchers("/api/admin/users").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )


                // =============================================
                // 5. TẮT BASIC AUTHENTICATION
                // =============================================
                .httpBasic(httpBasic -> httpBasic.disable())


                // =============================================
                // 6. JWT FILTER -   Thêm cơ chế xác thực JWT
                // =============================================
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );


        return http.build();
    }


    // =========================================================
    // PASSWORD ENCODER
    // =========================================================
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // =========================================================
    // AUTHENTICATION MANAGER
    // =========================================================
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
