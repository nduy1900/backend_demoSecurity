package com.example.demoSecurity.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {
    @NotBlank(message = "Username không được bỏ trống")
    private String username;

    @NotBlank(message = "Password không được bỏ trống")
    private String password;

    @NotBlank(message = "Quyền không được bỏ trống")
    private String role;
    
}
