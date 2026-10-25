# 🛡️ Chuyên Đề: Web Security & Bảo Mật Hệ Thống (Web & API Security)

Thư mục này tổng hợp toàn diện các kiến thức về **Bảo mật Ứng dụng Web & API**, các kỹ thuật tấn công kinh điển trong **OWASP Top 10**, và chiến lược phòng thủ chiều sâu (**Defense in Depth**) dành cho lập trình viên Backend và System Architect.

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

8. 🛡️ **[API Security Best Practices (Cẩm Nang Bảo Vệ API Chuẩn Doanh Nghiệp)](./08_api_security_best_practices.md)**
   - 10 Trụ cột bảo vệ API: Chống BOLA (IDOR) trên từng ID, chống gán lén quyền (Mass Assignment).
   - Rate Limiting & Throttling (Status 429), giới hạn kích thước Payload JSON (Body limit).
   - Bắt buộc dùng DTO (không trả Entity thô chứa mật khẩu), bắt buộc phân trang (Pagination).
   - Kiến trúc **API Gateway** tập trung, chuẩn hóa mã lỗi (RFC 7807), và che giấu dữ liệu nhạy cảm trong Log.

9. 📋 **[API Security Checklist: Input & Traffic Protection](./09_api_security_checklist_input.md)**
   - Giải mã chi tiết 5 chốt chặn kiểm soát lưu lượng đầu vào của API.
   - Throttling chống DDoS & Brute-force (Status 429 + Token Bucket).
   - HTTPS với Secure Ciphers (chỉ dùng TLS 1.2/1.3 và AEAD Ciphers, tắt RC4/3DES/CBC).
   - Cơ chế phòng thủ tấn công **SSL Strip** bằng Header **HSTS**.
   - Tắt duyệt danh mục tệp tin (**Turn off directory listings**) chống lộ file `.env`, file `.sql` backup.
   - Khóa chặt API nội bộ (**Private APIs**) chỉ cho phép truy cập từ dải IP an toàn (IP Whitelist / Private VPC).

10. ⚙️ **[API Security Checklist: Processing (Xử Lý Dữ Liệu An Toàn)](./10_api_security_checklist_processing.md)**
    - 9 Tiêu chuẩn an ninh bảo vệ tầng xử lý dữ liệu và logic nghiệp vụ.
    - Triết lý "Mặc định từ chối" (Deny by Default) chống bỏ sót endpoint chưa xác thực.
    - Tránh User ID trên URL (dùng `/me/orders`), ưu tiên UUIDv7 chống đoán ID và lộ doanh số.
    - Phòng chống lỗ hổng đọc file **XXE** và sập RAM **Billion Laughs (XML/YAML Bomb)**.
    - Kiến trúc upload file qua **CDN & Presigned URL**; xử lý dữ liệu lớn bất đồng bộ (Queue `202 Accepted`) chống treo kết nối HTTP.
    - Tắt hoàn toàn Debug mode trên Production và kích hoạt cờ phần cứng **Non-executable stacks (NX/DEP)**.

11. 📤 **[API Security Checklist: Output (Bảo Vệ Dữ Liệu Phản Hồi)](./11_api_security_checklist_output.md)**
    - 7 Tiêu chuẩn an ninh kiểm soát dữ liệu và tiêu đề phản hồi của API.
    - Chống trình duyệt đoán mò định dạng (MIME Sniffing) với `X-Content-Type-Options: nosniff`.
    - Chống Clickjacking với `X-Frame-Options: DENY` và vô hiệu hóa script với `Content-Security-Policy: default-src 'none'`.
    - Xóa dấu vết công nghệ (**Remove fingerprinting headers**: ẩn `X-Powered-By`, ẩn phiên bản Apache/Nginx).
    - Ép cứng `Content-Type: application/json; charset=utf-8` và chống rò rỉ dữ liệu nhạy cảm (Dùng DTO, `@JsonIgnore`).
    - Chuẩn hóa mã phản hồi HTTP Semantics (200, 201, 204, 400, 401, 403, 404, 422, 429, 500).

12. 🚀 **[API Security Checklist: CI / CD (Tích Hợp & Triển Khai DevSecOps)](./12_api_security_checklist_cicd.md)**
    - 5 Tiêu chuẩn an ninh tự động hóa trên đường ống CI/CD (Shift-Left Security).
    - Viết Security Unit/Integration Test: Test mã lỗi 401, 403, test chống BOLA (IDOR), test Rate limit 429.
    - Quy trình Code Review bắt buộc trên GitHub/GitLab: Cấm tự phê duyệt (no self-approval), bắt buộc 2 approvals.
    - Quét mã nguồn tĩnh (**SAST** với Semgrep/SonarQube) và chặn lộ API Key (**Secret Scanning** với Gitleaks).
    - Quét lỗ hổng trong thư viện bên thứ 3 (**SCA** với Dependabot, OWASP Dependency-Check chống Log4Shell).
    - Thiết kế cơ chế **Rollback** tức thì (Chiến lược Blue-Green Deployment, Kubernetes undo rollout).

13. 📊 **[API Security Checklist: Monitoring (Giám Sát & Phát Hiện Xâm Nhập)](./13_api_security_checklist_monitoring.md)**
    - 5 Tiêu chuẩn an ninh xây dựng hệ thống radar giám sát và phát hiện kẻ đột nhập.
    - Tập trung hóa toàn bộ Log (**Centralized Logging**: ELK Stack Kibana / Grafana Loki) thay vì xem log rời rạc.
    - Cài đặt **Monitoring Agents** theo dõi 4 chỉ số vàng (Latency, Traffic, Errors, Saturation với Prometheus/Datadog).
    - Thiết lập hệ thống cảnh báo đa kênh tức thì: Cảnh báo dò pass (nhiều lỗi 401) bắn về Slack/SMS/PagerDuty.
    - Kỹ thuật mặt nạ dữ liệu (**Log Masking**): Che giấu số thẻ ngân hàng, email, mật khẩu trong log theo chuẩn PCI-DSS.
    - Hệ thống phát hiện xâm nhập **IDS** ("camera an ninh") và ngăn chặn xâm nhập **IPS** ("bảo vệ có vũ trang") với WAF, Fail2ban, Suricata.
