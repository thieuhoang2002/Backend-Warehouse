# 🛠️ Tech Stack — WMS Backend

## Runtime & Framework

| Công nghệ | Phiên bản | Mục đích |
|-----------|-----------|----------|
| **Java** | 21 (LTS) | Ngôn ngữ lập trình chính |
| **Spring Boot** | 3.3.2 | Framework chính, auto-configuration |
| **Spring Web** | _(managed)_ | REST API, HTTP handling |
| **Spring Data JPA** | _(managed)_ | ORM, repository pattern |
| **Spring Security** | _(managed)_ | Authentication & Authorization |
| **Spring Validation** | _(managed)_ | Bean Validation (JSR-380) |
| **Spring WebSocket** | _(managed)_ | Real-time notifications |
| **Spring Scheduling** | _(managed)_ | Cron jobs tự động |

## Database

| Công nghệ | Phiên bản | Mục đích |
|-----------|-----------|----------|
| **MySQL** | 8.0 | Relational database chính |
| **Hibernate** | _(managed by Spring)_ | JPA implementation, DDL auto |
| **mysql-connector-j** | _(managed by Spring)_ | JDBC driver |

## Security & Auth

| Thư viện | Phiên bản | Mục đích |
|----------|-----------|----------|
| **JJWT API** | 0.12.6 | Tạo và verify JWT token |
| **JJWT Impl** | 0.12.6 | Implementation của JJWT |
| **JJWT Jackson** | 0.12.6 | JSON serialization cho JWT |
| **BCrypt** | _(Spring Security)_ | Mã hóa mật khẩu |

> **Tại sao JJWT 0.12.6?** Phiên bản mới nhất, loại bỏ hoàn toàn các deprecated API như `SignatureAlgorithm`, `setSubject()`, `Jwts.parserBuilder()`. API mới gọn hơn và type-safe hơn với `SecretKey`.

## Reports & Files

| Thư viện | Phiên bản | Mục đích |
|----------|-----------|----------|
| **JasperReports** | 6.20.1 | Xuất báo cáo PDF từ template `.jrxml` |
| **Apache Commons CSV** | 1.10.0 | Parse file CSV nhập kho |

## Communication

| Công nghệ | Phiên bản | Mục đích |
|-----------|-----------|----------|
| **STOMP over WebSocket** | _(Spring)_ | Protocol cho real-time messaging |
| **SockJS** | _(client-side)_ | Fallback cho WebSocket |

## Testing

| Thư viện | Phiên bản | Mục đích |
|----------|-----------|----------|
| **Spring Boot Test** | _(managed)_ | Integration test |
| **JUnit Jupiter** | 5.9.2 | Unit test framework |

## DevOps & Deployment

| Công nghệ | Mục đích |
|-----------|----------|
| **Docker** (multi-stage) | Container hóa backend, tách build/runtime image |
| **Render.com** | Cloud hosting backend (Free tier — 512MB RAM) |
| **TiDB Cloud** | MySQL-compatible cloud DB (Free tier) |
| **GitHub Actions** | _(roadmap)_ CI/CD pipeline |

---

## Tại Sao Chọn Stack Này?

### Spring Boot 3.3.x thay vì 2.x
- **Jakarta namespace** — Spring Boot 3+ dùng `jakarta.*` thay `javax.*`, chuẩn cho Java EE 9+
- **Native compilation** — hỗ trợ GraalVM native image (tương lai)
- **Improved Security** — SecurityFilterChain API mới, không còn `WebSecurityConfigurerAdapter` deprecated
- **Java 21 LTS** — Spring Boot 3.3+ là phiên bản khuyến nghị để chạy Java 21

### Stateless JWT thay vì Session
- Backend và Frontend hoàn toàn tách biệt (decoupled)
- Dễ scale horizontal, không cần sticky session
- Phù hợp với React SPA và mobile client trong tương lai

### JasperReports thay vì iText/Apache POI
- Template `.jrxml` cho phép thiết kế báo cáo visual bằng Jaspersoft Studio
- Hỗ trợ xuất PDF, Excel, HTML từ cùng một template
- Đã được sử dụng rộng rãi trong môi trường doanh nghiệp

### Pessimistic Locking cho Checkout
- Vấn đề: Nếu 2 nhân viên checkout cùng 1 ngăn kệ đồng thời → race condition → tồn kho sai
- Giải pháp: `@Lock(PESSIMISTIC_WRITE)` + `@Transactional(REPEATABLE_READ)` đảm bảo chỉ 1 transaction thao tác tại một thời điểm
