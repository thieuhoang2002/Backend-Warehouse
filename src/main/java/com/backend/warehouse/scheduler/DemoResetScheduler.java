package com.backend.warehouse.scheduler;

import com.backend.warehouse.service.DemoResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Tự động chạy dọn rác và khôi phục dữ liệu demo ban đầu định kỳ sau 24h.
 * Mặc định kích hoạt vào lúc 2:00 sáng mỗi ngày (giờ Việt Nam).
 */
@Component
public class DemoResetScheduler {

    @Autowired
    private DemoResetService demoResetService;

    // Chạy lúc 02:00 AM hàng ngày theo múi giờ Việt Nam (Asia/Ho_Chi_Minh)
    @Scheduled(cron = "0 0 2 * * ?", zone = "Asia/Ho_Chi_Minh")
    public void scheduledDemoReset() {
        System.out.println("=== [DemoResetScheduler] Bắt đầu tác vụ dọn rác và reset demo tự động lúc 02:00 AM ===");
        try {
            demoResetService.resetAll();
            System.out.println("=== [DemoResetScheduler] Hoàn tất tác vụ reset demo tự động ===");
        } catch (Exception e) {
            System.err.println("=== [DemoResetScheduler] Lỗi khi thực hiện reset demo: " + e.getMessage());
        }
    }
}
