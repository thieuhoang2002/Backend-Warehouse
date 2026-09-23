# 📦 Hướng Dẫn Sử Dụng Hệ Thống Quản Lý Kho

> Dành cho người dùng không chuyên về kỹ thuật — đọc xong là biết dùng ngay!

---

## Mục Lục

1. [Hệ thống này làm gì?](#1-hệ-thống-này-làm-gì)
2. [Các khái niệm cần biết](#2-các-khái-niệm-cần-biết)
3. [Đăng nhập vào hệ thống](#3-đăng-nhập-vào-hệ-thống)
4. [Nhập hàng vào kho](#4-nhập-hàng-vào-kho)
5. [Xếp hàng vào ngăn kệ](#5-xếp-hàng-vào-ngăn-kệ)
6. [Xuất hàng ra khỏi kho](#6-xuất-hàng-ra-khỏi-kho)
7. [Xem thông báo](#7-xem-thông-báo)
8. [In báo cáo và phiếu](#8-in-báo-cáo-và-phiếu)
9. [Tìm kiếm hàng hóa](#9-tìm-kiếm-hàng-hóa)
10. [Chức năng dành cho Quản trị viên (Admin)](#10-chức-năng-dành-cho-quản-trị-viên-admin)
11. [Câu hỏi thường gặp](#11-câu-hỏi-thường-gặp)

---

## 1. Hệ Thống Này Làm Gì?

Hệ thống **Quản lý Kho hàng (WMS)** giúp bạn:

- 📥 **Nhận hàng vào kho** — ghi nhận thông tin hàng hóa từ file Excel/CSV
- 🗺️ **Biết hàng ở đâu** — xem bản đồ 3D kho, biết chính xác từng kiện hàng nằm ở kệ nào, ngăn nào
- 🚚 **Xuất hàng đúng quy trình** — có bước xác nhận, tránh nhầm lẫn
- 🔔 **Nhắc nhở tự động** — hệ thống tự báo khi hàng sắp đến ngày xuất
- 📊 **In phiếu xuất kho, phiếu giao hàng** ngay trong hệ thống

---

## 2. Các Khái Niệm Cần Biết

Hãy hình dung kho hàng thực tế:

```
🏭 Kho hàng (Warehouse)
└── 🗄️ Kệ hàng (Shelf) — ví dụ: Kệ A1, Kệ B2
    └── 📦 Ngăn (Compartment) — mỗi kệ có nhiều ngăn, ví dụ: Ngăn trái, Ngăn giữa, Ngăn phải
        └── 🎁 Hàng hóa (Item) — sản phẩm cụ thể đang nằm trong ngăn đó
```

| Khái niệm | Giải thích đơn giản |
|-----------|---------------------|
| **Kho (Warehouse)** | Toàn bộ nhà kho của bạn |
| **Kệ (Shelf)** | Từng dãy kệ trong kho — mỗi kệ có một loại hàng nhất định (điện tử, quần áo, thực phẩm...) |
| **Ngăn (Compartment)** | Ô nhỏ trên kệ — nơi chứa hàng thực sự |
| **Đơn nhập (Booking)** | Hồ sơ của một lần nhận hàng, gắn với một khách hàng/đơn vị gửi hàng |
| **Hàng hóa (Item)** | Từng sản phẩm được nhập vào kho theo đơn nhập |
| **Phiếu xuất kho (Checkout)** | Bản ghi khi xuất hàng ra, gồm: ai xuất, xuất bao nhiêu, biển số xe, ngày xuất |

---

## 3. Đăng Nhập Vào Hệ Thống

1. Mở trình duyệt (Chrome, Firefox...) và vào địa chỉ website của hệ thống
2. Nhập **Tên đăng nhập** và **Mật khẩu**
3. Nhấn **Đăng nhập**

**Tài khoản mẫu (dùng để thử):**

| Tên đăng nhập | Mật khẩu | Vai trò |
|---------------|----------|---------|
| `admin` | `warehouse2024` | 👑 Quản trị viên |
| `nguyen.van.a` | `staff2024` | 👤 Nhân viên kho |
| `tran.thi.b` | `staff2024` | 👤 Nhân viên kho |

> ⚠️ **Nhớ đổi mật khẩu** khi dùng thật — đừng dùng mật khẩu mẫu trong môi trường thực tế!

---

## 4. Nhập Hàng Vào Kho

### Bước 1 — Chuẩn bị file danh sách hàng

Hàng hóa được nhập vào qua **file CSV** (có thể tạo bằng Excel rồi lưu dạng `.csv`). File phải có đúng các cột sau:

| Tên cột | Ý nghĩa | Ví dụ |
|---------|---------|-------|
| `Name` | Tên sản phẩm | Laptop Dell Inspiron 15 |
| `Type` | Loại hàng | Electronics |
| `Quantity` | Số lượng | 10 |
| `Weight (g)` | Trọng lượng (gam) | 2500 |
| `Checkin Date` | Ngày nhập kho (dd/MM/yyyy) | 23/09/2024 |
| `Checkout Date` | Ngày dự kiến xuất kho (dd/MM/yyyy) | 23/12/2024 |
| `Image` | Link ảnh (để trống nếu không có) | |
| `Useremail` | Email khách hàng gửi hàng | khachhang@gmail.com |
| `Delivery` | Biển số xe giao hàng | 51A-123.45 |

**Ví dụ file mẫu:**

```
Name,Type,Quantity,Weight (g),Checkin Date,Checkout Date,Image,Useremail,Delivery
Laptop Dell Inspiron 15,Electronics,10,2500,23/09/2024,23/12/2024,,ctyabc@gmail.com,51A-123.45
Màn hình Samsung 27 inch,Electronics,5,4200,23/09/2024,23/12/2024,,ctyabc@gmail.com,51A-123.45
Áo thun Nike Size L,Clothing,50,200,23/09/2024,23/03/2025,,shopxyz@gmail.com,51B-456.78
```

> 💡 **Mẹo:** Tạo file này trong Excel, điền dữ liệu, rồi chọn **File → Lưu dưới dạng → CSV UTF-8**

### Bước 2 — Upload file lên hệ thống

1. Vào menu **Quản lý Booking** (hoặc **Nhập hàng**)
2. Nhấn nút **Upload file CSV**
3. Chọn file vừa chuẩn bị
4. Nhấn **Xác nhận**

✅ Hệ thống sẽ tự động tạo đơn nhập và danh sách hàng hóa từ file.

---

## 5. Xếp Hàng Vào Ngăn Kệ

Sau khi nhập hàng, hàng hóa chưa có vị trí cụ thể. Bạn cần **phân kệ** — chỉ định từng sản phẩm nằm ở ngăn nào.

### Các bước thực hiện:

1. Vào **Quản lý Kho** (xem bản đồ 3D kệ hàng)
2. Click vào **ngăn kệ trống** mà bạn muốn xếp hàng vào
3. Chọn **"Thêm hàng vào ngăn"**
4. Chọn **hàng hóa** từ danh sách (chỉ hiện hàng cùng loại với kệ)
5. Nhập **số lượng** muốn xếp vào ngăn này
6. Nhấn **Xác nhận**

> ⚠️ **Lưu ý quan trọng:**
> - Một ngăn chỉ chứa **một loại hàng** tại một thời điểm
> - **Loại hàng phải khớp với loại kệ** — ví dụ: kệ "Electronics" chỉ chứa hàng loại "Electronics"
> - Nếu số lượng nhiều hơn sức chứa 1 ngăn, bạn có thể xếp vào **nhiều ngăn khác nhau**

---

## 6. Xuất Hàng Ra Khỏi Kho

Quy trình xuất hàng gồm **2 bước** để đảm bảo chính xác:

```
Nhân viên khởi tạo xuất kho → Quản lý xác nhận → Hàng chính thức xuất kho
```

### Bước 1 — Nhân viên tạo phiếu xuất

1. Vào bản đồ 3D, click vào **ngăn chứa hàng** cần xuất
2. Chọn **"Xuất kho"**
3. Điền thông tin:
   - **Số tham chiếu (Reference No)** — số của đơn nhập hàng ban đầu
   - **Biển số xe** — biển số xe đến lấy hàng
4. Nhấn **Xác nhận tạo phiếu**

> Ngăn hàng sẽ chuyển sang trạng thái **"Đang chờ xác nhận"** (màu vàng trên bản đồ 3D)

### Bước 2 — Quản lý xác nhận hoặc hủy

1. Vào menu **Danh sách chờ xác nhận**
2. Xem danh sách các phiếu đang chờ
3. Nhấn **"Xác nhận xuất"** nếu đúng, hoặc **"Hủy"** nếu có lỗi
4. Sau khi xác nhận, hàng sẽ được đánh dấu **"Đã xuất kho"** và ngăn kệ được giải phóng

---

## 7. Xem Thông Báo

Hệ thống tự động gửi thông báo trong các trường hợp:

| Tình huống | Hệ thống làm gì |
|-----------|-----------------|
| Hàng **sắp đến ngày xuất kho** | Gửi thông báo nhắc nhở mỗi 30 phút |
| Xuất kho **thành công** | Ghi lại thông báo trong lịch sử |

**Cách xem thông báo:**
1. Nhấn vào **biểu tượng 🔔 chuông** ở góc trên màn hình
2. Danh sách thông báo hiện ra, thông báo chưa đọc sẽ được **tô đậm**
3. Click vào thông báo để đánh dấu đã đọc

---

## 8. In Báo Cáo Và Phiếu

Hệ thống có thể xuất ra **file PDF** cho các loại giấy tờ sau:

| Loại tài liệu | Khi nào dùng |
|---------------|--------------|
| **Phiếu danh sách hàng hóa** | Kiểm kê hàng trong kho |
| **Phiếu giao hàng** | Giao cho người nhận khi xuất kho |
| **Phiếu nhập kho** | Lưu hồ sơ khi nhận hàng vào |
| **Phiếu xuất kho** | Lưu hồ sơ khi xuất hàng đi |

**Cách in:**
1. Vào menu **Báo cáo**
2. Chọn loại phiếu cần in
3. Nhấn **"Tạo PDF"**
4. File PDF sẽ tự động tải về máy — mở và in như bình thường

---

## 9. Tìm Kiếm Hàng Hóa

Cần tìm một mặt hàng cụ thể? Dùng thanh tìm kiếm:

- **Tìm theo tên:** gõ một phần tên sản phẩm, ví dụ `Laptop` hoặc `Dell`
- **Tìm theo mã:** gõ mã sản phẩm, ví dụ `SP0001` hoặc chỉ gõ số `1`

✅ Kết quả sẽ hiện ngay, kèm thông tin vị trí kệ và ngăn chứa hàng.

---

## 10. Chức Năng Dành Cho Quản Trị Viên (Admin)

Nếu bạn đăng nhập bằng tài khoản có quyền **ROLE_ADMIN** (ví dụ tài khoản `admin`), trên thanh menu sẽ xuất hiện mục **"Quản Trị Viên"**:

### 10.1 Quản lý tài khoản nhân viên
* **Xem danh sách:** Xem toàn bộ nhân viên trong hệ thống và vai trò (Admin hoặc Nhân viên).
* **Tạo tài khoản mới:** Thêm tài khoản mới cấp cho nhân viên vận hành kho.
* **Sửa thông tin / Đổi vai trò:** Cập nhật họ tên, email hoặc nâng/hạ quyền hạn (ADMIN ↔ NHÂN VIÊN).
* **Đặt lại mật khẩu:** Cấp lại mật khẩu mới cho nhân viên khi quên mật khẩu.
* **Xóa tài khoản:** Xóa tài khoản nhân viên đã nghỉ việc (hệ thống tự động chặn tự xóa chính mình).

### 10.2 Quản lý Kho & Kệ Hàng
* **Quản lý Kho:** Xem danh sách các kho hàng, số lượng kệ và số ngăn có hàng; thêm kho mới hoặc sửa địa chỉ.
* **Quản lý Kệ:** Xem danh sách kệ hàng, tọa độ 3D và số ngăn. Có thể tạo thêm dãy kệ mới (chọn kho, loại hàng, tọa độ X/Y/Z) và hệ thống sẽ tự động tạo sẵn các ngăn trống cho kệ.
* **Xóa an toàn:** Hệ thống sẽ tự động chặn việc xóa Kho hoặc Kệ nếu bên trong vẫn còn hàng hóa chưa xuất.

### 10.3 Thông tin hệ thống
* Thống kê trực quan toàn bộ số liệu: Tổng số tài khoản, đơn booking, sản phẩm, kho hàng, kệ hàng, ngăn chứa và lịch sử xuất kho.

---

## 11. Câu Hỏi Thường Gặp

**❓ Tôi upload file CSV nhưng báo lỗi?**
> Kiểm tra lại:
> - File phải lưu dạng **CSV UTF-8** (không phải ANSI)
> - Tên cột phải **chính xác** như bảng ở Mục 4
> - Định dạng ngày phải là **dd/MM/yyyy** (ví dụ: `23/09/2024`)
> - Số lượng và trọng lượng **không có dấu phẩy** (viết `2500` thay vì `2,500`)

**❓ Tôi thêm hàng vào ngăn nhưng nó báo lỗi "loại không khớp"?**
> Mỗi kệ chỉ chứa một loại hàng nhất định (Electronics, Clothing, Food...).
> Hãy chọn đúng ngăn thuộc kệ **có cùng loại** với sản phẩm cần xếp.

**❓ Ngăn đang màu vàng, tôi không thể xuất tiếp?**
> Màu vàng nghĩa là ngăn đang **"chờ xác nhận xuất kho"**. Cần quản lý vào xác nhận hoặc hủy phiếu đang chờ trước khi thao tác tiếp.

**❓ Hàng đã "Đã xuất kho" có thể xem lại lịch sử không?**
> Có. Vào **Báo cáo → Phiếu xuất kho** để xem toàn bộ lịch sử xuất. Tuy nhiên, hàng đã xuất quá **6 tháng** sẽ tự động được xóa khỏi hệ thống.

**❓ Tôi quên mật khẩu?**
> Liên hệ **quản trị viên** (tài khoản `admin`) để được cấp lại mật khẩu.

**❓ Thông báo nhắc nhở xuất kho đến lúc nào?**
> Hệ thống tự động kiểm tra mỗi **30 phút một lần**. Nếu hôm nay là ngày xuất kho của một mặt hàng, bạn sẽ nhận được thông báo nhắc nhở.

---

## Liên Hệ Hỗ Trợ

Nếu gặp vấn đề kỹ thuật, hãy liên hệ người quản trị hệ thống để được hỗ trợ.

> 📌 Tài liệu kỹ thuật chi tiết dành cho lập trình viên xem tại [README.md](README.md)
