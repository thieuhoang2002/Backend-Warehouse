# 🏭 Backend Warehouse Management System (WMS)

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.2-brightgreen?logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql)
![JWT](https://img.shields.io/badge/Auth-JWT-yellow?logo=jsonwebtokens)
![Docker](https://img.shields.io/badge/Deploy-Docker%20%2B%20Render-blueviolet?logo=docker)
![License](https://img.shields.io/badge/License-MIT-lightgrey)

> **Đồ án Khóa luận Tốt nghiệp** — Hệ thống Quản lý Kho hàng 3D, được chuẩn hóa và nâng cấp lên chuẩn production năm 2026.

---

## 📋 Mô Tả Dự Án

Hệ thống WMS (Warehouse Management System) cho phép quản lý kho hàng với giao diện 3D trực quan. Dữ liệu hàng hóa được nhập từ file CSV, phân bổ vào các ngăn kệ (Compartment), và theo dõi toàn bộ vòng đời từ nhập kho đến xuất kho.

### Tính năng chính
- 📦 **Nhập kho** — Upload file CSV chứa danh sách hàng, tạo Booking và Items tự động
- 🗃️ **Quản lý vị trí** — Phân kệ 3D: Warehouse → Shelf → Compartment với tọa độ (x, y, z)
- 🚚 **Xuất kho** — Flow: Checkout → Xác nhận → Cập nhật tồn kho, với Pessimistic Locking chống race condition
- 🔔 **Thông báo real-time** — WebSocket/STOMP: nhắc nhở checkout, cảnh báo sắp hết hạn
- 📊 **Báo cáo PDF** — JasperReports: phiếu nhập, phiếu giao hàng, phiếu xuất kho
- 🔐 **Bảo mật** — JWT Stateless, phân quyền ROLE_ADMIN / ROLE_STAFF
- ⏰ **Tự động hóa** — Scheduler nhắc checkout (30 phút/lần), dọn dẹp dữ liệu cũ (0h/ngày)

---

## 🏗️ Kiến Trúc Hệ Thống

```
┌─────────────────────────────────────────────────────┐
│                    Frontend (React)                  │
│         3D Viewer (Three.js) + MUI Dashboard         │
└──────────────────────┬──────────────────────────────┘
                       │ HTTP/REST + WebSocket
┌──────────────────────▼──────────────────────────────┐
│              Backend (Spring Boot 3.3.2)             │
│  Controllers → Services → Repositories → MySQL DB   │
│  Spring Security (JWT) + WebSocket (STOMP/SockJS)   │
└──────────────────────┬──────────────────────────────┘
                       │ JDBC
┌──────────────────────▼──────────────────────────────┐
│                MySQL 8.0 (TiDB Cloud / XAMPP)        │
│  9 bảng: users, items, booking, compartment, shelf   │
│           warehouseinfo, position, checkoutrecord,   │
│           notification                               │
└─────────────────────────────────────────────────────┘
```

---

## 🚀 Bắt Đầu Nhanh

### Yêu cầu
- Java 21+
- MySQL 8.0+ (hoặc XAMPP)
- Maven (hoặc dùng `mvnw` wrapper có sẵn)

### 1. Clone và cấu hình
```bash
git clone https://github.com/thieuhoang2002/Backend-Warehouse.git
cd Backend-Warehouse

# Tạo file cấu hình từ template
cp src/main/resources/application.properties.example \
   src/main/resources/application.properties
# Chỉnh sửa application.properties với thông tin DB của bạn
```

### 2. Tạo database
```sql
CREATE DATABASE warehouse CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Chạy backend
```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux/macOS
./mvnw spring-boot:run
```

Backend khởi động tại: `http://localhost:8080`

> Hibernate tự động tạo toàn bộ bảng qua `ddl-auto=update` — không cần chạy SQL thủ công.

---

## 🐳 Deploy với Docker

Xem hướng dẫn đầy đủ tại **[DOCKER.md](DOCKER.md)**

```bash
# Build image
docker build -t wms-backend .

# Run với env vars
docker run -p 8080:8080 \
  -e DB_URL="jdbc:mysql://host:3306/warehouse" \
  -e DB_USERNAME="root" \
  -e DB_PASSWORD="password" \
  -e JWT_SECRET="your-secret-key-here" \
  wms-backend
```

---

## 📚 Tài Liệu

| File | Nội dung |
|------|----------|
| [PROJECT_SPEC.md](PROJECT_SPEC.md) | Đặc tả nghiệp vụ, ERD, luồng xử lý |
| [TECHSTACK.md](TECHSTACK.md) | Công nghệ sử dụng và lý do chọn |
| [TODO.md](TODO.md) | Danh sách endpoint + roadmap cải tiến |
| [HANDOVER.md](HANDOVER.md) | Hướng dẫn setup local + cURL test |
| [DOCKER.md](DOCKER.md) | Dockerfile, docker-compose, deploy Render |

---

## 🔑 Biến Môi Trường

| Biến | Mô tả | Mặc định (local) |
|------|-------|-----------------|
| `DB_URL` | JDBC URL kết nối MySQL | `jdbc:mysql://localhost:3306/warehouse` |
| `DB_USERNAME` | Tên đăng nhập DB | `root` |
| `DB_PASSWORD` | Mật khẩu DB | _(rỗng)_ |
| `JWT_SECRET` | Secret key ký JWT (≥ 32 ký tự) | _(xem example)_ |
| `ALLOWED_ORIGINS` | CORS origins, cách nhau bằng dấu phẩy | `http://localhost:3000` |

---

## 📁 Cấu Trúc Thư Mục

```
Backend-Warehouse/
├── src/main/java/com/backend/warehouse/
│   ├── controller/      # REST Controllers (~40 endpoints)
│   ├── service/         # Business logic (Interface + Impl)
│   ├── repository/      # Spring Data JPA Repositories
│   ├── entity/          # JPA Entities (9 bảng)
│   ├── payload/         # DTOs (Request/Response)
│   ├── security/        # Spring Security + JWT
│   ├── config/          # WebSocket Config
│   └── scheduler/       # Scheduled Jobs
├── src/main/resources/
│   ├── application.properties.example
│   └── reports/         # JasperReports .jrxml templates
├── Dockerfile
├── docker-compose.yml
└── README.md
```

---

## 🛡️ Bảo Mật

- **Authentication:** JWT Bearer Token (JJWT 0.12.6, HS256)
- **Authorization:** Role-based — `ROLE_ADMIN`, `ROLE_STAFF`
- **Session:** Stateless (không lưu session phía server)
- **CORS:** Cấu hình tập trung qua biến `ALLOWED_ORIGINS`
- **Secrets:** Toàn bộ credentials qua Environment Variables, KHÔNG hardcode

---

## 📄 License

MIT License — xem [LICENSE](LICENSE) để biết thêm.
