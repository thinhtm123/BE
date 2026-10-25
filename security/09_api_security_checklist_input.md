# 📋 API Security Checklist: Input & Traffic Protection

Tài liệu này chi tiết hóa 5 chốt chặn an ninh tối quan trọng thuộc nhóm **"Input / Traffic Protection"** (Kiểm soát lưu lượng và kết nối đầu vào) trong bộ tiêu chuẩn **API Security Checklist** quốc tế:

```
┌────────────────────────────────────────────────────────────────────────┐
│                        API SECURITY: INPUT                             │
├────────────────────────────────────────────────────────────────────────┤
│ [ ] 1. Limit requests (throttling) to avoid DDoS / brute force         │
│ [ ] 2. Use HTTPS on server side and secure ciphers                     │
│ [ ] 3. Use HSTS header with SSL to avoid SSL strip attacks             │
│ [ ] 4. Turn off directory listings                                     │
│ [ ] 5. Private APIs should only be accessible from safe-listed IPs      │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 🚦 1. Limit Requests (Throttling) To Avoid DDoS / Brute Force

### ❓ Vấn đề:
Nếu không kiểm soát tần suất gọi API:
- Hacker có thể gửi hàng chục nghìn request/giây làm cạn kiệt tài nguyên (CPU, RAM, Database Connections), dẫn đến sập toàn bộ hệ thống (**DDoS - Distributed Denial of Service**).
- Hacker dùng tool tự động dò mật khẩu (Brute-force) hoặc quét vét sạch mã OTP 6 số chỉ trong vài phút.
- Bot quét cào dữ liệu (Web Scraping) làm giảm hiệu năng của người dùng thật.

### 🛡️ Giải pháp kỹ thuật:
1. **Thuật toán áp dụng**:
   - **Token Bucket** hoặc **Sliding Window Counter** kết hợp lưu trữ bộ nhớ đệm **Redis**.
2. **Quy tắc phân tầng Rate Limit**:
   - **Public Read API** (Xem sản phẩm, tin tức): 60 – 100 requests / phút / IP.
   - **Write / Action API** (Thêm giỏ hàng, thanh toán): 20 – 30 requests / phút / User.
   - **Authentication API** (Đăng nhập, Quên pass, Gửi OTP): **Tối đa 5 requests / phút**.
3. **HTTP Response khi vi phạm**:
   - Trả về mã lỗi: **`HTTP 429 Too Many Requests`**.
   - Header bắt buộc: `Retry-After: 60` (Báo cho client biết sau bao nhiêu giây mới được gọi lại).

#### 🛠️ Cấu hình thực tế trên Nginx:
```nginx
# Định nghĩa vùng nhớ theo dõi IP: 10MB lưu được khoảng 160.000 IP
limit_req_zone $binary_remote_addr zone=api_limit:10m rate=10r/s;
limit_req_zone $binary_remote_addr zone=login_limit:10m rate=5r/m;

