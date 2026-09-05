package com.example.demoSecurity.service;

import com.example.demoSecurity.dto.request.UserLoginDTO;
import com.example.demoSecurity.dto.request.UserRequestDTO;
import com.example.demoSecurity.dto.response.UserResponseDTO;
import com.example.demoSecurity.entity.User;
import com.example.demoSecurity.mapper.UserMapper;
import com.example.demoSecurity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

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
    public UserResponseDTO login(UserLoginDTO loginDTO) {
        // kiểm tra tên đăng nhập có tồn tại không
        User user = userRepository.findByUsername(loginDTO.getUsername());

        // Sau đó mới kiểm tra password nhập vào có trùng với pass đã hash không
        boolean passwordMatches = passwordEncoder.matches(loginDTO.getPassword(), user.getPassword());

        if (passwordMatches) {
            return userMapper.toResponse(user);
        } else {
            return null;
        }
    }
}
