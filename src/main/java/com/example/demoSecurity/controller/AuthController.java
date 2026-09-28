package com.example.demoSecurity.controller;

import com.example.demoSecurity.dto.request.ChangePasswordDTO;
import com.example.demoSecurity.dto.request.RefreshTokenRequestDTO;
import com.example.demoSecurity.dto.request.UserLoginDTO;
import com.example.demoSecurity.dto.request.UserRequestDTO;
import com.example.demoSecurity.dto.response.LoginResponseDTO;
import com.example.demoSecurity.dto.response.UserResponseDTO;
import com.example.demoSecurity.service.AuthService;
import com.example.demoSecurity.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@Valid @RequestBody UserLoginDTO loginDTO) {
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "Đăng nhập thành công",
                authService.login(loginDTO)
        ));
    }

    // Vô hiệu hoá tài khoản
    @PutMapping("/disable/{id}")
    public ResponseEntity<ApiResponse<Void>> disableUser(@PathVariable Integer id) {
        authService.disableUser(id);
        return ResponseEntity.ok(new ApiResponse<>(
                        200,
                        "Vô hiệu hóa tài khoản thành công",
                        null
                )
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> refresh(@Valid @RequestBody RefreshTokenRequestDTO refreshTokenRequest) {
        return ResponseEntity.ok(new ApiResponse<>(
                        200,
                        "Tạo mới accessToken thành công",
                        authService.refreshToken(refreshTokenRequest)
                )
        );
    }

    // Đăng xuất
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody RefreshTokenRequestDTO request) {
        authService.logout(request);
        return ResponseEntity.ok(new ApiResponse<>(
                        200,
                        "Logout thành công",
                        null
                )
        );
    }

    // Đăng xuất toàn bộ dựa vào username
    @PostMapping("/logout/all")
    public ResponseEntity<ApiResponse<Void>> logoutAll(@Valid @RequestBody RefreshTokenRequestDTO request) {
        authService.logoutAll(request);
        return ResponseEntity.ok(new ApiResponse<>(
                        200,
                        "Logout tất cả user thành công",
                        null
                )
        );
    }

    // Đổi mật khẩu dựa vào username
    @PostMapping("/changePassword")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordDTO request, Authentication authentication) {
        authService.changePassword(request, authentication);
        return ResponseEntity.ok(new ApiResponse<>(
                        200,
                        "Đổi mật khẩu thành công",
                        null
                )
        );
    }
}
