package com.example.demoSecurity.controller;

import com.example.demoSecurity.dto.request.UserLoginDTO;
import com.example.demoSecurity.dto.request.UserRequestDTO;
import com.example.demoSecurity.dto.response.UserResponseDTO;
import com.example.demoSecurity.service.AuthService;
import com.example.demoSecurity.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    // Đăng ký
    @PostMapping("/signIn")
    public ResponseEntity<ApiResponse<UserResponseDTO>> signIn(@Valid @RequestBody UserRequestDTO request) {
        return ResponseEntity.status(200).body(new ApiResponse<>(
                200,
                "Đăng ký thành công",
                authService.signIn(request)
        ));
    }


    // Đăng nhập
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponseDTO>> login(@Valid @RequestBody UserLoginDTO loginDTO) {
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "Đăng nhập thành công",
                authService.login(loginDTO)
        ));
    }
}
