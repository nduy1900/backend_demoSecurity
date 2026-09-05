package com.example.demoSecurity.controller;

import com.example.demoSecurity.util.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class HomeController {

    @GetMapping("/public/products")
    public String test() {
        return "Đây là endpoint public";
    }

    @GetMapping
    public ResponseEntity<ApiResponse<String>> getProducts() {
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "Truy cập thành công",
                "Bạn đang xem danh sách sản phẩm"
        ));
    }

    @GetMapping("/admin/users")
    public ResponseEntity<ApiResponse<String>> viewAllUser() {
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "Truy cập thành công",
                "Bạn đang xem danh sách tất cả user"
        ));
    }

    @GetMapping("/reports")
    public ResponseEntity<ApiResponse<String>> getReports() {
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "Truy cập thành công",
                "Bạn đang xem báo cáo sản phẩm"
        ));
    }


    @PostMapping
    public ResponseEntity<ApiResponse<String>> createProducts(@RequestBody String productName) {
        return ResponseEntity.status(201).body(new ApiResponse<>(
                201,
                "Tạo mới thành công!",
                "Đã thêm thành công " + productName
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> updateProduct(@PathVariable int id, @RequestBody String productName) {
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "Cập nhật thành công",
                "Đã cập nhật thành " + productName
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable int id) {
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "Xoá thành công",
                "Đã xoá sản phẩm có id= " + id
        ));
    }
}
