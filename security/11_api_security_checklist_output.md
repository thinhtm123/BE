# 📤 API Security Checklist: Output (Bảo Vệ Dữ Liệu Phản Hồi)

Tài liệu này chi tiết hóa toàn bộ 7 tiêu chuẩn an ninh thuộc nhóm **"Output" (Kiểm soát dữ liệu và tiêu đề phản hồi của API)** trong bộ tiêu chuẩn **API Security Checklist** quốc tế:

```
┌────────────────────────────────────────────────────────────────────────┐
│                        API SECURITY: OUTPUT                            │
├────────────────────────────────────────────────────────────────────────┤
│ [ ] 1. Send X-Content-Type-Options: nosniff header                     │
│ [ ] 2. Send X-Frame-Options: deny header                               │
│ [ ] 3. Send Content-Security-Policy: default-src 'none' header         │
│ [ ] 4. Remove fingerprinting headers (e.g. x-powered-by)               │
│ [ ] 5. Force content-type for your response                            │
│ [ ] 6. Avoid returning sensitive data (credentials, tokens, etc)       │
│ [ ] 7. Return proper response codes as per the operation               │
└────────────────────────────────────────────────────────────────────────┘
```

Nhiều lập trình viên lầm tưởng: *"API chỉ trả về JSON thuần thì làm gì có giao diện mà cần bảo vệ?"*.  
Thực tế, dữ liệu phản hồi (Response) nếu không được kiểm soát chặt chẽ sẽ là **mảnh đất màu mỡ cho tấn công XSS, Clickjacking, rò rỉ công nghệ và lộ thông tin nhạy cảm**.

---

## 🚫 1. Send `X-Content-Type-Options: nosniff` Header

### ❓ Vấn đề: Cơ chế "Đoán mò định dạng" (MIME Sniffing)
Một số trình duyệt khi nhận phản hồi từ server thường có tính năng tự động "đoán" xem nội dung thực sự là gì thay vì tin vào header `Content-Type`.
- **Kịch bản tấn công**: 
  1. Người dùng gửi dữ liệu bình luận chứa mã JavaScript: `<script>alert(document.cookie)</script>`.
  2. API lưu vào DB và trả về nguyên văn trong response JSON.
  3. Nếu trình duyệt thực hiện MIME Sniffing, nó có thể nhầm lẫn JSON này là mã HTML/JS và **tự động thực thi đoạn mã độc ngay trên trình duyệt nạn nhân** (Reflected / Stored XSS)!

### 🛡️ Giải pháp kỹ thuật:
Server bắt buộc gửi kèm header:
```http
X-Content-Type-Options: nosniff
```
👉 Ra lệnh nghiêm ngặt cho trình duyệt: *"Nội dung tôi khai báo là `application/json` thì chỉ được đối xử như dữ liệu JSON, cấm tuyệt đối việc đoán mò và cấm thực thi như mã lệnh!"*.

---

## 🖼️ 2. Send `X-Frame-Options: deny` Header

### ❓ Vấn đề: Tấn công Clickjacking (Bấm chuột tặc)
Hacker tạo một trang web độc hại và nhúng API endpoint của bạn vào trong một thẻ `<iframe src="https://api.mycompany.com/...">` trong suốt.

### 🛡️ Giải pháp kỹ thuật:
Đối với một hệ thống API thuần túy, **hoàn toàn không có bất kỳ lý do gì để các endpoint JSON được phép nhúng vào thẻ iframe**!
```http
X-Frame-Options: DENY
```
Header này chặn đứng 100% mọi nỗ lực nhúng API của bạn vào bất kỳ thẻ `<iframe>` nào trên Internet.

---

## 🛑 3. Send `Content-Security-Policy: default-src 'none'` Header

### ❓ Tại sao API lại dùng `default-src 'none'`?
Ở bài học trước về CSP cho website giao diện, ta phải cấu hình phức tạp: cho phép tải script từ Google Analytics, cho phép ảnh từ Cloudinary...  
Nhưng đối với **REST API thuần túy**:
- API **chỉ trả về dữ liệu thô (JSON/XML)**.
- API **không bao giờ phục vụ file HTML, file CSS, file JavaScript, Font chữ hay Ảnh**!

