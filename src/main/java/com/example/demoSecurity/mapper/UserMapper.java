package com.example.demoSecurity.mapper;

import com.example.demoSecurity.dto.response.UserResponseDTO;
import com.example.demoSecurity.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDTO toResponse(User user);
}