server {
    location /api/ {
        limit_req zone=api_limit burst=20 nodelay;
        limit_req_status 429;
        proxy_pass http://backend;
    }

    # Áp dụng giới hạn gắt gao cho API login
    location /api/v1/auth/login {
        limit_req zone=login_limit burst=2 nodelay;
        limit_req_status 429;
        proxy_pass http://backend;
    }
}
```

---

## 🔒 2. Use HTTPS On Server Side And Secure Ciphers

### ❓ Vấn đề:
- Bật HTTPS là điều kiện cần, nhưng **chưa đủ**.
- Nếu máy chủ vẫn hỗ trợ các giao thức cũ (`SSLv3`, `TLS 1.0`, `TLS 1.1`) hoặc các bộ mã hóa yếu (Ciphers lỗi thời như `RC4`, `3DES`, `MD5`, các thuật toán CBC dính lỗi POODLE / BEAST / Sweet32), hacker vẫn có thể giải mã và nghe lén được gói tin!

### 🛡️ Giải pháp kỹ thuật:
- **Tắt hoàn toàn**: `SSLv2`, `SSLv3`, `TLSv1.0`, `TLSv1.1`.
- **Chỉ kích hoạt**: **`TLSv1.2`** và **`TLSv1.3`**.
- **Chỉ kích hoạt các bộ Modern AEAD Ciphers** (Authenticated Encryption with Associated Data) như `AES-GCM` hoặc `CHACHA20-POLY1305`.

#### 🛠️ Cấu hình Nginx chuẩn Mozilla SSL Configuration:
```nginx
server {
    listen 443 ssl http2;
    server_name api.mycompany.com;

    ssl_certificate /etc/letsencrypt/live/api.mycompany.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/api.mycompany.com/privkey.pem;

    # Chỉ cho phép TLS 1.2 và 1.3
    ssl_protocols TLSv1.2 TLSv1.3;

    # Bộ mã hóa an toàn cao cấp (Secure Ciphers)
    ssl_ciphers 'ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256:ECDHE-ECDSA-AES256-GCM-SHA384:ECDHE-RSA-AES256-GCM-SHA384:ECDHE-ECDSA-CHACHA20-POLY1305:ECDHE-RSA-CHACHA20-POLY1305';
    ssl_prefer_server_ciphers on;

    # Tối ưu hóa hiệu năng SSL Session Cache
    ssl_session_cache shared:SSL:10m;
    ssl_session_timeout 1d;
}
```

---

## 🛡️ 3. Use HSTS Header With SSL To Avoid SSL Strip Attacks

### ❓ Vấn đề: Cuộc tấn công "Tước đoạt chứng chỉ" (SSL Stripping)
Khi người dùng gõ vào trình duyệt hoặc ứng dụng gọi API lần đầu: `api.mycompany.com`.
1. Theo mặc định, trình duyệt sẽ gửi request qua **`http://`** trước.
2. Server nhận được sẽ trả về Redirect `301 Moved Permanently` sang `https://`.
3. **Lỗ hổng nằm ở đúng bước 1**: Hacker ngồi cùng mạng Wi-Fi (Man-in-the-Middle) sẽ chặn ngay request `http://` đó, tự mình kết nối HTTPS với Server, nhưng **trả về trang HTTP trần cho nạn nhân**. Nạn nhân truyền mật khẩu qua HTTP trần và bị hacker đọc sạch!

```
[ Nạn nhân ] ──(HTTP trần)──> [ Hacker ở giữa ] ──(HTTPS)──> [ Server Thật ]
                               (Đọc trộm dữ liệu)
```

### 🛡️ Giải pháp kỹ thuật: Header HSTS (HTTP Strict Transport Security)
Server trả về header:
```http
Strict-Transport-Security: max-age=31536000; includeSubDomains; preload
```

* **Ý nghĩa**:
  - `max-age=31536000`: Trong vòng 1 năm (31.536.000 giây), trình duyệt **bắt buộc phải tự động đổi `http://` thành `https://` ngay trên máy khách**, trước khi gói tin rời card mạng!
  - `includeSubDomains`: Áp dụng cho toàn bộ các subdomain (`api.`, `auth.`, `admin.`).
  - `preload`: Đăng ký tên miền vào danh sách bảo vệ cứng tích hợp sẵn trong mã nguồn trình duyệt Chrome, Safari, Firefox.

---

## 📂 4. Turn Off Directory Listings (Tắt Duyệt Thư Mục)

### ❓ Vấn đề:
Khi người dùng hoặc hacker truy cập vào một thư mục tĩnh (ví dụ: `https://api.mycompany.com/uploads/` hoặc `/backup/`) mà trong thư mục đó **không có file `index.html` hoặc `index.php`**:
- Nếu Web Server bật tính năng Directory Listing, nó sẽ tự động render một trang HTML hiển thị toàn bộ cây danh mục:
  ```text
  Index of /uploads/
  [DIR]  ..
  [FILE] 2026_db_backup.sql.gz    1.2 GB   2026-10-20 02:00
  [FILE] customer_id_card_01.png   250 KB   2026-10-24 14:15
  [FILE] .env                      1.5 KB   2026-09-10 10:00
  ```
