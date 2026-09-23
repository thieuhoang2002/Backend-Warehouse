# ✅ TODO — WMS Backend

## Đã Hoàn Thành

### 🔐 Auth (`/api/auth`)
- [x] `POST /api/auth/signin` — Đăng nhập, trả về JWT
- [x] `POST /api/auth/signup` — Đăng ký (role: STAFF mặc định, hoặc ADMIN)
- [x] `GET  /api/auth/countUsers` — Đếm tổng user

### 📦 Hàng Hóa (`/api/product`)
- [x] `GET  /api/product/all` — Lấy tất cả items
- [x] `GET  /api/product/get-list-products` — Items đang lưu kho
- [x] `GET  /api/product/{itemId}/compartments` — Compartments của item
- [x] `GET  /api/product/totalItemsInStock` — Tổng số lượng tồn kho
- [x] `GET  /api/product/search?data=` — Tìm kiếm theo ID hoặc tên
- [x] `GET  /api/product/monthlyItemCount` — Thống kê theo tháng
- [x] `GET  /api/product/items-not-in-compartments` — Items chưa phân kệ
- [x] `GET  /api/product/items-check-in-decrease` — Sắp xếp checkin giảm dần
- [x] `GET  /api/product/items-check-in-increase` — Sắp xếp checkin tăng dần
- [x] `GET  /api/product/items-check-out-decrease` — Sắp xếp checkout giảm dần
- [x] `GET  /api/product/items-check-out-increase` — Sắp xếp checkout tăng dần
- [x] `PUT  /api/product/update/{id}` — Cập nhật thông tin item

### 🗂️ Booking (`/api/booking`)
- [x] `POST   /api/booking/upload` — Upload CSV, tạo Booking + Items
- [x] `GET    /api/booking/all` — Lấy tất cả bookings (đang hoạt động)
- [x] `PUT    /api/booking/update/{id}` — Cập nhật thông tin booking
- [x] `DELETE /api/booking/delete/{id}` — Xóa booking và items liên quan
- [x] `GET    /api/booking/download/{fileName}` — Tải file CSV đã upload
- [x] `GET    /api/booking/totalCustomers` — Đếm tổng khách hàng

### 🏠 Compartment (`/api/compartments`)
- [x] `GET  /api/compartments` — Lấy tất cả compartments
- [x] `GET  /api/compartments/{shelfId}/{nameComp}` — Lấy 1 compartment
- [x] `POST /api/compartments/{shelfId}` — Tạo compartment mới
- [x] `POST /api/compartments/{compartmentId}/addItem` — Gán item vào ngăn
- [x] `PUT  /api/compartments/{compartmentId}/updateQuantity` — Cập nhật số lượng
- [x] `DELETE /api/compartments/{compartmentId}/removeItem/{itemId}` — Gỡ item
- [x] `POST /api/compartments/{compartmentId}/checkout/{itemId}` — Khởi tạo xuất kho
- [x] `GET  /api/compartments/checkout/pending` — Danh sách chờ xác nhận
- [x] `POST /api/compartments/checkout/confirm/{recordId}` — Xác nhận checkout
- [x] `POST /api/compartments/checkout/cancel/{recordId}` — Hủy checkout

### 🏭 Warehouse & Shelf
- [x] `GET/POST/PUT/DELETE /api/warehouse` — CRUD Warehouse
- [x] `GET /api/warehouse/{id}` — Chi tiết warehouse
- [x] `GET/POST/PUT/DELETE /api/shelf` — CRUD Shelf

### 🔔 Notification (`/api/notifications`)
- [x] `GET  /api/notifications` — Lấy tất cả thông báo
- [x] `GET  /api/notifications/{id}` — Chi tiết thông báo
- [x] `POST /api/notifications/create` — Tạo thông báo thủ công
- [x] `GET  /api/notifications/checkout-reminder` — Trigger job thủ công
- [x] `PUT  /api/notifications/{id}/mark-as-read` — Đánh dấu đã đọc
- [x] `DELETE /api/notifications/{id}` — Xóa thông báo

