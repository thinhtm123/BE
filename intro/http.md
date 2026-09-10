# 🌐 HTTP & HTTPS (Hypertext Transfer Protocol)

> 💡 **What is HTTP?**  
> **HTTP (Hypertext Transfer Protocol)** transmits hypertext over the web using a **request-response model**. It defines **message formatting** and standardizes **server-browser communication**. It is a **stateless protocol** where each request is independent. HTTP forms the foundation of web communication, often used with **HTTPS** for encryption.

---

## 1. Bản Chất & Đặc Tính Cốt Lõi Của HTTP

1. **Truyền tải siêu văn bản (Hypertext Transmission):** Vận chuyển các tài liệu HTML, CSS, JavaScript, hình ảnh, video và dữ liệu API (JSON, XML) giữa Client và Server.
2. **Mô hình Request - Response:** Client (trình duyệt, mobile app) chủ động gửi một yêu cầu (Request), Server tiếp nhận xử lý và trả về kết quả (Response).
3. **Giao thức phi trạng thái (Stateless Protocol):**
   - Mỗi request được thực thi độc lập, Server không tự động ghi nhớ thông tin hay lịch sử của request trước.
   - Để nhận diện người dùng đăng nhập hay lưu giỏ hàng, ứng dụng Web sử dụng các giải pháp bổ trợ: **Cookies**, **Session**, hoặc **JWT (JSON Web Token)**.

---

## 2. Cấu Trúc Thông Điệp HTTP (Message Formatting)

### 2.1. HTTP Request
```http
GET /api/v1/users/10 HTTP/1.1
Host: api.example.com
User-Agent: Mozilla/5.0
Accept: application/json
Authorization: Bearer eyJhbGci...

(Dòng trống)
[Body gửi lên (nếu dùng POST/PUT/PATCH)]
```

### 2.2. HTTP Response
```http
HTTP/1.1 200 OK
Content-Type: application/json; charset=UTF-8
Content-Length: 53

{
  "id": 10,
  "name": "Alex Nguyen",
  "role": "Backend Engineer"
}
```

---

## 3. Các HTTP Methods & Status Codes Cần Nhớ

### 3.1. Các HTTP Methods phổ biến:
- **GET:** Lấy dữ liệu từ server (Safe & Idempotent).
- **POST:** Tạo mới một tài nguyên trên server.
- **PUT:** Cập nhật thay thế toàn bộ tài nguyên (Idempotent).
- **PATCH:** Cập nhật một phần thuộc tính của tài nguyên.
- **DELETE:** Xóa tài nguyên (Idempotent).

### 3.2. Phân loại mã trạng thái HTTP (Status Codes):
- **1xx (Informational):** Đang xử lý yêu cầu.
- **2xx (Success):** Thành công (`200 OK`, `201 Created`, `204 No Content`).
- **3xx (Redirection):** Chuyển hướng (`301 Moved Permanently`, `302 Found`, `304 Not Modified`).
- **4xx (Client Error):** Lỗi từ phía Client (`400 Bad Request`, `401 Unauthorized`, `403 Forbidden`, `404 Not Found`).
- **5xx (Server Error):** Lỗi từ phía Server (`500 Internal Server Error`, `502 Bad Gateway`, `503 Service Unavailable`, `504 Gateway Timeout`).

---

## 4. Sự Khác Biệt Giữa HTTP và HTTPS

| Tiêu Chí | HTTP (Cổng mặc định: 80) | HTTPS (Cổng mặc định: 443) |
| :--- | :--- | :--- |
| **Bảo mật** | Truyền dữ liệu dạng **Plain Text** (chữ thô không mã hóa). | Dữ liệu được mã hóa bằng **SSL/TLS**. |
| **Rủi ro** | Dễ bị nghe lén (Eavesdropping), giả mạo dữ liệu (Man-in-the-Middle). | An toàn tuyệt đối: Bảo vệ mật khẩu, thẻ tín dụng, thông tin cá nhân. |
| **Xác thực** | Không xác thực được server thật hay giả. | Có chứng chỉ số (**SSL Certificate**) được ký bởi tổ chức uy tín (CA). |
| **Tác động SEO** | Bị trình duyệt cảnh báo "Not Secure", xếp hạng tìm kiếm thấp. | Chuẩn mực bắt buộc cho mọi website hiện đại, ưu tiên xếp hạng SEO. |

---

## 5. Tóm Tắt Nhanh (Key Takeaway)

- **HTTP:** Ngôn ngữ giao tiếp nền tảng của Web dựa trên mô hình Request - Response.
- **Stateless:** Không lưu trạng thái giữa các request $\rightarrow$ Cần Cookies/Tokens để duy trì phiên đăng nhập.
- **HTTPS:** HTTP + Lớp mã hóa SSL/TLS để bảo mật dữ liệu trên đường truyền.