- Hacker chỉ cần bấm tải về là sở hữu toàn bộ mã nguồn, cơ sở dữ liệu và dữ liệu nhạy cảm của khách hàng!

### 🛡️ Giải pháp kỹ thuật:
Tắt hoàn toàn tính năng Directory Listing trên toàn bộ máy chủ web:

#### Trên Nginx (`nginx.conf`):
```nginx
# Đảm bảo autoindex luôn ở trạng thái OFF (mặc định đã tắt):
autoindex off;
```

#### Trên Apache (`httpd.conf` hoặc `.htaccess`):
```apache
# Thêm dấu trừ trước Options Indexes:
Options -Indexes
```

---

## 🏢 5. Private APIs Should Only Be Accessible From Safe-Listed IPs

### ❓ Vấn đề:
Trong bất kỳ hệ thống nào cũng có 2 loại API:
1. **Public APIs**: Dành cho người dùng bên ngoài, mobile app, khách vãng lai gọi vào (`/api/v1/login`, `/api/v1/products`).
2. **Private APIs**: 
   - API quản trị hệ thống (`/admin/**`, `/actuator/**`, `/metrics`).
   - API giao tiếp nội bộ giữa các Microservices.
   - API nhận Webhook từ đối tác thanh toán (VNPay, Momo, Stripe).

Nếu bạn để Private APIs lộ ra ngoài Internet công khai, dù có đặt mật khẩu hay token thì nguy cơ bị dò pass, dính lỗ hổng Zero-day hoặc bị DDoS là cực kỳ lớn.

### 🛡️ Giải pháp kỹ thuật:

#### Cách 1: Thiết lập danh sách trắng IP (IP Whitelisting) tại Nginx
```nginx
location /internal-api/ {
    # Chỉ cho phép IP tĩnh của đối tác thanh toán VNPay:
    allow 118.69.12.34;
    
    # Chỉ cho phép dải IP mạng VPN nội bộ công ty:
    allow 10.0.0.0/8;
    allow 192.168.1.0/24;

    # CẤM TOÀN BỘ CÁC IP CÒN LẠI TRÊN TOÀN CẦU:
    deny all;

    proxy_pass http://backend_internal;
}
```

#### Cách 2: Kiến trúc mạng cô lập (Network Isolation - Chuẩn Cloud / AWS)
- Đưa toàn bộ máy chủ Backend chứa Private API vào **Private Subnet (không gán Public IP)**.
- Người ngoài Internet **hoàn toàn không thể ping hay gửi bất kỳ gói tin nào tới server**.
- Kỹ sư muốn truy cập vào Private API phải kết nối qua **mạng riêng ảo VPN (WireGuard / OpenVPN)** hoặc thông qua máy chủ trung chuyển **Bastion Host (Jump Host)**.

---

## 🎯 Bảng Tổng Kết Nhanh 5 Chốt Chặn Input

| # | Tiêu chí trong ảnh | Mối đe dọa ngăn chặn | Công cụ triển khai |
|---|---|---|---|
| 1 | **Limit requests (throttling)** | DDoS, Brute-force, Scraping | Nginx `limit_req`, Redis Token Bucket, HTTP 429 |
| 2 | **Use HTTPS & secure ciphers** | Nghe lén gói tin, Man-in-the-Middle | TLS 1.2/1.3, AEAD Ciphers (AES-GCM) |
| 3 | **Use HSTS header with SSL** | Tấn công hạ cấp SSL Strip | Header `Strict-Transport-Security: max-age=...` |
| 4 | **Turn off directory listings** | Rò rỉ file cấu hình, file backup `.sql`, file upload | Nginx `autoindex off;`, Apache `Options -Indexes` |
| 5 | **Private APIs safe-listed IPs** | Tấn công API nội bộ, lộ dữ liệu Admin | Nginx IP `allow/deny`, AWS Private VPC, VPN |
