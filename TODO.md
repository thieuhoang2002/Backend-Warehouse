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

## ⚙️ Tính Năng Cấu Hình Hệ Thống (Admin Panel) — Đề Xuất Mới

> Mục tiêu: Admin có UI tập trung để xem & quản lý hệ thống mà không cần truy cập DB hay file config.

### 👤 Module 1: Quản Lý Tài Khoản Nhân Viên
**Route:** `GET/POST/PUT /api/admin/users`

- [ ] `GET  /api/admin/users` — Danh sách tất cả users (id, username, profileName, email, role)
- [ ] `POST /api/admin/users` — Tạo tài khoản nhân viên mới (dùng lại SignupRequest)
- [ ] `PUT  /api/admin/users/{id}/role` — Đổi role (ADMIN ↔ STAFF)
- [ ] `PUT  /api/admin/users/{id}/reset-password` — Admin reset mật khẩu cho nhân viên
- [ ] `PUT  /api/admin/users/{id}/disable` — Vô hiệu hóa tài khoản (thêm field `enabled` vào User)

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

- [ ] Trả về: tổng users, tổng kho, tổng kệ, tổng ngăn, tổng items đang lưu
- [ ] Tên môi trường (local / staging / production từ `spring.profiles.active`)
- [ ] Thời gian server khởi động

### Frontend Pages Cần Tạo
- [ ] `/admin/users` — Trang quản lý tài khoản
- [ ] `/admin/warehouses` — Trang quản lý kho & kệ
- [ ] `/admin/config` — Trang cấu hình thông báo
- [ ] `/admin/system` — Trang thông tin hệ thống
- [ ] Sidebar menu "Cấu hình" chỉ hiện với ADMIN role
