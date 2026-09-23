package com.backend.warehouse.service;

import com.backend.warehouse.DataLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service thực hiện dọn dẹp file rác trên R2 và đưa database về trạng thái demo ban đầu.
 */
@Service
public class DemoResetService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataLoader dataLoader;

    @Autowired
    private R2StorageService r2StorageService;

    /**
     * Dọn sạch file trên R2, xóa toàn bộ dữ liệu DB hiện tại và nạp lại dữ liệu demo chuẩn.
     */
    @Transactional
    public void resetAll() {
        System.out.println("=== [DemoResetService] Bắt đầu quá trình Reset Demo & Dọn dẹp R2 ===");

        // 1. Dọn dẹp toàn bộ file CSV upload rác trên Cloudflare R2
        try {
            r2StorageService.deleteAllObjects();
        } catch (Exception e) {
            System.err.println("=== [DemoResetService] Cảnh báo lỗi khi xóa file R2: " + e.getMessage());
        }

        // 2. Xóa toàn bộ dữ liệu trong các bảng DB (tắt tạm thời kiểm tra khóa ngoại)
        try {
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
            jdbcTemplate.execute("DELETE FROM checkoutrecord");
            jdbcTemplate.execute("DELETE FROM notification");
            jdbcTemplate.execute("DELETE FROM compartment");
            jdbcTemplate.execute("DELETE FROM item");
            jdbcTemplate.execute("DELETE FROM booking");
            jdbcTemplate.execute("DELETE FROM shelf");
            jdbcTemplate.execute("DELETE FROM position");
            jdbcTemplate.execute("DELETE FROM warehouseinfo");
            jdbcTemplate.execute("DELETE FROM users");
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
            System.out.println("=== [DemoResetService] Đã xóa toàn bộ dữ liệu cũ trong DB ===");
        } catch (Exception e) {
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
            System.err.println("=== [DemoResetService] Lỗi khi xóa dữ liệu DB: " + e.getMessage());
            throw new RuntimeException("Không thể reset database: " + e.getMessage(), e);
        }

        // 3. Nạp lại bộ dữ liệu chuẩn demo ban đầu
        try {
            dataLoader.seedData();
            System.out.println("=== [DemoResetService] ✅ Hoàn tất nạp lại dữ liệu demo chuẩn ===");
        } catch (Exception e) {
            System.err.println("=== [DemoResetService] Lỗi khi seed dữ liệu demo: " + e.getMessage());
            throw new RuntimeException("Không thể nạp dữ liệu demo: " + e.getMessage(), e);
        }
    }
}
