# 🔑 HTTP Basic Authentication (Cơ Bản Nhưng Vẫn Rất Thực Dụng)

**HTTP Basic Authentication** (thường gọi tắt là **Basic Auth**) là cơ chế xác thực lâu đời nhất, đơn giản nhất và được tích hợp sẵn trực tiếp vào chuẩn của giao thức HTTP (**RFC 7617**).

Không cần cơ sở dữ liệu session phức tạp, không cần thư viện JWT cao siêu — chỉ với vài dòng cấu hình header HTTP, bạn đã có một lớp bảo vệ cho hệ thống của mình.

---

## ⚙️ 1. Cơ Chế Hoạt Động Của Basic Auth

Toàn bộ quá trình diễn ra qua cơ chế "Thách thức & Phản hồi" (**Challenge & Response**):

```mermaid
sequenceDiagram
    autonumber
    actor Client as Trình Duyệt / Postman
    participant Server as Web Server (Nginx / Backend)

    Client->>Server: 1. GET /admin (Không gửi thông tin xác thực)
    Server-->>Client: 2. 401 Unauthorized<br/>Header: WWW-Authenticate: Basic realm="Khu vuc quan tri"
    
    Note over Client: Trình duyệt tự động bật popup nguyên bản:<br/>"Yêu cầu nhập Username & Password"
    
    Client->>Server: 3. GET /admin<br/>Header: Authorization: Basic dXNlcjpwYXNzMTIz
    Server-->>Client: 4. 200 OK (Giải mã Base64 thấy đúng user/pass -> Trả dữ liệu)
```

---

## 🔬 2. Header `Authorization: Basic` Được Tạo Ra Như Thế Nào?

Quy tắc tạo chuỗi Basic Auth cực kỳ đơn giản:

1. Lấy `username` và `password`, nối với nhau bằng dấu hai chấm `:`:
   $$\text{chuoi} = \text{"admin"} + \text{":"} + \text{"secret123"} = \text{"admin:secret123"}$$
2. Mã hóa chuỗi trên bằng **Base64**:
   $$\text{Base64("admin:secret123")} \longrightarrow \text{"YWRtaW46c2VjcmV0MTIz"}$$
3. Đính vào header HTTP gửi đi:
   ```http
   Authorization: Basic YWRtaW46c2VjcmV0MTIz
   ```

---

## 🛑 3. Cảnh Báo An Toàn: Lỗ Hổng "Chết Người"

> ⚠️ **BASE64 KHÔNG PHẢI LÀ MÃ HÓA (ENCRYPTION)! NÓ CHỈ LÀ ENCODING!**

Bất kỳ ai bắt được gói tin cũng chỉ mất đúng 1 giây để dịch ngược `YWRtaW46c2VjcmV0MTIz` ra thẳng **`admin:secret123`**.

```
    Khách hàng                             Hacker đứng giữa                     Server
        │                                         │                                │
        ├───── Authorization: Basic YWRtaW46... ──┼──> Bắt gói tin:                │
        │                                         │    "Aha! admin:secret123"      │
        │                                         │                                │
```

👉 **QUY TẮC SỐNG CÒN**: 
- **Tuyệt đối KHÔNG BAO GIỜ dùng Basic Auth trên giao thức HTTP thường!**
- **Bắt buộc phải đi kèm với HTTPS (TLS/SSL)**: Lúc này toàn bộ HTTP Header (bao gồm cả chuỗi Basic Auth) sẽ được đường hầm HTTPS mã hóa bảo vệ, hacker không thể nhìn trộm được.

---

## ⚖️ 4. Ưu Điểm & Nhược Điểm

### ✅ Ưu điểm:
1. **Cực kỳ đơn giản**: Không cần cài thêm thư viện phức tạp, ngôn ngữ nào cũng hỗ trợ (Java, Python, PHP, Go, Node.js).
2. **Hỗ trợ mặc định bởi trình duyệt**: Không cần viết giao diện form login HTML/CSS, trình duyệt tự bật một popup hệ thống hỏi tài khoản/mật khẩu.
3. **Stateless**: Server không cần lưu session vào Redis hay RAM.

### ❌ Nhược điểm:
1. **Gửi mật khẩu ở MỌI request**: Mỗi một lần click, một API call đều gửi lại nguyên văn mật khẩu (dù đã Base64) qua mạng.
2. **Không có cơ chế Đăng xuất (Logout) chuẩn mực**: Trình duyệt sẽ âm thầm lưu username/password trong bộ nhớ đệm và tự động gửi kèm ở mọi request tiếp theo cho đến khi bạn đóng hoàn toàn trình duyệt!
3. **Không có phân quyền chi tiết (Scope)**: Chỉ có 2 trạng thái: hoặc là đúng pass (vào được hết), hoặc là sai (bị chặn).
4. **Không hỗ trợ 2FA / OTP**: Không có cách nào chèn thêm bước nhập mã OTP từ điện thoại vào popup mặc định của HTTP.

---

## 🎯 5. Trong Thực Tế Khi Nào Nên Dùng Basic Auth?

Mặc dù không dùng cho ứng dụng người dùng cuối (B2C), Basic Auth vẫn cực kỳ phổ biến trong các tình huống kỹ thuật sau:

1. **Khóa môi trường Staging / Dev (Nginx HTTP Basic Auth)**:
   - Khi bạn dựng một web demo cho sếp hoặc khách hàng xem trước ngày ra mắt, cấu hình 3 dòng Nginx với file `.htpasswd` là khóa sạch mọi ánh nhìn từ người ngoài hoặc bot Google dò tìm.
2. **Webhooks nội bộ**:
   - Giao tiếp giữa 2 hệ thống server nội bộ đơn giản (ví dụ: Jenkins gọi webhook sang server build tự động).
3. **Quản trị thiết bị mạng**:
   - Trang quản trị Modem Wi-Fi gia đình, Router MikroTik, Switch mạng...
4. **Bảo vệ trang Swagger UI / Metrics nội bộ**:
   - Đặt Basic Auth che trước các trang như `/swagger-ui.html` hoặc `/actuator/prometheus` để chỉ đội kỹ thuật mới vào xem được.
