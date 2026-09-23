# 🐳 Docker Guide — WMS Backend

## 1. Dockerfile Giải Thích

```dockerfile
# Stage 1: BUILD — Dùng JDK đầy đủ để compile Maven
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline    # Cache dependencies
COPY src/ ./src/
RUN ./mvnw clean package -DskipTests

# Stage 2: RUNTIME — Chỉ cần JRE nhỏ gọn để chạy
FROM eclipse-temurin:17-jre-alpine
# Chạy với user không phải root (bảo mật)
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# JVM tối ưu cho Render Free tier 512MB RAM
ENTRYPOINT ["java",
  "-Xms256m", "-Xmx384m", "-Xss512k",
  "-XX:+UseSerialGC",
  "-jar", "app.jar"]
```

**Lợi ích multi-stage:**
- Image cuối chỉ chứa JRE + JAR file (~200MB thay vì ~500MB với JDK)
- Không lộ source code trong image production
- Cache dependency layer → build lại nhanh hơn khi chỉ thay đổi source

---

## 2. Build và Run Docker

### Build image
```bash
docker build -t wms-backend:latest .
```

### Run với biến môi trường
```bash
docker run -d \
  --name wms-backend \
  -p 8080:8080 \
  -e DB_URL="jdbc:mysql://host.docker.internal:3306/warehouse?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true" \
  -e DB_USERNAME="root" \
  -e DB_PASSWORD="" \
  -e JWT_SECRET="my-super-secret-key-for-wms-minimum-32-chars" \
  -e ALLOWED_ORIGINS="http://localhost:3000" \
  wms-backend:latest
```

> **Windows + XAMPP:** Dùng `host.docker.internal` thay `localhost` để container kết nối tới MySQL của máy host.

---

## 3. Docker Compose (Backend + MySQL)

Chạy toàn bộ stack chỉ với 1 lệnh:

```bash
docker compose up -d
```

Xem [docker-compose.yml](docker-compose.yml) để biết cấu hình chi tiết.

**Services:**
- `wms-db` — MySQL 8.0 (port 3307 trên host để không đụng XAMPP)
- `wms-backend` — Spring Boot app (port 8080)

### Dừng và xóa
```bash
docker compose down          # Dừng, giữ data
docker compose down -v       # Dừng + xóa volume (mất data DB)
```

---

## 4. Deploy lên Render.com

### Bước 1: Push code lên GitHub
```bash
git add .
git commit -m "feat: ready for Render deployment"
git push origin main
```

### Bước 2: Tạo Web Service trên Render
1. Đăng nhập [render.com](https://render.com) → **New** → **Web Service**
2. Kết nối GitHub repo `Backend-Warehouse`
3. Cấu hình:
   - **Environment:** Docker
   - **Region:** Singapore (gần VN nhất)
   - **Instance Type:** Free

### Bước 3: Cấu hình Environment Variables
Vào tab **Environment** → thêm các biến sau:

| Key | Value mẫu | Ghi chú |
|-----|-----------|---------|
| `DB_URL` | `jdbc:mysql://gateway01.ap-southeast-1.prod.aws.tidbcloud.com:4000/warehouse?useSSL=true&requireSSL=true` | JDBC URL từ TiDB Cloud |
| `DB_USERNAME` | `3Pxxxxxxxx.root` | Tên đăng nhập TiDB |
| `DB_PASSWORD` | `your-db-password` | Mật khẩu TiDB |
| `JWT_SECRET` | `chuoi-ngau-nhien-bao-mat-lon-hon-32-ky-tu` | Ký JWT Token |
| `ALLOWED_ORIGINS` | `https://your-frontend.vercel.app,http://localhost:3000` | URL Frontend được phép gọi CORS |
| `R2_ACCOUNT_ID` | `<cloudflare-account-id>` | Cloudflare Account ID |
| `R2_ACCESS_KEY` | `<r2-access-key-id>` | Cloudflare R2 Access Key ID |
| `R2_SECRET_KEY` | `<r2-secret-access-key>` | Cloudflare R2 Secret Access Key |
| `R2_BUCKET` | `warehouse` | Tên Cloudflare R2 Bucket |

### Bước 4: Deploy
Render tự động build Docker image (multi-stage) và deploy. Theo dõi tiến độ trong tab **Logs**.

**URL sau khi deploy:** `https://wms-backend-iu98.onrender.com`


---

## 4.1. ⏰ Chống Ngủ Đông Trên Render (Cron-job.org)

Render Free Tier sẽ tự động **ngủ đông (sleep/spin-down)** nếu không có request trong vòng **15 phút**. Khi có người dùng truy cập, service mất ~30-50 giây để khởi động lại.

**Giải pháp miễn phí 100%:** Sử dụng [cron-job.org](https://cron-job.org) để tự động ping giữ ấm backend.

1. Đăng ký tài khoản miễn phí tại **[cron-job.org](https://cron-job.org)**.
2. Bấm **Create Cronjob**.
3. Điền thông tin:
   - **Title:** `Keep-Alive WMS Backend`
   - **URL:** `https://wms-backend-iu98.onrender.com/api/auth/health`
   - **Execution schedule:** Chọn **Every 10 minutes** (mỗi 10 phút ping 1 lần, trước mốc 15 phút của Render).
   - **Request method:** `GET`
4. Bấm **Create**.
5. **Kết quả:** Service Render của bạn sẽ luôn hoạt động liên tục 24/7 mà không bị rơi vào trạng thái ngủ đông!


---

## 5. TiDB Cloud Setup

TiDB Cloud cung cấp MySQL-compatible cloud DB miễn phí:

1. Đăng ký tại [tidbcloud.com](https://tidbcloud.com)
2. Tạo **Serverless Cluster** (miễn phí)
3. Chọn region **AWS - Singapore**
4. Vào **Connect** → **General** → lấy connection string

**Connection string format:**
```
jdbc:mysql://gateway01.ap-southeast-1.prod.aws.tidbcloud.com:4000/warehouse?useSSL=true&requireSSL=true
```

> TiDB Cloud yêu cầu SSL (`useSSL=true`), khác với XAMPP local.

---

## 6. Tạo JWT Secret An Toàn

```bash
# Linux/macOS
openssl rand -base64 32

# PowerShell (Windows)
[Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Maximum 256 }))

# Node.js
node -e "console.log(require('crypto').randomBytes(32).toString('base64'))"
```
