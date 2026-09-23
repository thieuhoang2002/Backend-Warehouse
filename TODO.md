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