### 📊 Reports (`/api/jasper`)
- [x] `GET  /api/jasper/checkout-records/grouped` — Checkout records nhóm theo ngày
- [x] `GET  /api/jasper/generate-pdf-item` — PDF danh sách hàng hóa
- [x] `POST /api/jasper/generate-pdf-delivery-report` — PDF phiếu giao hàng
- [x] `POST /api/jasper/generate-pdf-booking` — PDF phiếu nhập kho
- [x] `POST /api/jasper/generate-pdf-checkout-item` — PDF phiếu xuất kho

---

## 🔧 Cần Cải Thiện

### Bảo Mật
- [ ] Enforce `@PreAuthorize("hasRole('ADMIN')")` trên các route nhạy cảm (xóa booking, quản lý warehouse/shelf)
- [ ] Thêm Rate Limiting (chống brute-force login)
- [ ] Thêm `@Valid` cho tất cả request body chưa có

### Nghiệp Vụ
- [ ] Cho phép checkout linh hoạt hơn — hiện tại chỉ được checkout đúng ngày checkout date
- [ ] Pagination cho `/api/product/all` (hiện tại trả toàn bộ)
- [ ] Sort/Filter ở Repository layer thay vì in-memory tại Controller

### Code Quality
- [ ] Viết Unit Tests (Service layer) và Integration Tests (Controller layer)
- [ ] Thêm `@Slf4j` logging thay thế `System.out.println` trong BookingServiceImpl
- [ ] Chuẩn hóa error response format (hiện tại mỗi nơi trả lỗi theo cách khác nhau)
- [ ] Thêm Swagger/OpenAPI documentation (`springdoc-openapi`)

### Performance
- [ ] Thêm Redis Cache cho các query thường xuyên (getAllProducts, getAllCompartments)
- [ ] Lazy loading review — tránh N+1 query problem

---

## 💡 Tính Năng Tương Lai (Nice to Have)

- [ ] Dashboard Admin: quản lý Users, phân công nhân viên theo kho
- [ ] Tích hợp email notification (khi booking mới, sắp đến hạn)
- [ ] Import/Export Excel (ngoài CSV)
- [ ] QR code cho từng ngăn kệ — quét để xem thông tin
- [ ] Audit Log — lưu lịch sử thao tác của từng user
- [ ] Multi-warehouse support với phân quyền theo kho

---

### ⚙️ Quản Trị Hệ Thống (`/api/admin`) — Đã Hoàn Thành
- [x] `GET    /api/admin/users` — Lấy danh sách tất cả tài khoản
- [x] `POST   /api/admin/users` — Tạo tài khoản mới (Admin / Nhân viên)
- [x] `PUT    /api/admin/users/{id}` — Cập nhật họ tên, email, vai trò
- [x] `PUT    /api/admin/users/{id}/reset-password` — Đặt lại mật khẩu
- [x] `DELETE /api/admin/users/{id}` — Xóa tài khoản (bảo vệ chống tự xóa chính mình)
- [x] `GET    /api/admin/system-info` — Thống kê tổng quan hệ thống (tổng user, booking, item, kho, kệ, ngăn, checkout)
- [x] Tích hợp Cloudflare R2 Storage (AWS SDK v2 S3-compatible) cho lưu trữ file CSV lâu dài
- [x] Tự động xóa file trên R2 khi hủy booking

---

## 🔧 Cần Cải Thiện

### Bảo Mật
- [x] Enforce `@PreAuthorize("hasRole('ROLE_ADMIN')")` trên các route admin
- [ ] Thêm Rate Limiting (chống brute-force login)
- [ ] Thêm `@Valid` cho tất cả request body chưa có

### Nghiệp Vụ
- [ ] Cho phép checkout linh hoạt hơn — hiện tại chỉ được checkout đúng ngày checkout date
- [ ] Pagination cho `/api/product/all` (hiện tại trả toàn bộ)
- [ ] Sort/Filter ở Repository layer thay vì in-memory tại Controller

