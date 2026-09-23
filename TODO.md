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

### 📦 Hàng Hóa (`/api/product`) — Đã Cải Thiện
- [x] Sửa lỗi 401 khi xem ngăn của sản phẩm (`/api/product/{itemId}/compartments`)
- [x] Truy vấn trực tiếp từ `CompartmentRepository` thay vì Hibernate Lazy Cache
- [x] Hỗ trợ nhận diện cả mã ID số (`1`) và mã định dạng (`SP0001`)
- [x] Fix số thứ tự (STT) trang danh sách sản phẩm khi chuyển phân trang

---

## 🔧 Cần Cải Thiện

### Bảo Mật
- [x] Enforce `@PreAuthorize("hasRole('ROLE_ADMIN')")` trên các route admin
- [ ] Thêm Rate Limiting (chống brute-force login)
- [ ] Thêm `@Valid` cho tất cả request body chưa có

### Nghiệp Vụ
- [ ] Cho phép checkout linh hoạt hơn — hiện tại chỉ được checkout đúng ngày checkout date
- [ ] Phân trang (Pagination) ở tầng Database cho `/api/product/all`
- [ ] Sort/Filter ở Repository layer thay vì in-memory tại Controller

### Code Quality
- [ ] Viết Unit Tests (Service layer) và Integration Tests (Controller layer)
- [ ] Thêm `@Slf4j` logging thay thế `System.out.println` trong BookingServiceImpl
- [ ] Chuẩn hóa error response format (dùng `@RestControllerAdvice`)
- [ ] Thêm Swagger/OpenAPI documentation (`springdoc-openapi`)

### Performance
- [ ] Thêm Redis Cache cho các query thường xuyên (getAllProducts, getAllCompartments)
- [ ] Lazy loading review — tránh N+1 query problem

---

## ⚙️ Tính Năng Cấu Hình Hệ Thống (Admin Panel) — Tiến Độ

> Mục tiêu: Admin có UI tập trung để xem & quản lý hệ thống mà không cần truy cập DB hay file config.

### 👤 Module 1: Quản Lý Tài Khoản Nhân Viên — [HOÀN THÀNH]
**Route:** `GET/POST/PUT/DELETE /api/admin/users`
- [x] `GET  /api/admin/users` — Danh sách tất cả users (id, username, profileName, email, role)
- [x] `POST /api/admin/users` — Tạo tài khoản nhân viên mới (hỗ trợ ROLE_ADMIN, ROLE_STAFF)
- [x] `PUT  /api/admin/users/{id}` — Cập nhật họ tên, email, vai trò (ADMIN ↔ STAFF)
- [x] `PUT  /api/admin/users/{id}/reset-password` — Admin reset mật khẩu cho nhân viên
- [x] `DELETE /api/admin/users/{id}` — Xóa người dùng (chặn tự xóa bản thân)

### 🏭 Module 2: Quản Lý Kho & Kệ — [HOÀN THÀNH]
**Route:** `GET/POST/PUT/DELETE /api/admin/warehouses`, `GET/POST/DELETE /api/admin/shelves`
- [x] Backend: Thống kê số lượng kệ, ngăn, hàng hóa cho từng kho (`GET /api/admin/warehouses`)
- [x] Backend: Tạo kho mới, cập nhật tên/địa chỉ kho (`POST/PUT /api/admin/warehouses`)
- [x] Backend: Tạo kệ mới kèm tọa độ 3D (X, Y, Z) và tự động sinh các tầng/ngăn ban đầu (`POST /api/admin/shelves`)
- [x] Backend: Ràng buộc an toàn — Chặn xóa Kho/Kệ nếu còn ngăn chứa hàng hóa
- [x] Frontend: Tab Quản lý Kho — Xem danh sách thẻ kho, thêm kho, sửa kho, xóa an toàn
- [x] Frontend: Tab Quản lý Kệ — Bảng danh sách kệ, tọa độ 3D, số ngăn, trạng thái hàng, thêm kệ mới, xóa kệ

### 🔔 Module 3: Cấu Hình Thông Báo — [CHƯA LÀM]
**Route:** `GET/PUT /api/admin/config`
- [ ] `GET /api/admin/config` — Lấy cấu hình hiện tại
- [ ] `PUT /api/admin/config` — Cập nhật cấu hình:
  - `checkoutReminderDays` — cảnh báo trước X ngày khi sắp đến hạn xuất (mặc định 3)
  - `maxStorageDays` — số ngày tối đa lưu kho trước khi forced checkout (mặc định 30)

### 📊 Module 4: Thông Tin Hệ Thống — [ĐANG HOÀN THIỆN]
**Route:** `GET /api/admin/system-info`
- [x] Trả về: tổng users, tổng kho, tổng kệ, tổng ngăn, tổng items đang lưu, tổng booking, tổng checkout
- [ ] Tên môi trường (local / staging / production từ `spring.profiles.active`)
- [ ] Thời gian server khởi động (Server Uptime)

---

## 💡 Tính Năng Tương Lai (Nice to Have)

- [ ] Tích hợp email notification (khi booking mới, sắp đến hạn)
- [ ] Import/Export Excel (ngoài CSV)
- [ ] QR code cho từng ngăn kệ — quét để xem thông tin
- [ ] Audit Log — lưu lịch sử thao tác của từng user
- [ ] Multi-warehouse support với phân quyền theo kho

---

### Frontend Pages
- [x] `/admin` (Tab: Quản lý nhân viên) — Bảng danh sách, tạo mới, sửa, đổi pass, xóa
- [x] `/admin` (Tab: Quản lý kho & kệ) — Quản lý kho hàng & kệ hàng, tọa độ 3D, sinh ngăn tự động
- [x] `/admin` (Tab: Thông tin hệ thống) — Thống kê các chỉ số bằng thẻ số liệu trực quan
- [x] Sidebar navigation & menu "Quản Trị Viên" trên Navbar (chỉ hiển thị với ROLE_ADMIN)
- [ ] `/admin` (Tab: Cấu hình quy tắc vận hành)
## 🚀 Deploy & CI/CD

- [x] Dockerfile multi-stage (Build: JDK 21 Alpine → Runtime: JRE Alpine)
- [x] JVM flags tối ưu cho Render Free (512MB): `-Xms128m -Xmx350m -XX:+UseSerialGC`
- [x] `-Djava.awt.headless=true` — Fix JasperReports trên môi trường headless (Linux server)
- [x] `server.port=${PORT:8080}` — Tự động dùng PORT của Render
- [x] `GET /api/auth/health` — Endpoint health check cho cron-job.org anti-sleep ping
- [x] Backend deploy thành công lên Render.com: `https://wms-backend-iu98.onrender.com`
- [x] Cron-job.org ping `/api/auth/health` mỗi 10 phút — chống Render ngủ đông
- [x] Frontend deploy lên Vercel với `.env.production` trỏ về Render backend
- [x] TiDB Cloud (MySQL-compatible) dùng làm production database
- [ ] GitHub Actions CI/CD pipeline (tự động test + build khi push)