### 🛡️ Giải pháp kỹ thuật:
Đặt chính sách CSP ở mức **tối đa nghiêm ngặt nhất**:
```http
Content-Security-Policy: default-src 'none'; frame-ancestors 'none';
```
👉 Nếu ai đó (hoặc trình duyệt) mở trực tiếp một link API trên thanh địa chỉ, header này đảm bảo rằng **ngay cả khi trong JSON có lẫn mã độc HTML/JS, trình duyệt cũng bị tước hoàn toàn quyền tải hay thực thi bất kỳ script/style nào**.

---

## 🕵️ 4. Remove Fingerprinting Headers (Xóa Dấu Vết Công Nghệ: `X-Powered-By`)

### ❓ Vấn đề: "Vạch áo cho người xem lưng"
Rất nhiều web framework và server mặc định tự động đính kèm các header "khoe mẽ" công nghệ:

```http
Server: Apache/2.4.41 (Ubuntu)
X-Powered-By: Express
X-Powered-By: PHP/8.1.0
X-AspNet-Version: 4.0.30319
```

Hacker chỉ cần gửi 1 request là biết chính xác:
- Bạn đang dùng Express.js, PHP 8.1 hay ASP.NET.
- Server đang chạy Apache phiên bản mấy trên hệ điều hành Ubuntu nào.
👉 Chúng sẽ vào ngay kho dữ liệu **CVE (Common Vulnerabilities and Exposures)** tìm các lỗ hổng đã biết của đúng phiên bản đó để tấn công bạn!

### 🛡️ Giải pháp: Tắt sạch các header này trên Server

#### 1. Trong Node.js / Express:
```javascript
const app = express();
app.disable('x-powered-by'); // Tắt header X-Powered-By
```

#### 2. Trong Nginx (`nginx.conf`):
```nginx
# Ẩn thông tin phiên bản Nginx:
server_tokens off;

# Xóa header X-Powered-By từ backend chuyển tiếp ra:
proxy_hide_header X-Powered-By;
```

#### 3. Trong Spring Boot (`application.yml`):
```yaml
server:
  server-header: "" # Không gửi header Server: Apache-Coyote
```

---

## 🎯 5. Force Content-Type For Your Response

### ❓ Vấn đề:
Nếu server trả về dữ liệu mà quên đặt header `Content-Type` hoặc để mặc định là `text/html`, trình duyệt sẽ cố tình render dữ liệu đó như một trang web, tạo cơ hội cho mã độc JavaScript kích hoạt.

### 🛡️ Giải pháp:
Luôn luôn xác định rõ ràng và ép cứng định dạng kèm bảng mã ký tự chuẩn:
```http
Content-Type: application/json; charset=utf-8
```
*(Đối với các response lỗi chuẩn RFC 7807, dùng: `Content-Type: application/problem+json; charset=utf-8`)*.

---

## 🙈 6. Avoid Returning Sensitive Data (Tránh Trả Về Dữ Liệu Nhạy Cảm)

### ❓ Sai lầm phổ biến:
Nhiều lập trình viên dùng câu lệnh ORM: `userRepository.findById(id)` rồi trả thẳng Entity ra ngoài Controller.

```json
// ❌ CỰC KỲ NGUY HIỂM: Response API lộ sạch thông tin mật trong Database!
{
  "id": 105,
  "username": "nguyenvana",
  "password": "$2a$12$e8... (Chuỗi băm bcrypt)",
  "passwordResetToken": "9f8b2a1c...",
  "ssn": "001200001234",
  "creditCardNumber": "4111222233334444",
  "internalAdminNotes": "Khách hàng này từng gian lận voucher"
}
```

### 🛡️ Giải pháp kỹ thuật:
1. **Dùng Response DTO**: Chỉ copy đúng những trường mà giao diện cần hiển thị (`id`, `username`, `avatarUrl`).
2. **Annotation che giấu trong Entity (Jackson / Java)**:
   ```java
   public class User {
       // Bắt buộc ẩn khi serialize sang JSON trả về cho client:
       @JsonIgnore
       private String password;

       @JsonIgnore
       private String passwordResetToken;
   }
   ```
