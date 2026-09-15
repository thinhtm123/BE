# 🔌 Kết Nối & Thao Tác PostgreSQL Bằng `psql` CLI

`psql` là công cụ dòng lệnh (CLI) chính thức, mạnh mẽ nhất đi kèm với PostgreSQL. Bất kể bạn kết nối vào database trên máy cá nhân, container Docker hay Cloud (AWS RDS, Supabase), `psql` là kỹ năng bắt buộc phải thành thạo.

---

## 🚀 1. Cú Pháp Kết Nối Cơ Bản

### A. Kết Nối Local (Mặc Định)
```bash
# Kết nối vào database 'postgres' với user hiện tại của hệ điều hành
psql -d postgres

# Kết nối với user cụ thể (sẽ nhắc nhập mật khẩu nếu có flag -W)
psql -U postgres -d postgres
```

### B. Kết Nối Từ Xa / Docker / Cloud (Qua Host & Port)
```bash
# Cú pháp tham số cờ (Flags)
psql -h localhost -p 5432 -U postgres -d mydb

# Hoặc dùng Connection String (URI) — Chuẩn thường gặp trong file .env Backend
psql "postgresql://postgres:password@localhost:5432/mydb?sslmode=disable"
```

| Tham số | Ý nghĩa | Mặc định |
| :--- | :--- | :--- |
| `-h` (`--host`) | Địa chỉ IP / hostname của server | `localhost` (Unix Socket) |
| `-p` (`--port`) | Cổng kết nối PostgreSQL | `5432` |
| `-U` (`--username`) | Tên người dùng database | Tên user hệ điều hành hiện tại |
| `-d` (`--dbname`) | Tên cơ sở dữ liệu muốn vào | Trùng với tên username |
| `-W` (`--password`) | Bắt buộc nhắc nhập mật khẩu | Tự động hỏi nếu cần |

---

## ⚡ 2. Bảng Lệnh Meta (Meta-Commands) Cốt Lõi

> ⚠️ **Lưu ý:** Lệnh meta bắt đầu bằng dấu gạch chéo ngược `\` và **KHÔNG** cần dấu chấm phẩy `;` ở cuối.

### 📌 Nhóm Điều Hướng & Thông Tin

| Lệnh | Ý Nghĩa / Công Dụng | Tương Đương MySQL |
| :--- | :--- | :--- |
| `\q` | **Thoát** khỏi `psql` | `exit` hoặc `quit` |
| `\?` | Xem danh sách trợ giúp các lệnh `\` | `help` |
| `\h [COMMAND]` | Tra cứu cú pháp lệnh SQL (vd: `\h CREATE TABLE`) | `\h` |
| `\l` hoặc `\l+` | Liệt kê tất cả **Databases** (`+` để xem dung lượng, quyền) | `SHOW DATABASES;` |
| `\c <dbname>` | **Chuyển** sang làm việc với Database khác | `USE <dbname>;` |
| `\c <dbname> <user>` | Chuyển database đồng thời đổi user đăng nhập | - |

---

### 📋 Nhóm Tra Cứu Schema & Bảng

| Lệnh | Ý Nghĩa / Công Dụng | Tương Đương MySQL |
| :--- | :--- | :--- |
| `\dt` hoặc `\dt+` | Liệt kê các **Bảng (Tables)** trong schema hiện tại | `SHOW TABLES;` |
| `\dt <schema>.*` | Liệt kê bảng trong một schema cụ thể (vd: `\dt auth.*`) | - |
| `\d <table_name>` | **Xem cấu trúc chi tiết** của bảng (cột, kiểu dữ liệu, index, FK) | `DESCRIBE <table>;` |
| `\d+ <table_name>` | Xem chi tiết kèm thống kê, comment, storage size | - |
| `\dn` | Liệt kê tất cả các **Schemas** | - |
| `\di` | Liệt kê các **Indexes** (Mục lục tìm kiếm) | `SHOW INDEX FROM ...` |
| `\dv` | Liệt kê các **Views** (Bảng ảo) | - |
| `\df` | Liệt kê các **Functions / Stored Procedures** | `SHOW FUNCTION STATUS;` |
| `\du` | Liệt kê danh sách **Users / Roles** và quyền hạn | `SELECT * FROM mysql.user;` |

---

## ⏱️ 3. Mẹo Thực Chiến Cho Backend Developer

### 1. Bật hiển thị thời gian chạy câu lệnh (`\timing`)
Rất hữu ích khi cần đo hiệu năng và benchmark query:
```text
postgres=# \timing
Timing is on.
postgres=# SELECT count(*) FROM users;
 count 
-------
 50000
Time: 4.125 ms
```

### 2. Chế độ xem mở rộng (`\x` - Expanded Display)
Khi một bảng có quá nhiều cột, hiển thị dạng dòng sẽ bị vỡ nát màn hình. Bật `\x` sẽ chuyển sang hiển thị từng cột theo hàng dọc (giống `\G` của MySQL):
```text
postgres=# \x auto
Expanded display is used automatically.
```

### 3. Thực thi file script SQL từ bên ngoài (`-f` hoặc `\i`)
```bash
# Từ ngoài Terminal:
psql -U postgres -d mydb -f schema.sql

# Hoặc ngay trong giao diện psql:
postgres=# \i /path/to/script.sql
```

### 4. Chạy một câu lệnh SQL rồi thoát ngay (`-c`)
Thường dùng trong CI/CD, script bash hoặc automation:
```bash
psql -U postgres -d mydb -c "SELECT count(*) FROM orders;"
```

---

## 🎯 4. Tóm Tắt Quy Trình Làm Việc Điển Hình

```mermaid
sequenceDiagram
    autonumber
    participant Dev as Backend Dev
    participant CLI as psql CLI
    participant DB as Postgres Server

    Dev->>CLI: psql -U postgres -d postgres
    CLI->>DB: Kết nối cổng 5432
    DB-->>CLI: Sẵn sàng (postgres=#)
    Dev->>CLI: \l (Xem có những DB nào)
    Dev->>CLI: \c my_app (Chuyển vào DB my_app)
    Dev->>CLI: \dt (Xem danh sách bảng)
    Dev->>CLI: \d users (Xem schema bảng users)
    Dev->>CLI: SELECT * FROM users LIMIT 5;
    Dev->>CLI: \q (Thoát)
```
