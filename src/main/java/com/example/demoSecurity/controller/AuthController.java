package com.example.demoSecurity.controller;

import com.example.demoSecurity.dto.request.UserLoginDTO;
import com.example.demoSecurity.dto.request.UserRequestDTO;
import com.example.demoSecurity.dto.response.UserResponseDTO;
import com.example.demoSecurity.service.UserService;
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
    private final UserService userService;

    // Đăng ký
    @PostMapping("/signIn")
    public ResponseEntity<ApiResponse<UserResponseDTO>> signIn(@Valid @RequestBody UserRequestDTO request) {
        return ResponseEntity.status(200).body(new ApiResponse<>(
                200,
                "Đăng ký thành công",
                userService.signIn(request)
        ));
    }

    // Đăng nhập
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponseDTO>> login(@Valid @RequestBody UserLoginDTO loginDTO) {
        if (userService.login(loginDTO) != null) {
            return ResponseEntity.status(200).body(new ApiResponse<>(
                    200,
                    "Đăng nhập thành công",
                    userService.login(loginDTO)
            ));
        } else {
            return ResponseEntity.status(401).body(new ApiResponse<>(
                    401,
                    "Đăng nhập thất bại",
                    null
            ));
        }
    }
}
