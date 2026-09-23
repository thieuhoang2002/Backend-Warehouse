package com.backend.warehouse.controller;

import com.backend.warehouse.entity.User;
import com.backend.warehouse.entity.UserRole;
import com.backend.warehouse.payload.request.CreateUserRequest;
import com.backend.warehouse.payload.request.UpdateUserRequest;
import com.backend.warehouse.payload.response.MessageResponse;
import com.backend.warehouse.payload.response.UserDto;
import com.backend.warehouse.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AdminController — quản lý hệ thống dành riêng cho ROLE_ADMIN.
 * Tất cả endpoint yêu cầu JWT hợp lệ + vai trò ADMIN.
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    @Autowired private UserRepository           userRepository;
    @Autowired private BookingRepository        bookingRepository;
    @Autowired private ItemRepository           itemRepository;
    @Autowired private WarehouseRepository      warehouseRepository;
    @Autowired private ShelfRepository          shelfRepository;
    @Autowired private CompartmentRepository    compartmentRepository;
    @Autowired private CheckoutRecordRepository checkoutRecordRepository;
    @Autowired private PasswordEncoder          passwordEncoder;

    // =========================================================================
    // MODULE 1: Quản lý Nhân Viên
    // =========================================================================

    /** Danh sách tất cả người dùng */
    @GetMapping("/users")
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(u -> new UserDto(u.getUserId(), u.getUsername(),
                        u.getProfileName(), u.getEmail(), u.getRole().name()))
                .collect(Collectors.toList());
    }

    /** Tạo tài khoản mới */
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Tên đăng nhập đã tồn tại: " + req.getUsername()));
        }

        UserRole role;
        try {
            role = UserRole.valueOf(req.getRole() != null ? req.getRole() : "ROLE_STAFF");
        } catch (IllegalArgumentException e) {
            role = UserRole.ROLE_STAFF;
        }

        User user = new User(
                req.getUsername(),
                passwordEncoder.encode(req.getPassword()),
                req.getProfileName(),
                req.getEmail(),
                role
        );
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("Tạo tài khoản thành công!"));
    }

    /** Cập nhật thông tin người dùng (tên, email, vai trò) */
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest req) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng ID: " + id));

        if (req.getProfileName() != null) user.setProfileName(req.getProfileName());
        if (req.getEmail()       != null) user.setEmail(req.getEmail());
        if (req.getRole()        != null) {
            try { user.setRole(UserRole.valueOf(req.getRole())); }
            catch (IllegalArgumentException ignored) {}
        }
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("Cập nhật thành công!"));
    }

    /** Đặt lại mật khẩu */
    @PutMapping("/users/{id}/reset-password")
    public ResponseEntity<?> resetPassword(@PathVariable Long id,
                                           @RequestBody Map<String, String> body) {
        String newPassword = body.get("password");
        if (newPassword == null || newPassword.length() < 6) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Mật khẩu phải có ít nhất 6 ký tự."));
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng ID: " + id));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("Đặt lại mật khẩu thành công!"));
    }

    /** Xóa tài khoản (không thể tự xóa mình) */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        User target = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng ID: " + id));

        if (target.getUsername().equals(currentUsername)) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Không thể xóa tài khoản đang đăng nhập!"));
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok(new MessageResponse("Xóa tài khoản thành công!"));
    }

    // =========================================================================
    // MODULE 2: Thông tin Hệ Thống
    // =========================================================================

    /** Thống kê toàn hệ thống */
    @GetMapping("/system-info")
    public ResponseEntity<Map<String, Long>> getSystemInfo() {
        Map<String, Long> info = new LinkedHashMap<>();
        info.put("totalUsers",       userRepository.count());
        info.put("totalBookings",    bookingRepository.count());
        info.put("totalItems",       itemRepository.count());
        info.put("totalWarehouses",  warehouseRepository.count());
        info.put("totalShelves",     shelfRepository.count());
        info.put("totalCompartments",compartmentRepository.count());
        info.put("totalCheckouts",   checkoutRecordRepository.count());
        return ResponseEntity.ok(info);
    }
}