3. **Mặt nạ dữ liệu nhạy cảm**: Số điện thoại, email, số thẻ ngân hàng phải được làm mờ (`**** **** **** 1234`).

---

## 🚦 7. Return Proper Response Codes As Per The Operation

### ❌ Anti-pattern kinh điển: "Mọi thứ đều là 200 OK"
Một số hệ thống thiết kế cẩu thả: Bất kể thành công, sai mật khẩu hay sập database, server đều trả về HTTP Status `200 OK`:

```json
// ❌ PHẢN PATTERN (KHÔNG DÙNG):
HTTP/1.1 200 OK
{
  "success": false,
  "errorCode": 401,
  "message": "Token hết hạn"
}
```
**Hậu quả**:
- Tường lửa (WAF), API Gateway và công cụ giám sát (Datadog, Prometheus) tưởng hệ thống chạy tốt 100% (vì thấy toàn status 200).
- Không kích hoạt được các cơ chế retry tự động hoặc cảnh báo sự cố khi server gặp lỗi 5xx.

### ✅ Chuẩn Semantics HTTP RESTful:

| Mã HTTP Status | Tên gọi chuẩn | Khi nào sử dụng? |
|---|---|---|
| **`200 OK`** | Success | Đọc dữ liệu (GET) hoặc cập nhật (PUT/PATCH) thành công. |
| **`201 Created`** | Created | Tạo mới tài nguyên thành công (POST), kèm header `Location`. |
| **`204 No Content`** | No Content | Xóa tài nguyên thành công (DELETE), không cần trả body. |
| **`400 Bad Request`** | Bad Request | Dữ liệu client gửi lên sai format, thiếu trường bắt buộc. |
| **`401 Unauthorized`** | Unauthorized | **Chưa đăng nhập** hoặc Token gửi lên không hợp lệ/hết hạn. |
| **`403 Forbidden`** | Forbidden | Đã đăng nhập nhưng **không có quyền** truy cập (Role không đủ). |
| **`404 Not Found`** | Not Found | Không tìm thấy ID tài nguyên trong Database. |
| **`422 Unprocessable`**| Unprocessable Entity| Dữ liệu đúng cú pháp nhưng sai logic nghiệp vụ (vd: số dư không đủ).|
| **`429 Too Many Req`** | Too Many Requests | Vượt quá giới hạn Rate Limit. |
| **`500 Internal Error`**| Internal Server Error| Lỗi sập code Backend không mong muốn. |

---

## 🎯 Bảng Đối Chiếu Nhanh 7 Tiêu Chí "Output"

| # | Tiêu chí | Nguy cơ ngăn chặn | Cách triển khai |
|---|---|---|---|
| 1 | **X-Content-Type-Options** | Ngăn trình duyệt đoán mò định dạng (MIME Sniffing) | `X-Content-Type-Options: nosniff` |
| 2 | **X-Frame-Options: deny** | Chống nhúng API vào iframe (Clickjacking) | `X-Frame-Options: DENY` |
| 3 | **CSP: default-src 'none'** | Cấm mọi thực thi script/media trên response API | `Content-Security-Policy: default-src 'none'` |
| 4 | **Remove fingerprinting headers**| Ẩn phiên bản hệ điều hành & framework (Express/PHP/Apache)| Tắt `X-Powered-By`, `server_tokens off` |
| 5 | **Force content-type** | Ngăn chặn sai lệch bảng mã ký tự và định dạng | `Content-Type: application/json; charset=utf-8` |
| 6 | **Avoid returning sensitive data**| Chống lộ mật khẩu hash, token, CCCD trong JSON | Dùng Response DTO, `@JsonIgnore` |
| 7 | **Proper response codes** | Giúp WAF/Gateway/Monitoring nhận biết đúng trạng thái | Dùng đúng chuẩn HTTP: 200, 201, 204, 400, 401, 403, 404, 429, 500 |