### Code Quality
- [ ] Viết Unit Tests (Service layer) và Integration Tests (Controller layer)
- [ ] Thêm `@Slf4j` logging thay thế `System.out.println` trong BookingServiceImpl
- [ ] Chuẩn hóa error response format (hiện tại mỗi nơi trả lỗi theo cách khác nhau)
- [ ] Thêm Swagger/OpenAPI documentation (`springdoc-openapi`)

### Performance
- [ ] Thêm Redis Cache cho các query thường xuyên (getAllProducts, getAllCompartments)
- [ ] Lazy loading review — tránh N+1 query problem

---

## 💡 Tính Năng Tương Lai (Nice to Have)

- [ ] Import/Export Excel (ngoài CSV)
- [ ] QR code cho từng ngăn kệ — quét để xem thông tin
- [ ] Audit Log — lưu lịch sử thao tác của từng user
- [ ] Multi-warehouse support với phân quyền theo kho

---

## ⚙️ Tính Năng Cấu Hình Hệ Thống (Admin Panel) — Tiến Độ

> Mục tiêu: Admin có UI tập trung để xem & quản lý hệ thống mà không cần truy cập DB hay file config.

### 👤 Module 1: Quản Lý Tài Khoản Nhân Viên
**Route:** `GET/POST/PUT/DELETE /api/admin/users`
- [x] `GET  /api/admin/users` — Danh sách tất cả users (id, username, profileName, email, role)
- [x] `POST /api/admin/users` — Tạo tài khoản nhân viên mới (hỗ trợ ROLE_ADMIN, ROLE_STAFF)
- [x] `PUT  /api/admin/users/{id}` — Cập nhật họ tên, email, vai trò (ADMIN ↔ STAFF)
- [x] `PUT  /api/admin/users/{id}/reset-password` — Admin reset mật khẩu cho nhân viên
- [x] `DELETE /api/admin/users/{id}` — Xóa người dùng (chặn tự xóa bản thân)

### 🏭 Module 2: Quản Lý Kho & Kệ
**Route:** đã có `/api/warehouse`, `/api/shelf` — cần thêm UI frontend
- [ ] Frontend: form tạo warehouse mới (tên, địa chỉ)
- [ ] Frontend: form tạo shelf mới (tên, loại, tọa độ X/Y/Z, chọn warehouse)
- [ ] Frontend: danh sách warehouse + số kệ, số ngăn trong mỗi kho
- [ ] `DELETE /api/warehouse/{id}` — Xóa kho (chỉ khi không còn hàng)
- [ ] `DELETE /api/shelf/{id}` — Xóa kệ (chỉ khi không còn hàng)

### 🔔 Module 3: Cấu Hình Thông Báo
**Route:** `GET/PUT /api/admin/config`
- [ ] `GET /api/admin/config` — Lấy cấu hình hiện tại
- [ ] `PUT /api/admin/config` — Cập nhật cấu hình:
  - `checkoutReminderDays` — cảnh báo trước X ngày khi sắp đến hạn xuất (mặc định 3)
  - `maxStorageDays` — số ngày tối đa lưu kho trước khi forced checkout (mặc định 30)

### 📊 Module 4: Thông Tin Hệ Thống (Read-only)
**Route:** `GET /api/admin/system-info`
- [x] Trả về: tổng users, tổng kho, tổng kệ, tổng ngăn, tổng items đang lưu, tổng booking, tổng checkout
- [ ] Tên môi trường (local / staging / production từ `spring.profiles.active`)
- [ ] Thời gian server khởi động

### Frontend Pages
- [x] `/admin` (Tab: Quản lý nhân viên) — Bảng danh sách, tạo mới, sửa, đổi pass, xóa
- [x] `/admin` (Tab: Thông tin hệ thống) — Thống kê các chỉ số bằng thẻ số liệu trực quan
- [x] Sidebar navigation & menu "Quản Trị Viên" trên Navbar (chỉ hiển thị với ROLE_ADMIN)
- [ ] `/admin` (Tab: Quản lý kho & kệ)
- [ ] `/admin` (Tab: Cấu hình hệ thống)

