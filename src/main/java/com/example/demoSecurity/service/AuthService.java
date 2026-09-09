package com.example.demoSecurity.service;

import com.example.demoSecurity.dto.request.UserLoginDTO;
import com.example.demoSecurity.dto.request.UserRequestDTO;
import com.example.demoSecurity.dto.response.UserResponseDTO;
import com.example.demoSecurity.entity.User;
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
    public String login(UserLoginDTO loginDTO) {
        // kiểm tra username + password ~ tìm user, map mật khẩu
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getUsername(),
                        loginDTO.getPassword()
                )
        );
        // lấy thông tin người dùng
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return jwtService.generateToken(userDetails);

    }
}
