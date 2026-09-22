# 🛡️ SELinux & PostgreSQL (Bảo Mật Cấp Độ Nhân Hệ Điều Hành Linux)

**SELinux (Security-Enhanced Linux)** là một mô-đun bảo mật nằm sâu bên trong **Kernel (nhân)** của hệ điều hành Linux (do Cơ quan An ninh Quốc gia Hoa Kỳ - **NSA** phát triển cùng cộng đồng Red Hat). 

Nếu như các tầng bảo mật trước đây chúng ta học (mật khẩu, quyền `GRANT`, RLS) nằm **bên trong PostgreSQL**, thì SELinux là lớp "áo giáp sắt" bảo vệ **từ bên ngoài hệ điều hành**, bao bọc lấy toàn bộ tiến trình PostgreSQL.

---

## 🎯 1. Bài Toán Thực Tế: Vì Sao Cần Đến SELinux?

### Khi không có SELinux (Mô hình DAC truyền thống):
- Giả sử tin tặc khai thác được một lỗ hổng bảo mật dạng Zero-Day trong một Extension của PostgreSQL và **chiếm được quyền điều khiển tiến trình `postgres`** trên server Linux.
- Với quyền user `postgres` của hệ điều hành, hacker có thể:
  - Đọc trộm file cấu hình mạng của server (`/etc/passwd`, `/etc/shadow`).
  - Tải mã độc về thư mục tạm `/tmp` rồi thực thi.
  - Tấn công sang các ứng dụng khác đang chạy chung trên cùng server (như Redis, Nginx).

### Khi BẬT SELinux (Mô hình MAC - Mandatory Access Control):
- SELinux nhốt tiến trình PostgreSQL vào một "chiếc lồng vô hình" (**Sandbox / Security Context**).
- Dù hacker có chiếm được quyền điều khiển user `postgres`, SELinux ở tầng Kernel sẽ **CHẶN ĐỨNG NGAY LẬP TỨC**:
  - *"Tiến trình PostgreSQL chỉ được phép đọc/ghi duy nhất thư mục `/var/lib/postgresql/data`. Cấm tuyệt đối đụng vào `/etc/`, `/home/`, `/tmp/`!"*
  - *"Tiến trình PostgreSQL chỉ được lắng nghe ở cổng `5432`. Cấm mở cổng mạng lạ để liên lạc với máy chủ hacker!"*

```mermaid
graph TD
    Hacker["Hacker tấn công qua lỗ hổng SQL"] --> Postgres["Tiến trình PostgreSQL (Đã bị hack)"]
    Postgres -->|Cố gắng đọc file hệ thống /etc/shadow| Kernel["Linux Kernel + SELinux"]
    Kernel -->|🚨 CHẶN ĐỨNG (Access Denied)| Block["Ghi log cảnh báo & Khóa chặt hacker trong thư mục data"]
```

---

## 🧭 2. Ba Trạng Thái Hoạt Động Của SELinux

Trên server Linux (RHEL, CentOS, Rocky Linux, Fedora), bạn kiểm tra trạng thái SELinux bằng lệnh:
```bash
sestatus
# hoặc
getenforce
```

| Trạng thái | Ý nghĩa | Môi trường áp dụng |
| :--- | :--- | :--- |
| **`Enforcing`** | **Bật bảo vệ tối đa:** Mọi hành vi vi phạm chính sách đều bị chặn đứng và ghi log. | **Bắt buộc trên Production** của Ngân hàng / Doanh nghiệp. |
| **`Permissive`** | **Chế độ cảnh báo:** Không chặn hành vi nhưng vẫn ghi log vi phạm (dùng khi debug lỗi). | Môi trường thử nghiệm / Cấu hình ban đầu. |
| **`Disabled`** | Tắt hoàn toàn SELinux. | Các máy dev cá nhân (cho đỡ phức tạp). |

---

## ⚠️ 3. Lỗi Kinh Điển Mà Mọi Kỹ Sư DevOps/Backend Đều Gặp Phải

> 📌 **Tình huống:** Bạn gắn thêm một ổ cứng SSD dung lượng lớn vào server tại đường dẫn `/data/my_postgres/` và cấu hình Postgres lưu dữ liệu ở đó. Khi bấm khởi động service, Postgres **bị lỗi crash ngay lập tức**, dù bạn đã cấp quyền `chmod -R 777` cho thư mục đó!

- **Thủ phạm:** Chính là **SELinux**! Thư mục mới tạo chưa được dán "nhãn an ninh" (`Security Label`) nên SELinux không cho Postgres chạm vào.
- **Cách khắc phục chuẩn DevOps:** Dán đúng nhãn an ninh cho thư mục mới:
  ```bash
  # Dán nhãn kiểu dữ liệu PostgreSQL cho thư mục mới:
  sudo semanage fcontext -a -t postgresql_db_t "/data/my_postgres(/.*)?"
  sudo restorecon -Rv /data/my_postgres
  ```

---

## 🎯 Tóm Tắt 1 Dòng Để Đi Phỏng Vấn:

> *"SELinux là cơ chế bảo mật cấp độ Kernel Linux (MAC). Đối với PostgreSQL, nó đóng vai trò như một chiếc **Sandbox cô lập**, đảm bảo rằng ngay cả khi tiến trình database bị tấn công chiếm quyền, hacker cũng không thể thoát ra ngoài để phá hoại các tài nguyên khác của hệ điều hành."*
