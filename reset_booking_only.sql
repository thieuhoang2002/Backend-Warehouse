-- ============================================================
-- Xóa dữ liệu booking/items cũ (seed từ DataLoader)
-- GIỮ NGUYÊN: users, warehouseinfo, shelf, position, compartment
-- Sau khi chạy → upload 3 file CSV mẫu qua giao diện web
-- ============================================================

USE warehouse;

-- Bước 1: Xóa ngăn gán hàng (unassign items khỏi compartment)
UPDATE compartment
SET item_id   = NULL,
    has_item  = 0,
    quantity  = 0,
    is_reserved = 0;

-- Bước 2: Xóa notification liên quan đến items
DELETE FROM notification
WHERE item_id IS NOT NULL
   OR compartment_id IS NOT NULL;

-- Bước 3: Xóa checkout records
DELETE FROM checkoutrecord;

-- Bước 4: Xóa items
DELETE FROM items;

-- Bước 5: Xóa bookings
DELETE FROM booking;

-- ============================================================
-- Sau khi chạy xong:
-- 1. Mở http://localhost:3000 → đăng nhập admin / warehouse2024
-- 2. Vào Booking → Nhập Booking Mới
-- 3. Upload từng file trong thư mục csv_samples/:
--      csv_samples/booking_cty_abc.csv
--      csv_samples/booking_shop_xyz.csv
--      csv_samples/booking_tdd_food.csv
-- 4. Test Tải xuống / Xuất PDF trên các booking vừa upload
-- ============================================================
