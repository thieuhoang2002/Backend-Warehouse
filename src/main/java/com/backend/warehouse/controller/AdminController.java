package com.backend.warehouse.controller;

import com.backend.warehouse.entity.*;
import com.backend.warehouse.payload.request.CreateShelfRequest;
import com.backend.warehouse.payload.request.CreateUserRequest;
import com.backend.warehouse.payload.request.CreateWarehouseRequest;
import com.backend.warehouse.payload.request.UpdateUserRequest;
import com.backend.warehouse.payload.response.MessageResponse;
import com.backend.warehouse.payload.response.ShelfSummaryDto;
import com.backend.warehouse.payload.response.UserDto;
import com.backend.warehouse.payload.response.WarehouseSummaryDto;
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
    // MODULE 2: Quản lý Kho & Kệ
    // =========================================================================

    /** Danh sách kho kèm thống kê số kệ, số ngăn, số sản phẩm */
    @GetMapping("/warehouses")
    public List<WarehouseSummaryDto> getAllWarehouses() {
        return warehouseRepository.findAll().stream().map(w -> {
            List<Shelf> shelves = shelfRepository.findByWarehouse_WarehouseId(w.getWarehouseId());
            int shelfCount = shelves.size();
            int compartmentCount = 0;
            int itemCount = 0;

            for (Shelf s : shelves) {
                List<Compartment> comps = compartmentRepository.findByShelf_ShelfId(s.getShelfId());
                compartmentCount += comps.size();
                for (Compartment c : comps) {
                    if (c.isHasItem() || c.getItem() != null || c.getQuantity() > 0) {
                        itemCount++;
                    }
                }
            }

            return new WarehouseSummaryDto(
                    w.getWarehouseId(),
                    w.getName(),
                    w.getLocation(),
                    shelfCount,
                    compartmentCount,
                    itemCount
            );
        }).collect(Collectors.toList());
    }

    /** Tạo kho mới */
    @PostMapping("/warehouses")
    public ResponseEntity<?> createWarehouse(@RequestBody CreateWarehouseRequest req) {
        if (req.getName() == null || req.getName().isBlank()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Tên kho không được để trống."));
        }
        Warehouse warehouse = new Warehouse(req.getName().trim(), req.getLocation() != null ? req.getLocation().trim() : "", null);
        warehouseRepository.save(warehouse);
        return ResponseEntity.ok(new MessageResponse("Tạo kho hàng thành công!"));
    }

    /** Sửa thông tin kho */
    @PutMapping("/warehouses/{id}")
    public ResponseEntity<?> updateWarehouse(@PathVariable Long id, @RequestBody CreateWarehouseRequest req) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kho hàng ID: " + id));

        if (req.getName() != null && !req.getName().isBlank()) {
            warehouse.setName(req.getName().trim());
        }
        if (req.getLocation() != null) {
            warehouse.setLocation(req.getLocation().trim());
        }
        warehouseRepository.save(warehouse);
        return ResponseEntity.ok(new MessageResponse("Cập nhật kho hàng thành công!"));
    }

    /** Xóa kho (chỉ cho phép nếu không còn hàng trong kho) */
    @DeleteMapping("/warehouses/{id}")
    public ResponseEntity<?> deleteWarehouse(@PathVariable Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kho hàng ID: " + id));

        List<Shelf> shelves = shelfRepository.findByWarehouse_WarehouseId(id);
        for (Shelf s : shelves) {
            List<Compartment> comps = compartmentRepository.findByShelf_ShelfId(s.getShelfId());
            for (Compartment c : comps) {
                if (c.isHasItem() || c.getItem() != null || c.getQuantity() > 0) {
                    return ResponseEntity.badRequest()
                            .body(new MessageResponse("Không thể xóa kho! Kệ '" + s.getNameShelf() + "' vẫn còn chứa hàng hóa."));
                }
            }
        }

        // Xóa an toàn: xóa các compartment trống -> xóa shelf -> xóa warehouse
        for (Shelf s : shelves) {
            List<Compartment> comps = compartmentRepository.findByShelf_ShelfId(s.getShelfId());
            compartmentRepository.deleteAll(comps);
            shelfRepository.delete(s);
        }
        warehouseRepository.delete(warehouse);

        return ResponseEntity.ok(new MessageResponse("Xóa kho hàng thành công!"));
    }

    /** Danh sách kệ kèm tọa độ 3D và trạng thái */
    @GetMapping("/shelves")
    public List<ShelfSummaryDto> getAllShelves() {
        return shelfRepository.findAll().stream().map(s -> {
            List<Compartment> comps = compartmentRepository.findByShelf_ShelfId(s.getShelfId());
            boolean hasItems = comps.stream().anyMatch(c -> c.isHasItem() || c.getItem() != null || c.getQuantity() > 0);
            Position pos = s.getPosition();
            float x = pos != null ? pos.getxCoord() : 0f;
            float y = pos != null ? pos.getyCoord() : 0f;
            float z = pos != null ? pos.getzCoord() : 0f;

            Warehouse w = s.getWarehouse();
            Long wId = w != null ? w.getWarehouseId() : null;
            String wName = w != null ? w.getName() : "Chưa gán kho";

            return new ShelfSummaryDto(
                    s.getShelfId(),
                    s.getNameShelf(),
                    s.getType(),
                    wId,
                    wName,
                    x, y, z,
                    comps.size(),
                    hasItems
            );
        }).collect(Collectors.toList());
    }

    /** Tạo kệ mới và tự động sinh các ngăn (Compartment) ban đầu */
    @PostMapping("/shelves")
    public ResponseEntity<?> createShelf(@RequestBody CreateShelfRequest req) {
        if (req.getNameShelf() == null || req.getNameShelf().isBlank()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Tên kệ không được để trống."));
        }
        if (req.getWarehouseId() == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Vui lòng chọn kho hàng trực thuộc."));
        }

        Warehouse warehouse = warehouseRepository.findById(req.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kho hàng ID: " + req.getWarehouseId()));

        Position position = new Position(req.getxCoord(), req.getyCoord(), req.getzCoord());

        String shelfType = req.getType() != null && !req.getType().isBlank() ? req.getType().trim() : "General";
        Shelf shelf = new Shelf(req.getNameShelf().trim(), shelfType, position, null, warehouse);
        Shelf savedShelf = shelfRepository.save(shelf);

        // Tự động khởi tạo các ngăn cho kệ mới
        int layers = Math.max(1, Math.min(req.getLayers(), 5)); // 1 đến 5 tầng
        int compsPerLayer = Math.max(1, Math.min(req.getCompartmentsPerLayer(), 4)); // 1 đến 4 ngăn

        String[] sideNames = { "Trái", "Giữa", "Phải", "Phụ" };

        for (int layer = 1; layer <= layers; layer++) {
            for (int side = 1; side <= compsPerLayer; side++) {
                String sideName = side <= sideNames.length ? sideNames[side - 1] : "N" + side;
                String compName = String.format("%s-L%d-%s", savedShelf.getNameShelf(), layer, sideName);
                Compartment comp = new Compartment(compName, null, layer, side, false, savedShelf, 0, false);
                compartmentRepository.save(comp);
            }
        }

        return ResponseEntity.ok(new MessageResponse("Tạo kệ hàng và " + (layers * compsPerLayer) + " ngăn thành công!"));
    }

    /** Xóa kệ (chỉ cho phép nếu không còn hàng) */
    @DeleteMapping("/shelves/{id}")
    public ResponseEntity<?> deleteShelf(@PathVariable Long id) {
        Shelf shelf = shelfRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kệ ID: " + id));

        List<Compartment> comps = compartmentRepository.findByShelf_ShelfId(id);
        boolean hasItems = comps.stream().anyMatch(c -> c.isHasItem() || c.getItem() != null || c.getQuantity() > 0);
        if (hasItems) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Không thể xóa kệ! Ngăn của kệ vẫn còn chứa hàng hóa."));
        }

        // Xóa các ngăn trống rồi xóa kệ
        compartmentRepository.deleteAll(comps);
        shelfRepository.delete(shelf);

        return ResponseEntity.ok(new MessageResponse("Xóa kệ hàng thành công!"));
    }

    // =========================================================================
    // MODULE 3: Thông tin Hệ Thống
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

