# 🛡️ Chuyên Đề: Web Security & Phòng Thủ Hệ Thống (Web Security Fundamentals)

Thư mục này tổng hợp toàn diện các kiến thức về **Bảo mật Ứng dụng Web (Web Application Security)**, các lỗ hổng kinh điển trong **OWASP Top 10**, các kỹ thuật tấn công phổ biến và chiến lược phòng thủ chiều sâu (**Defense in Depth**) dành cho lập trình viên Backend và System Architect.

---

## 📚 Danh Mục Tài Liệu

1. 🏰 **[Web Security Fundamentals (Toàn Cảnh An Ninh Web & Tam Giác CIA)](./01_web_security_fundamentals.md)**
   - Tam giác bảo mật kinh điển: **CIA Triad** (Confidentiality, Integrity, Availability).
   - Mô hình phòng thủ đa tầng (**Defense in Depth**).
   - Toàn cảnh các mối đe dọa web: Injection, XSS, CSRF, IDOR, SSRF, Broken Access Control.
   - Nguyên tắc đặc quyền tối thiểu (**Principle of Least Privilege**) & Không tin tưởng ai (**Zero Trust**).

2. 🔐 **[Thuật Toán Băm: MD5, SHA, bcrypt, scrypt](./02_hashing_algorithms_md5_sha_bcrypt_scrypt.md)**
   - Bản chất: Fast Hashing (Băm nhanh kiểm tra file) vs Slow Hashing (Cố tình băm chậm để lưu mật khẩu).
   - Vì sao cấm tuyệt đối dùng MD5 & SHA-256 để lưu mật khẩu (Nguy cơ vét cạn Brute-force bằng GPU).
   - Cơ chế bảo vệ của `bcrypt` (Cost factor / Salt tự động) và `scrypt` (Memory-hard chống chip ASIC).
   - Bảng so sánh 4 thuật toán và chuẩn mới nhất `Argon2`.

3. 🔒 **[HTTPS & SSL/TLS: Lá Chắn Bảo Vệ Đường Truyền](./03_https_and_ssl_tls.md)**
   - Bản chất: HTTPS = HTTP + TLS (Mã hóa đường truyền chống nghe lén Man-in-the-Middle).
   - Phân biệt lịch sử: SSL (đã khai tử) vs TLS 1.2 / TLS 1.3 hiện đại.
   - Cơ chế lai thiên tài: Dùng Mã hóa Bất đối xứng (Asymmetric) để bắt tay Handshake, rồi chuyển sang Mã hóa Đối xứng (AES) để truyền data siêu tốc.
   - Sơ đồ bắt tay **TLS Handshake** chi tiết & Chuỗi tin cậy **Chain of Trust (CA)**.
   - Hướng dẫn cấu hình Nginx chuẩn với Let's Encrypt (Certbot) và cờ bảo mật **HSTS**.

4. 🌐 **[CORS: Cross-Origin Resource Sharing (Cơn Ác Mộng Lập Trình Viên)](./04_cors_cross_origin_resource_sharing.md)**
   - Gốc rễ: Chính sách cùng nguồn gốc **Same-Origin Policy (SOP)** bảo vệ người dùng khỏi web độc.
   - Vì sao Postman/curl gọi API 200 OK mượt mà, nhưng React/Vue gọi lại bị chặn đỏ lòm?
   - Phân biệt **Simple Request** vs **Preflight Request (OPTIONS)** thăm dò.
   - Lỗi kinh điển khi gửi kèm Cookie (`withCredentials` cấm dùng dấu sao `*`).
   - Cấu hình chuẩn trong **Spring Boot 3.x** và **Nginx Reverse Proxy**.

5. 🛡️ **[CSP & Các Web Security Headers (Khắc Tinh Của XSS & Clickjacking)](./05_csp_and_security_headers.md)**
   - **Content Security Policy (CSP)**: Thiết lập "Luật giới nghiêm" Whitelist nguồn tài nguyên được tải.
   - Cơ chế tiêu diệt XSS: Cấm inline `<script>` và hàm `eval()`, chốt chặn tẩu tán dữ liệu `connect-src`.
   - Chế độ thử nghiệm an toàn không lỗi: `Content-Security-Policy-Report-Only`.
   - Bộ tứ bảo vệ bắt buộc: `X-Frame-Options` (Clickjacking), `X-Content-Type-Options` (MIME Sniffing), `HSTS`, `Referrer-Policy`.
   - File cấu hình Nginx mẫu đạt chuẩn **A+ Security Headers**.

6. ☠️ **[OWASP Top 10: Bảng Phong Thần 10 Lỗ Hổng Nguy Hiểm Nhất](./06_owasp_top_10_risks.md)**
   - Tiêu chuẩn an ninh bắt buộc trong mọi cuộc kiểm toán ngân hàng & doanh nghiệp lớn.
   - Phân tích chi tiết kèm code mẫu: **A01: Broken Access Control (IDOR), A03: SQL Injection, A10: SSRF (Lừa server đọc AWS keys)**...
   - Bảng tổng hợp giải pháp phòng thủ then chốt cho kỹ sư Backend.

7. 🖥️ **[Server Security & Hardening (Gia Cố Bảo Mật Máy Chủ Linux)](./07_server_security_and_hardening.md)**
   - Thực tế phũ phàng: Vừa mở IP công khai là hàng nghìn botnet tự động tấn công Brute-force.
   - 7 Trụ cột gia cố: Khóa chặt SSH (`PermitRootLogin no`, tắt password, dùng SSH Key Ed25519, đổi port).
   - Tường lửa **UFW** (nguyên tắc "mặc định đóng", tuyệt đối không mở cổng Database 5432/3306 ra ngoài).
   - Tự động bỏ tù IP hacker với **Fail2ban** & tự động vá lỗi Kernel với **Unattended Upgrades**.
   - Nguyên tắc **Non-Root** cho ứng dụng Backend và chiến lược **Sao lưu dữ liệu 3-2-1**.
