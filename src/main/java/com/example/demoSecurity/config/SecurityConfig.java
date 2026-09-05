package com.example.demoSecurity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
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
                        .requestMatchers(HttpMethod.GET, "/api/public/products").permitAll()

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
                        .requestMatchers(HttpMethod.GET, "/api/reports").hasAnyRole("MANAGER", "ADMIN")

                        // GET /api/admin/users
                        .requestMatchers(HttpMethod.GET, "/api/admin/users").hasAnyRole("ADMIN")

                        .anyRequest().authenticated()
                ).httpBasic(Customizer.withDefaults());
        return http.build();
    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails user = User.withUsername("user01")
//                .password("$2a$10$y/shyZb9tzn/UMYu4ev3cuFdDSQjaIndYyZKYtTax8IAvu0VyT.SW")
//                .roles("USER")
//                .build();
//
//        UserDetails staff = User.withUsername("staff01")
//                .password("{noop}123456")
//                .roles("STAFF")
//                .build();
//
//        UserDetails manager = User.withUsername("manager01")
//                .password("{noop}123456")
//                .roles("MANAGER")
//                .build();
//
//        UserDetails admin = User.withUsername("admin")
//                .password("{noop}admin123")
//                .roles("ADMIN")
//                .build();
//
//        return new InMemoryUserDetailsManager(user, staff, manager, admin);
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
