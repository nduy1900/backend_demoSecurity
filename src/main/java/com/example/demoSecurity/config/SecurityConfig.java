package com.example.demoSecurity.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

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


                                // Nếu dùng .hasRole thì không cần thêm tiền tố "ROLE_"
                                .requestMatchers("/api/admin/users").hasRole("ADMIN")

                                // Nếu dùng .hasAuthority phải thêm tiền tố "ROLE_"
//                                .requestMatchers("/api/admin/users").hasAuthority("ROLE_ADMIN")


//                                // GET /api/products
//                                .requestMatchers("/api/products").hasAnyRole("USER", "STAFF", "MANAGER", "ADMIN")
//
//                                // POST /api/products
//                                .requestMatchers(HttpMethod.POST, "/api/products").hasAnyRole("STAFF", "MANAGER", "ADMIN")
//
//                                // PUT /api/products/{id}
//                                .requestMatchers(HttpMethod.PUT, "/api/products/*").hasAnyRole("STAFF", "MANAGER", "ADMIN")
//
//                                // DELETE /api/products/{id}
//                                .requestMatchers(HttpMethod.DELETE, "/api/products/*").hasAnyRole("MANAGER", "ADMIN")
//
//                                // GET /api/reports
//                                .requestMatchers("/api/reports").hasAnyRole("MANAGER", "ADMIN")

                                .anyRequest().authenticated()
                )


                // =============================================
                // 5. TẮT BASIC AUTHENTICATION
                // =============================================
                .httpBasic(httpBasic -> httpBasic.disable())


                // =============================================
                // 6. XÁC THỰC JWT BẰNG OAUTH2 RESOURCE SERVER
                // =============================================
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));


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


    //    JwtAuthenticationConverter lấy thông tin từ JWT và tạo ra Authentication cho Spring Security.
    // =========================================================
    // JwtAuthenticationConverter
    // =========================================================
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();

        // Lấy quyền từ claim "role"
        authoritiesConverter.setAuthoritiesClaimName("role");

        // JWT đã có ROLE_USER nên không thêm tiền tố
        authoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(
                authoritiesConverter
        );
        return converter;
    }

}
