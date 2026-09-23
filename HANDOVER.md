# 🤝 Handover Guide — WMS Backend

## 1. Yêu Cầu Môi Trường

| Phần mềm | Phiên bản tối thiểu | Ghi chú |
|----------|---------------------|---------|
| Java JDK | 21+ | OpenJDK hoặc Oracle JDK |
| MySQL | 8.0+ | Hoặc XAMPP (MySQL 8.x) |
| Maven | 3.9+ | Hoặc dùng `mvnw` wrapper |
| Git | any | |

> **Windows + XAMPP:** Đảm bảo XAMPP đã khởi động Apache và MySQL trước khi chạy backend.

---

## 2. Setup Local (Step-by-Step)

### Bước 1: Clone repo
```bash
git clone https://github.com/thieuhoang2002/Backend-Warehouse.git
cd Backend-Warehouse
```

### Bước 2: Tạo database
```sql
-- Mở phpMyAdmin hoặc MySQL Workbench
CREATE DATABASE warehouse
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

### Bước 3: Tạo file cấu hình
```bash
cp src/main/resources/application.properties.example \
   src/main/resources/application.properties
```

Chỉnh sửa `application.properties`:
```properties
# Nếu dùng XAMPP mặc định (root, no password):
spring.datasource.url=jdbc:mysql://localhost:3306/warehouse?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=

# JWT Secret - phải >= 32 ký tự
backend.warehouse.app.jwtSecret=my-super-secret-key-for-wms-app-2024

# Cloudflare R2 Storage (S3-compatible)
r2.account-id=your-cloudflare-account-id
r2.access-key=your-r2-access-key-id
r2.secret-key=your-r2-secret-access-key
r2.bucket=your-bucket-name
```

### Bước 4: Chạy backend
```bash
# Windows (PowerShell)
.\mvnw.cmd spring-boot:run

# Linux/macOS
./mvnw spring-boot:run
```

**Kết quả thành công:**
```
Started BackendWarehouseApplication in X.XXX seconds
Hibernate: create table users (...)
Hibernate: create table booking (...)
...
```

> Hibernate tự tạo toàn bộ 9 bảng — **không cần chạy SQL thủ công**.

### Bước 5: Tạo tài khoản đầu tiên

```bash
# Tạo tài khoản Admin
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123",
    "profileName": "Administrator",
    "email": "admin@example.com",
    "role": "ADMIN"
  }'
```

---

## 3. Bộ cURL Test Đầy Đủ

### 3.1 Authentication

```bash
# ✅ Đăng nhập
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'

# Response:
# {"token":"eyJ...", "id":1, "username":"admin", "profileName":"Administrator", "email":"admin@example.com"}

# Lưu token để dùng tiếp (PowerShell):
$TOKEN = "eyJ..."

# ✅ Đăng ký tài khoản Staff
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "staff01",
    "password": "staff123",
    "profileName": "Nguyen Van A",
    "email": "staff01@example.com"
  }'
```

### 3.2 Nhập Kho (Booking + Items)

```bash
# ✅ Upload file CSV để tạo booking
curl -X POST http://localhost:8080/api/booking/upload \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@path/to/items.csv"

# ✅ Lấy danh sách bookings
curl http://localhost:8080/api/booking/all \
  -H "Authorization: Bearer $TOKEN"

# ✅ Lấy danh sách hàng hóa
curl http://localhost:8080/api/product/all \
  -H "Authorization: Bearer $TOKEN"

# ✅ Tìm kiếm hàng theo tên
curl "http://localhost:8080/api/product/search?data=Laptop" \
  -H "Authorization: Bearer $TOKEN"
```

> **CSV mẫu** (`items.csv`):
> ```
> Name,Type,Quantity,Weight (g),Checkin Date,Checkout Date,Image,Useremail,Delivery
> Laptop Dell,Electronics,5,2500,23/09/2024,23/12/2024,,user@mail.com,51A-123.45
> Màn hình Samsung,Electronics,10,3200,23/09/2024,23/12/2024,,user@mail.com,51A-123.45
> ```

### 3.3 Tạo Warehouse & Shelf & Compartment

```bash
# ✅ Tạo Warehouse
curl -X POST http://localhost:8080/api/warehouse \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name": "Kho A", "location": "Tầng 1, Tòa nhà B"}'

# ✅ Tạo Shelf (thay {warehouseId} bằng ID vừa tạo)
curl -X POST http://localhost:8080/api/shelf \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nameShelf": "Kệ A1",
    "type": "Electronics",
    "position": {"xCoord": 1.0, "yCoord": 0.0, "zCoord": 2.0},
    "warehouse": {"warehouseId": 1}
  }'

# ✅ Tạo Compartment trong Shelf
curl -X POST http://localhost:8080/api/compartments/1 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"nameComp": "A1-L1-L", "layerIndex": 1, "side": 1, "hasItem": false, "quantity": 0}'
```

### 3.4 Phân Kệ và Xuất Kho

```bash
# ✅ Gán Item vào Compartment (thay {compartmentId} và itemId)
curl -X POST "http://localhost:8080/api/compartments/1/addItem" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"itemId": 1, "quantity": 3}'

# ✅ Khởi tạo xuất kho
curl -X POST "http://localhost:8080/api/compartments/1/checkout/1?referenceNo=12345&delivery=51A-123.45" \
  -H "Authorization: Bearer $TOKEN"

# ✅ Xem danh sách chờ xác nhận
curl http://localhost:8080/api/compartments/checkout/pending \
  -H "Authorization: Bearer $TOKEN"

# ✅ Xác nhận checkout
curl -X POST http://localhost:8080/api/compartments/checkout/confirm/1 \
  -H "Authorization: Bearer $TOKEN"
```

### 3.5 Báo Cáo PDF

```bash
# ✅ Xuất PDF danh sách hàng hóa
curl http://localhost:8080/api/jasper/generate-pdf-item \
  -H "Authorization: Bearer $TOKEN" \
  --output items_report.pdf
```

---

## 4. Lưu Ý Quan Trọng

### application.properties
- File này **nằm trong `.gitignore`** — không được commit lên Git
- Khi clone repo lần đầu, luôn phải tạo từ `.example`
- Khi deploy lên Render, dùng Environment Variables thay vì file

### Thư mục uploads/
- Chứa các file CSV đã upload
- Không được commit (đã trong `.gitignore`)
- Khi deploy Docker, cần mount volume nếu muốn persist

### CORS
- Development: `app.cors.allowed-origins=http://localhost:3000`
- Production: Thêm URL frontend vào biến môi trường `ALLOWED_ORIGINS`
- Nhiều origin: ngăn cách bằng dấu phẩy `http://localhost:3000,https://yourapp.netlify.app`

### WebSocket test
```javascript
// Kết nối WebSocket từ browser console
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);
stompClient.connect({}, () => {
  stompClient.subscribe('/topic/notifications', (msg) => {
    console.log(JSON.parse(msg.body));
  });
});
```
