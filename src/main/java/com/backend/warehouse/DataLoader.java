package com.backend.warehouse;

import com.backend.warehouse.entity.*;
import com.backend.warehouse.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DataLoader — tự động seed dữ liệu mẫu khi khởi động lần đầu.
 * Chỉ chạy nếu bảng users còn trống. Xóa file này hoặc comment @Component
 * khi deploy production để tránh seed lại mỗi lần restart.
 */
@Component
public class DataLoader implements ApplicationRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private WarehouseRepository warehouseRepository;
    @Autowired private ShelfRepository shelfRepository;
    @Autowired private CompartmentRepository compartmentRepository;
    @Autowired private BookingRepository bookingRepository;
    @Autowired private ItemRepository itemRepository;
    @Autowired private CheckoutRecordRepository checkoutRecordRepository;
    @Autowired private NotificationRepository notificationRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (userRepository.count() > 0) {
            System.out.println("=== [DataLoader] DB đã có dữ liệu, bỏ qua seed. ===");
            return;
        }

        System.out.println("=== [DataLoader] Đang tạo dữ liệu mẫu... ===");

        // =====================================================================
        // USERS: 1 admin + 2 nhân viên
        // =====================================================================
        User admin  = new User("admin",         passwordEncoder.encode("warehouse2024"), "Quản Trị Viên",  "admin@wms.com",   UserRole.ROLE_ADMIN);
        User staff1 = new User("nguyen.van.a",  passwordEncoder.encode("staff2024"),     "Nguyễn Văn A",   "nva@wms.com",     UserRole.ROLE_STAFF);
        User staff2 = new User("tran.thi.b",    passwordEncoder.encode("staff2024"),     "Trần Thị B",     "ttb@wms.com",     UserRole.ROLE_STAFF);
        userRepository.saveAll(List.of(admin, staff1, staff2));

        // =====================================================================
        // WAREHOUSES
        // =====================================================================
        Warehouse warehouseA = new Warehouse("Kho A - Điện tử", "Tầng 1, Tòa nhà B, KCN Tân Bình, TP.HCM", null);
        Warehouse warehouseB = new Warehouse("Kho B - Thời trang", "Tầng 2, Tòa nhà B, KCN Tân Bình, TP.HCM", null);
        warehouseRepository.saveAll(List.of(warehouseA, warehouseB));

        // =====================================================================
        // SHELVES (Position được cascade save qua Shelf)
        // Tọa độ (x, y, z) cho mô hình 3D: x = hàng, z = cột, y = tầng
        // =====================================================================
        Shelf shelfA1 = new Shelf("Kệ A1", "Electronics", new Position(1f, 0f, 1f), null, warehouseA);
        Shelf shelfA2 = new Shelf("Kệ A2", "Electronics", new Position(3f, 0f, 1f), null, warehouseA);
        Shelf shelfA3 = new Shelf("Kệ A3", "Electronics", new Position(5f, 0f, 1f), null, warehouseA);
        Shelf shelfA4 = new Shelf("Kệ A4", "Food",        new Position(7f, 0f, 1f), null, warehouseA);
        Shelf shelfB1 = new Shelf("Kệ B1", "Clothing",    new Position(1f, 0f, 3f), null, warehouseB);
        Shelf shelfB2 = new Shelf("Kệ B2", "Clothing",    new Position(3f, 0f, 3f), null, warehouseB);
        shelfRepository.saveAll(List.of(shelfA1, shelfA2, shelfA3, shelfA4, shelfB1, shelfB2));

        // =====================================================================
        // COMPARTMENTS — mỗi kệ có 3-4 ngăn (layerIndex: tầng, side: 1=Trái/2=Giữa/3=Phải)
        // format tên: {kệ}-L{layer}-{side}
        // =====================================================================

        // Kệ A1 — 4 ngăn
        Compartment cA1L1T = comp("A1-L1-Trái",  1, 1, shelfA1);
        Compartment cA1L1G = comp("A1-L1-Giữa",  1, 2, shelfA1);
        Compartment cA1L1P = comp("A1-L1-Phải",  1, 3, shelfA1);
        Compartment cA1L2T = comp("A1-L2-Trái",  2, 1, shelfA1);

        // Kệ A2 — 3 ngăn
        Compartment cA2L1T = comp("A2-L1-Trái",  1, 1, shelfA2);
        Compartment cA2L1G = comp("A2-L1-Giữa",  1, 2, shelfA2);
        Compartment cA2L1P = comp("A2-L1-Phải",  1, 3, shelfA2);

        // Kệ A3 — 3 ngăn
        Compartment cA3L1T = comp("A3-L1-Trái",  1, 1, shelfA3);
        Compartment cA3L1G = comp("A3-L1-Giữa",  1, 2, shelfA3);
        Compartment cA3L2T = comp("A3-L2-Trái",  2, 1, shelfA3);

        // Kệ A4 (Food) — 2 ngăn
        Compartment cA4L1T = comp("A4-L1-Trái",  1, 1, shelfA4);
        Compartment cA4L1G = comp("A4-L1-Giữa",  1, 2, shelfA4);

        // Kệ B1 (Clothing) — 3 ngăn
        Compartment cB1L1T = comp("B1-L1-Trái",  1, 1, shelfB1);
        Compartment cB1L1G = comp("B1-L1-Giữa",  1, 2, shelfB1);
        Compartment cB1L1P = comp("B1-L1-Phải",  1, 3, shelfB1);

        // Kệ B2 (Clothing) — 3 ngăn
        Compartment cB2L1T = comp("B2-L1-Trái",  1, 1, shelfB2);
        Compartment cB2L1G = comp("B2-L1-Giữa",  1, 2, shelfB2);
        Compartment cB2L2T = comp("B2-L2-Trái",  2, 1, shelfB2);

        compartmentRepository.saveAll(List.of(
            cA1L1T, cA1L1G, cA1L1P, cA1L2T,
            cA2L1T, cA2L1G, cA2L1P,
            cA3L1T, cA3L1G, cA3L2T,
            cA4L1T, cA4L1G,
            cB1L1T, cB1L1G, cB1L1P,
            cB2L1T, cB2L1G, cB2L2T
        ));

        // =====================================================================
        // BOOKINGS
        // =====================================================================
        Booking booking1 = new Booking();
        booking1.setCustomerEmail("cty.abc@gmail.com");
        booking1.setCustomerName("Công ty TNHH ABC Technology");
        booking1.setNumberphone("0901234567");
        booking1.setExcelFile("uploads/booking_cty_abc.csv");
        booking1.setReferenceNo(54321L);

        Booking booking2 = new Booking();
        booking2.setCustomerEmail("shop.xyz@gmail.com");
        booking2.setCustomerName("Cửa hàng thời trang XYZ");
        booking2.setNumberphone("0912345678");
        booking2.setExcelFile("uploads/booking_shop_xyz.csv");
        booking2.setReferenceNo(67890L);

        Booking booking3 = new Booking();
        booking3.setCustomerEmail("tdd.food@gmail.com");
        booking3.setCustomerName("Tập đoàn thực phẩm DEF");
        booking3.setNumberphone("0923456789");
        booking3.setExcelFile("uploads/booking_tdd_food.csv");
        booking3.setReferenceNo(11223L);

        bookingRepository.saveAll(List.of(booking1, booking2, booking3));

        // =====================================================================
        // ITEMS
        // =====================================================================
        LocalDate today = LocalDate.now();

        // --- Booking 1: Hàng điện tử ---
        Item laptopDell     = item("Laptop Dell Inspiron 15 3000", 10, 2500f, "Electronics",
                today.minusDays(30), today.plusDays(60),  "Đang lưu kho", "51A-123.45", booking1);
        Item monitorSamsung = item("Màn hình Samsung 27\" 4K",      5, 4200f, "Electronics",
                today.minusDays(30), today.plusDays(60),  "Đang lưu kho", "51A-123.45", booking1);
        Item mouseLogitech  = item("Chuột Logitech MX Master 3",   20,  145f, "Electronics",
                today.minusDays(30), today.plusDays(30),  "Đang lưu kho", "51A-123.45", booking1);
        Item kbCorsair      = item("Bàn phím Corsair K95 RGB",      8, 1200f, "Electronics",
                today.minusDays(7),  today.plusDays(45),  "Đang lưu kho", "51A-789.01", booking1);
        Item headsetSony    = item("Tai nghe Sony WH-1000XM5",     12,  250f, "Electronics",
                today.minusDays(7),  today.plusDays(90),  "Đang lưu kho", "51A-789.01", booking1);

        // --- Booking 2: Thời trang ---
        Item shirtNike      = item("Áo thun Nike Dri-FIT Size L",  50,  200f, "Clothing",
                today.minusDays(15), today.plusDays(90),  "Đang lưu kho", "51B-456.78", booking2);
        Item jeansLevis     = item("Quần Jeans Levi's 511 Slim",   30,  450f, "Clothing",
                today.minusDays(15), today.plusDays(90),  "Đang lưu kho", "51B-456.78", booking2);
        Item jacketAdidas   = item("Áo khoác Adidas Slim Fit",     15,  380f, "Clothing",
                today.minusDays(15), today.plusDays(45),  "Đang lưu kho", "51B-456.78", booking2);

        // --- Booking 3: Thực phẩm ---
        Item coffeeDalat    = item("Cà phê nguyên chất Đà Lạt",  100,  500f, "Food",
                today.minusDays(7),  today.plusDays(180), "Đang lưu kho", "51C-001.00", booking3);

        // --- Item đã xuất kho (lịch sử) ---
        Item oldLaptopHP    = item("Laptop HP EliteBook 840 G9",    5, 2000f, "Electronics",
                today.minusDays(90), today.minusDays(30), "Đã xuất kho",  "51A-000.11", booking1);

        itemRepository.saveAll(List.of(
            laptopDell, monitorSamsung, mouseLogitech, kbCorsair, headsetSony,
            shirtNike, jeansLevis, jacketAdidas,
            coffeeDalat, oldLaptopHP
        ));

        // =====================================================================
        // GÁN ITEMS VÀO COMPARTMENTS (hàng đang trong kho)
        // =====================================================================
        assignItem(cA1L1T, laptopDell,     5);  // 5/10 laptop trong ngăn A1-Trái
        assignItem(cA1L1G, monitorSamsung, 5);  // 5/5 màn hình trong ngăn A1-Giữa
        assignItem(cA1L1P, mouseLogitech, 10);  // 10/20 chuột trong ngăn A1-Phải
        assignItem(cA1L2T, mouseLogitech, 10);  // 10/20 chuột trong ngăn A1-L2-Trái
        assignItem(cA2L1T, kbCorsair,      8);  // 8/8 bàn phím trong ngăn A2-Trái
        assignItem(cA2L1G, headsetSony,   12);  // 12/12 tai nghe trong ngăn A2-Giữa
        assignItem(cA4L1T, coffeeDalat,   60);  // 60/100 cà phê trong ngăn A4-Trái
        assignItem(cA4L1G, coffeeDalat,   40);  // 40/100 cà phê trong ngăn A4-Giữa
        assignItem(cB1L1T, shirtNike,     25);  // 25/50 áo Nike trong ngăn B1-Trái
        assignItem(cB1L1G, shirtNike,     25);  // 25/50 áo Nike trong ngăn B1-Giữa
        assignItem(cB1L1P, jeansLevis,    30);  // 30/30 jeans trong ngăn B1-Phải
        assignItem(cB2L1T, jacketAdidas,  15);  // 15/15 áo khoác trong ngăn B2-Trái

        compartmentRepository.saveAll(List.of(
            cA1L1T, cA1L1G, cA1L1P, cA1L2T,
            cA2L1T, cA2L1G,
            cA4L1T, cA4L1G,
            cB1L1T, cB1L1G, cB1L1P,
            cB2L1T
        ));

        // =====================================================================
        // CHECKOUT RECORDS — lịch sử xuất kho
        // =====================================================================
        // Record 1: Laptop HP đã xuất 30 ngày trước (confirmed = true)
        CheckoutRecord record1 = new CheckoutRecord(
            oldLaptopHP, cA3L1T, staff1,
            "54321", "51A-000.11",
            today.minusDays(30), true, 5, 60L
        );

        // Record 2: 5 laptop Dell đang chờ xác nhận (confirmed = false, ngăn bị reserved)
        cA3L1G.setItem(laptopDell);
        cA3L1G.setHasItem(true);
        cA3L1G.setQuantity(5);
        cA3L1G.setReserved(true); // locked vì đang trong quá trình checkout
        compartmentRepository.save(cA3L1G);

        CheckoutRecord record2 = new CheckoutRecord(
            laptopDell, cA3L1G, staff2,
            "54321", "51A-123.45",
            today, false, 5, 30L
        );

        checkoutRecordRepository.saveAll(List.of(record1, record2));

        // =====================================================================
        // NOTIFICATIONS
        // =====================================================================
        Notification notif1 = new Notification(
            "⚠️ Sản phẩm 'Chuột Logitech MX Master 3' sắp đến ngày xuất kho (" + today.plusDays(30) + "). Vui lòng chuẩn bị xuất hàng.",
            LocalDateTime.now().minusHours(1),
            NotificationType.CHECKOUT_REMINDER, NotificationStatus.UNREAD,
            mouseLogitech, shelfA1, cA1L1P
        );

        Notification notif2 = new Notification(
            "✅ Xuất kho thành công: 'Laptop HP EliteBook 840 G9' (5 chiếc) đã được xác nhận bởi Nguyễn Văn A.",
            LocalDateTime.now().minusDays(30),
            NotificationType.ITEM_CHECKOUT, NotificationStatus.READ,
            oldLaptopHP, shelfA3, cA3L1T
        );

        Notification notif3 = new Notification(
            "⚠️ Áo khoác Adidas Slim Fit sắp đến ngày xuất kho (" + today.plusDays(45) + "). Còn 45 ngày.",
            LocalDateTime.now().minusMinutes(30),
            NotificationType.CHECKOUT_REMINDER, NotificationStatus.UNREAD,
            jacketAdidas, shelfB2, cB2L1T
        );

        notificationRepository.saveAll(List.of(notif1, notif2, notif3));

        System.out.println("=== [DataLoader] ✅ Seed hoàn tất! ===");
        System.out.println("=== Tài khoản mẫu: ===");
        System.out.println("  👑 admin        / warehouse2024 (ADMIN)");
        System.out.println("  👤 nguyen.van.a / staff2024     (STAFF)");
        System.out.println("  👤 tran.thi.b   / staff2024     (STAFF)");
    }

    // -------------------------------------------------------------------------
    // Helper methods
    // -------------------------------------------------------------------------

    private Compartment comp(String name, int layer, int side, Shelf shelf) {
        return new Compartment(name, null, layer, side, false, shelf, 0, false);
    }

    private Item item(String name, int quantity, float weight, String type,
                      LocalDate checkin, LocalDate checkout, String status,
                      String delivery, Booking booking) {
        Item i = new Item();
        i.setName(name);
        i.setQuantity(quantity);
        i.setWeight(weight);
        i.setType(type);
        i.setCheckin(checkin);
        i.setCheckout(checkout);
        i.setStatus(status);
        i.setDelivery(delivery);
        i.setBooking(booking);
        i.setImage(null);
        return i;
    }

    private void assignItem(Compartment c, Item item, int qty) {
        c.setItem(item);
        c.setHasItem(true);
        c.setQuantity(qty);
        c.setReserved(false);
    }
}
