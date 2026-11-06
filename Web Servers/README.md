# 🌐 Chuyên Đề: Web Servers - Máy Chủ Web Trong Kiến Trúc Backend

Thư mục này tập hợp các tài liệu nghiên cứu chuyên sâu về **Web Server (Máy chủ web)** — thành phần hạ tầng và cửa ngõ tiếp nhận lưu lượng truy cập Internet không thể thiếu trong phát triển Backend và hệ thống phân tán.

---

## 📚 Danh Mục Tài Liệu

1. 🌐 **[Web Server: Bản Chất, Kiến Trúc Xử Lý, Vòng Đời Request & So Sánh Toàn Diện](./01_web_servers_overview_and_architecture.md)**
   - Phân biệt Web Server phần cứng (Hardware) vs phần mềm (Software).
   - Vòng đời xử lý yêu cầu (Request-Response Lifecycle) từ TCP/TLS handshake đến HTTP parsing và nén dữ liệu.
   - Phân định ranh giới kiến trúc: Web Server vs Application Server vs Reverse Proxy vs Forward Proxy.
   - Các mô hình kiến trúc xử lý đồng thời (Concurrency Models): Multi-Process (Prefork), Multi-Threaded (Worker), và Event-Driven Non-blocking I/O (`epoll`/`kqueue`).
   - Ma trận so sánh các Web Server phổ biến nhất: **Nginx, Apache HTTP Server, Microsoft IIS, Caddy**.
   - 6 Trách nhiệm thiết yếu trong Production: SSL/TLS Termination, Load Balancing, Rate Limiting, Nén Gzip/Brotli, Security Headers, Caching Zero-Copy.
   - Mô hình kiến trúc 2 tầng chuẩn công nghiệp (Two-Tier Architecture Best Practice).

