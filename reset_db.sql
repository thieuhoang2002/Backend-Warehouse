-- ============================================================
-- RESET DATABASE — Xóa toàn bộ dữ liệu để seed lại từ DataLoader
-- Chạy trong MySQL Workbench hoặc phpMyAdmin (XAMPP)
-- SAU KHI chạy SQL này → restart backend → DataLoader tự seed lại
-- ============================================================

USE warehouse;

-- Tắt FK check tạm thời để xóa không bị lỗi constraint
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE notification;
TRUNCATE TABLE checkoutrecord;
TRUNCATE TABLE compartment;
TRUNCATE TABLE items;
TRUNCATE TABLE booking;
TRUNCATE TABLE position;
TRUNCATE TABLE shelf;
TRUNCATE TABLE warehouseinfo;
TRUNCATE TABLE users;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- Sau khi chạy xong → vào Eclipse → Restart backend
-- DataLoader sẽ tự động seed lại toàn bộ dữ liệu mẫu
-- Tài khoản mẫu sau khi seed:
--   admin        / warehouse2024  (ADMIN)
--   nguyen.van.a / staff2024      (STAFF)
--   tran.thi.b   / staff2024      (STAFF)
-- ============================================================
