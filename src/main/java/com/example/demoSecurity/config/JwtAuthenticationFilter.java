package com.example.demoSecurity.config;

import com.example.demoSecurity.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;

// Class này để thực hiện xác thực JWT
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. Đọc Authorization Header
        String authHeader = request.getHeader("Authorization");

        // 2. Kiểm tra header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Cắt chữ "Bearer" để lấy JWT
        String jwt = authHeader.substring(7);


        // Xác thực JWT
        try {
            // 4. Lấy username từ JWT
            String username = jwtService.extractUsername(jwt);

            // 5. Chỉ authenticate nếu Context chưa có Authentication
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 6. Load UserDetails
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 7. Validate JWT
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // 8. Tạo Authentication
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    // gắn request detail
                    authenticationToken.setDetails(new WebAuthenticationDetails(request));
                    // 9. Đưa Authentication vào SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                }

            }
        } catch (Exception e) {

            // JWT không hợp lệ
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 10. Cho request đi tiếp
        filterChain.doFilter(request, response);
    }
}
