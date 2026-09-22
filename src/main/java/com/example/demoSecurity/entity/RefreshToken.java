package com.example.demoSecurity.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// Tạo entity để lưu refreshToken vào trong DB để sau này so sánh khi đăng nhập
@Entity
@Table(name = "refresh_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "token", length = 500, nullable = false)
    private String token;

    @Column(name = "username", length = 100, nullable = false)
    private String username;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Column(name = "revoked", nullable = false)
    private boolean revoked;

    // Family_id chỉ tạo 1 lần duy nhất khi login
    @Column(name = "family_id")
    private String familyId;
}
