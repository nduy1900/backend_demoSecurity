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

    // Đăng nhập
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

        // lưu refreshToken vào DB khi đăng nhập
        refreshTokenService.save(refreshToken, userDetails.getUsername());
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


    // Phương thức dùng refreshToken tạo accessToken mới
    public LoginResponseDTO refreshToken(RefreshTokenRequestDTO refreshToken) {
        String token = refreshToken.getRefreshToken();

        // tìm trong DB
        RefreshToken refreshTokenInDB = refreshTokenService.findByToken(token);

        // kiểm tra xem refreshToken còn hạn không
        refreshTokenService.verifyToken(refreshTokenInDB);

        // lấy username
        String username = refreshTokenInDB.getUsername();

        // lấy userDetails
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        // tạo accessToken mới
        String newAccessToken = jwtService.generateAccessToken(userDetails);

        LoginResponseDTO responseDTO = new LoginResponseDTO();
        responseDTO.setAccessToken(newAccessToken);
        responseDTO.setRefreshToken(token);
        return responseDTO;
    }
}
