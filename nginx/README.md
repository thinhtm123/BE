# 🌐 Chuyên Đề: Nginx - Web Server, Reverse Proxy & Load Balancer

Thư mục này tập trung vào các kiến thức toàn diện về **Nginx (Engine-X)** — máy chủ web và reverse proxy hiệu năng cao số 1 thế giới, đóng vai trò là "cửa ngõ" đón tiếp toàn bộ lưu lượng truy cập Internet trước khi chuyển vào các dịch vụ Backend (Spring Boot, Node.js, Go).

---

## 📚 Danh Mục Tài Liệu

1. ⚡ **[Nginx Fundamentals & Architecture (Bản Chất & Kiến Trúc Xử Lý)](./01_nginx_fundamentals_and_architecture.md)**
   - Nguồn gốc & Bài toán C10K huyền thoại (Tại sao Nginx đánh bại Apache?).
   - Kiến trúc Master-Worker & Cơ chế Asynchronous Event-Driven Non-blocking I/O.
   - 4 Vai trò tối thượng: Web Server tĩnh, Reverse Proxy, Load Balancer, SSL Termination.
   - Giải phẫu khung sườn file cấu hình `nginx.conf` (`http`, `server`, `location`, `upstream`).
   - Các câu lệnh quản trị sống còn: `nginx -t`, `nginx -s reload`.
