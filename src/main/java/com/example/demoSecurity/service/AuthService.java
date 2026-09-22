package com.example.demoSecurity.service;

import com.example.demoSecurity.dto.request.RefreshTokenRequestDTO;
import com.example.demoSecurity.dto.request.UserLoginDTO;
import com.example.demoSecurity.dto.request.UserRequestDTO;
import com.example.demoSecurity.dto.response.LoginResponseDTO;
import com.example.demoSecurity.dto.response.UserResponseDTO;
import com.example.demoSecurity.entity.RefreshToken;
import com.example.demoSecurity.entity.User;
import com.example.demoSecurity.exception.ResourceNotFoundException;
import com.example.demoSecurity.mapper.UserMapper;
import com.example.demoSecurity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final CustomUserDetailsService customUserDetailsService;

    // Đăng ký
    public UserResponseDTO signIn(UserRequestDTO requestDTO) {
        User newUser = new User();
        newUser.setUsername(requestDTO.getUsername());
        newUser.setRole(requestDTO.getRole());
        newUser.setEnabled(true);

        //password phải hash
        String passwordEncrypted = passwordEncoder.encode(requestDTO.getPassword());
        newUser.setPassword(passwordEncrypted);

        userRepository.save(newUser);
        return userMapper.toResponse(newUser);
    }

    // Đăng nhập (Access + Refresh + Family)
    public LoginResponseDTO login(UserLoginDTO loginDTO) {
        // kiểm tra username + password ~ tìm user, map mật khẩu
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getUsername(),
                        loginDTO.getPassword()
                )
        );
        // lấy thông tin người dùng đã xác thực
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // sinh token
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        // tạo token family (family_id chỉ được lưu duy nhất 1 lần khi login)
        String familyId = UUID.randomUUID().toString();

        // lưu refreshToken vào DB khi đăng nhập
        refreshTokenService.save(refreshToken, userDetails.getUsername(), familyId);
        LoginResponseDTO responseDTO = new LoginResponseDTO();
        responseDTO.setAccessToken(accessToken);
        responseDTO.setRefreshToken(refreshToken);

        return responseDTO;

    }

    // Vô hiệu hoá tài khoản
    public void disableUser(int id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Không tìm thấy tài khoản có id: " + id
                )
        );

        // Vô hiệu hóa tài khoản
        user.setEnabled(false);
        userRepository.save(user);
    }


    // Phương thức dùng refreshToken tạo accessToken mới (Rotation + Reuse Detection + Token Family)
    public LoginResponseDTO refreshToken(RefreshTokenRequestDTO refreshToken) {
        // 1. Lấy Refresh Token từ request
        String token = refreshToken.getRefreshToken();

        // 2. tìm trong DB
        RefreshToken refreshTokenInDB = refreshTokenService.findByToken(token);

        // 3.kiểm tra refresh token đã revoke chưa, kiểm tra Reuse
        if (refreshTokenInDB.isRevoked()) {
            String familyId = refreshTokenInDB.getFamilyId();

            // Reuse detected → revoke toàn bộ Family
            refreshTokenService.revokeFamily(familyId);

            throw new RuntimeException("Refresh token reuse detected");
        }
        // 4.kiểm tra xem refreshToken còn hạn không
        refreshTokenService.verifyToken(refreshTokenInDB);

        // 5.lấy username
        String username = refreshTokenInDB.getUsername();

        // 6.lấy userDetails
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        // 7.tạo accessToken mới
        String newAccessToken = jwtService.generateAccessToken(userDetails);

        // 8.tạo refreshToken mới và lưu trong DB
        String newRefreshToken = jwtService.generateRefreshToken(userDetails);

        // 9.vô hiệu hoá refresh token trong DB
        refreshTokenService.revoke(refreshTokenInDB);

        String family_id = refreshTokenInDB.getFamilyId();
        refreshTokenService.save(newRefreshToken, userDetails.getUsername(), family_id);

        // 10.Lưu Refresh Token mới
        LoginResponseDTO responseDTO = new LoginResponseDTO();
        responseDTO.setAccessToken(newAccessToken);
        responseDTO.setRefreshToken(newRefreshToken);
        return responseDTO;
    }
}
