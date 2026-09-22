# 🔒 SSL Settings in PostgreSQL (Mã Hóa Đường Truyền Client - Server)

Trong môi trường thực tế, dữ liệu truyền qua mạng giữa ứng dụng Backend (hoặc DBeaver) và máy chủ PostgreSQL có thể bị tin tặc nghe lén hoặc can thiệp (**Man-in-the-Middle - MITM**). **SSL/TLS (Secure Sockets Layer / Transport Layer Security)** là lớp bảo mật bắt buộc giúp mã hóa toàn bộ luồng dữ liệu trên đường truyền.

---

## 🎯 1. Vì Sao SSL Là Bắt Buộc Khi Lên Production?

Nếu **KHÔNG có SSL (Plain Text)**:
- Mọi câu lệnh SQL bạn gửi đi, mật khẩu tài khoản và dữ liệu nhạy cảm (số thẻ ngân hàng, số điện thoại, mật khẩu khách hàng) đều chạy trần trụi dưới dạng văn bản rõ trên mạng Internet.
- Bất kỳ ai kiểm soát router WiFi hoặc hạ tầng mạng đều có thể đọc trọn vẹn dữ liệu đó.

Khi **CÓ SSL (Encrypted Channel)**:
- Toàn bộ gói tin được bọc trong một "đường hầm mã hóa" (Crypto Tunnel). Kẻ xấu dù có bắt được gói tin cũng chỉ thấy chuỗi ký tự rác vô nghĩa.

---

## ⚙️ 2. Cấu Hình SSL Ở Phía Server (PostgreSQL)

Để máy chủ PostgreSQL chấp nhận kết nối mã hóa, bạn cần chuẩn bị 2 file chứng chỉ số:
- **`server.crt`**: Chứng chỉ công khai (Certificate).
- **`server.key`**: Khóa bí mật của server (Private Key - phải bảo vệ nghiêm ngặt, cấp quyền `chmod 600`).

### Bật SSL trong `postgresql.conf`:
```ini
# Bật tính năng SSL
ssl = on

# Chỉ định đường dẫn tới 2 file chứng chỉ
ssl_cert_file = 'server.crt'
ssl_key_file = 'server.key'

# (Tùy chọn nâng cao) Chỉ định file CA nếu yêu cầu xác thực 2 chiều (mTLS):
# ssl_ca_file = 'root.crt'
```

### Bắt buộc client phải dùng SSL trong `pg_hba.conf`:
Thay vì dùng chữ `host` (cho phép cả có SSL lẫn không SSL), ta đổi thành **`hostssl`**:
```text
# TYPE      DATABASE   USER   ADDRESS          METHOD
# Chỉ chấp nhận kết nối ĐÃ ĐƯỢC MÃ HÓA SSL, từ chối mọi kết nối không có SSL:
hostssl     all        all    0.0.0.0/0        scram-sha-256
```

---

## 🛡️ 3. Phía Client & Backend: Các Chế Độ `sslmode` (Cực Kỳ Quan Trọng)

Khi Backend (Spring Boot, Node.js) hoặc DBeaver kết nối vào database, bạn sẽ truyền tham số **`sslmode`** vào chuỗi Connection String:

```text
postgresql://user:pass@db-host:5432/mydb?sslmode=require
```

### Bảng Giải Mã 6 Cấp Độ `sslmode`:

| Chế độ (`sslmode`) | Mức độ an toàn | Cơ chế hoạt động | Khuyên dùng khi nào? |
| :--- | :--- | :--- | :--- |
| **`disable`** | ❌ Không an toàn | Hoàn toàn không dùng SSL. | Chỉ dùng khi dev local trên cùng 1 máy. |
| **`allow`** | ⚠️ Rất yếu | Thử không dùng SSL trước, nếu server bắt buộc thì mới dùng. | Không nên dùng. |
| **`prefer`** *(Mặc định)*| ⚠️ Trung bình | Thử dùng SSL trước, nếu server không có SSL thì tự động tụt xuống không mã hóa. | Môi trường test/staging. |
| **`require`** | 🔒 Tốt | **Bắt buộc phải có SSL**, nếu server không có SSL thì từ chối kết nối ngay. Không kiểm tra chứng chỉ giả mạo. | Phổ biến nhất khi cắm app lên Cloud (AWS RDS, Supabase, Neon). |
| **`verify-ca`** | 🛡️ Rất cao | Bắt buộc SSL + **Kiểm tra chứng chỉ server** phải do cơ quan uy tín (CA) cấp. | Chống giả mạo server. |
| **`verify-full`** | 🏰 Tối đa | Bắt buộc SSL + Kiểm tra CA + **Kiểm tra đúng tên miền Hostname** của server. | Chuẩn bảo mật khắt khe của Ngân hàng & Tài chính. |

---

## 🔌 4. Cách Bật SSL Trong DBeaver

Khi bạn kết nối vào các dịch vụ Cloud như **Supabase, Neon, AWS RDS**:
1. Trong cửa sổ cấu hình kết nối DBeaver $\to$ Chuyển sang tab **SSL**.
2. Tích chọn **Use SSL**.
3. Ở ô **SSL mode**, chọn **`require`** (hoặc `verify-full` nếu tải file chứng chỉ `root.crt` về).
4. Bấm **Test Connection** là đường truyền lập tức được mã hóa an toàn!

---

## 🎯 Tóm Tắt 1 Dòng Để Ghi Nhớ:

> *"SSL bảo vệ đường truyền giữa Backend và Database. Trên server ta cấu hình `ssl = on` và dùng `hostssl` trong `pg_hba.conf`; còn ở chuỗi kết nối Backend luôn đảm bảo có đuôi `?sslmode=require`."*
