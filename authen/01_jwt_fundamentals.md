# 🎫 JWT (JSON Web Token) - Cơ Chế, Cấu Trúc & Thực Tế Triển Khai

**JWT (JSON Web Token)** (đọc là *"jot"*) là một chuẩn mở công nghiệp (**RFC 7519**) định nghĩa cách truyền tải thông tin an toàn giữa các bên dưới dạng một đối tượng JSON nhỏ gọn và tự chứa (**compact & self-contained**).

Trong kỷ nguyên Web hiện đại và kiến trúc **Microservices**, JWT đã gần như thay thế hoàn toàn cơ chế Session-Cookie truyền thống để trở thành "tiêu chuẩn vàng" cho xác thực (**Authentication**) và phân quyền (**Authorization**).

---

## 🔍 1. Giải Phẫu Cấu Trúc JWT (3 Phần Phân Tách Bằng Dấu Chấm `.`)

Một chuỗi JWT trên thực tế trông như thế này:

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTYiLCJuYW1lIjoiTmd1eWVuIFZhbiBBIiwicm9sZSI6IkFETUlOIiwiZXhwIjoxNzg5NTY5MzAwfQ.s8_pW6h8p29X9l3N5p9D2hG5Z_example_Signature
```

Nhìn thì có vẻ "bí ẩn", nhưng nó được ghép lại từ 3 phần bằng 2 dấu chấm (`.`):

$$\text{JWT} = \underbrace{\text{Header}}_{\text{Đỏ}} \text{ . } \underbrace{\text{Payload}}_{\text{Tím}} \text{ . } \underbrace{\text{Signature}}_{\text{Xanh}}$$

```mermaid
graph LR
    A["Header<br/>(Thuật toán & Loại token)"] -.->|Base64Url| D["Part 1"]
    B["Payload<br/>(Dữ liệu / Claims)"] -.->|Base64Url| E["Part 2"]
    C["Signature<br/>(Chữ ký chống giả mạo)"] -.->|Băm với Secret Key| F["Part 3"]
    D --> JWT["JWT Token: Part1 . Part2 . Part3"]
    E --> JWT
    F --> JWT
```

---

### Phần 1: Header (Tiêu Đề)
Chứa siêu dữ liệu (metadata) về chính cái token đó:
```json
{
  "alg": "HS256", // Thuật toán ký: HMAC SHA-256 (hoặc RS256)
  "typ": "JWT"    // Loại token
}
```
👉 Phần này được mã hóa bằng **Base64Url** để tạo thành đoạn chuỗi thứ nhất.

---

### Phần 2: Payload (Dữ Liệu - Claims)
Chứa các thông tin thực tế mà bạn muốn truyền đi (gọi là các **Claims**). Có 3 loại claims:

```json
{
  // 1. Registered Claims (Các trường chuẩn hóa theo RFC 7519)
  "sub": "123456",      // Subject: ID của người dùng
  "iss": "auth.mycompany.com", // Issuer: bên cấp phát token
  "iat": 1710000000,    // Issued At: thời điểm cấp token (timestamp)
  "exp": 1710003600,    // Expiration Time: thời điểm hết hạn (timestamp)

  // 2. Custom / Private Claims (Dữ liệu bạn tự định nghĩa)
  "email": "user@gmail.com",
  "role": "ADMIN",
  "tenant_id": "corp_xyz"
}
```
👉 Phần này cũng chỉ được mã hóa bằng **Base64Url** để tạo thành đoạn chuỗi thứ hai.

---

### Phần 3: Signature (Chữ Ký Số - Trái Tim Của JWT)
Đây là phần đảm bảo token **không bị làm giả hay chỉnh sửa** trên đường truyền.

Server lấy chuỗi `Base64Url(Header)` và `Base64Url(Payload)`, nối lại bằng dấu chấm `.`, rồi băm nó cùng với một **Secret Key (khóa bí mật)** mà chỉ Server biết:

```text
Signature = HMACSHA256(
    base64UrlEncode(Header) + "." + base64UrlEncode(Payload),
    YOUR_SECRET_KEY
)
```

#### 🛡️ Cơ chế chống giả mạo hoạt động thế nào?
1. Hacker bắt được token, giải mã Payload và sửa `"role": "USER"` thành `"role": "ADMIN"`.
2. Hacker gửi token đã sửa lên server.
3. Server nhận token, bóc Header và Payload đã sửa ra, chạy lại hàm băm với `YOUR_SECRET_KEY`.
4. Kết quả băm mới **không khớp** với chuỗi Signature đính kèm ở đuôi token.
5. 👉 **Server từ chối ngay lập tức (HTTP 401 Unauthorized)!**

---

## ⚠️ 2. HIỂU LẦM TAI HẠI NHẤT VỀ JWT

> 🛑 **JWT KHÔNG HỀ ĐƯỢC MÃ HÓA (ENCRYPT)! NÓ CHỈ ĐƯỢC KÝ SỐ (SIGN)!**

- `Base64Url` không phải là mã hóa bảo mật, nó chỉ là một cách biến đổi byte thành chuỗi ký tự đọc được trên URL/Header.
- **Bất kỳ ai** cầm token này (bằng cách paste vào web `jwt.io` hoặc dùng hàm `atob()`) đều **đọc được 100% nội dung Payload**!
- ❌ **TUYỆT ĐỐI KHÔNG LƯU**: Mật khẩu, số thẻ tín dụng, OTP, số CCCD/CMND vào Payload.
- ✅ **CHỈ LƯU**: User ID, username, email, roles/permissions, thời gian hết hạn (`exp`).

---

## ⚖️ 3. So Sánh: Session-Cookie vs. JWT (Stateful vs. Stateless)

Tại sao ngày nay các hệ thống lớn đều chuộng JWT?

```mermaid
sequenceDiagram
    autonumber
    actor Client
    participant Server 1
    participant Server 2
    participant Database/Redis

    Note over Client,Database/Redis: MÔ HÌNH SESSION-COOKIE (Stateful)
    Client->>Server 1: Gửi SessionID: "xyz123"
    Server 1->>Database/Redis: Query tìm SessionID "xyz123"?
    Database/Redis-->>Server 1: Trả về {userId: 1, role: "ADMIN"}
    Server 1-->>Client: Trả về kết quả

    Note over Client,Database/Redis: MÔ HÌNH JWT (Stateless - Tự Chứa)
    Client->>Server 2: Gửi Authorization: Bearer <JWT>
    Note over Server 2: Server 2 chỉ cần dùng Secret Key<br/>kiểm tra chữ ký (Signature).<br/>KHÔNG CẦN GỌI DATABASE HAY REDIS!
    Server 2-->>Client: Trả về kết quả
```

| Tiêu chí | Session-Cookie (Stateful) | JWT (Stateless) |
|---|---|---|
| **Lưu trữ trạng thái** | Server/Redis phải lưu session của từng user. | Server không cần lưu gì cả, thông tin nằm hết trong Token. |
| **Mở rộng (Scaling)** | Khó scale ngang. Phải dùng Sticky Session hoặc cụm Redis tập trung lưu session. | **Cực dễ scale ngang.** Cụm 100 server backend chỉ cần chia sẻ chung 1 Secret Key là verify được. |
| **Phù hợp Mobile App** | Kém (Mobile app quản lý Cookie rất vất vả). | **Cực kỳ phù hợp** (Mobile chỉ cần nhét vào Header `Authorization: Bearer <token>`). |
| **Tốc độ xác thực** | Chậm hơn vì mỗi request đều phải query DB/Redis check session. | Nhanh hơn vì chỉ cần tính toán thuật toán băm (CPU bound) trong memory. |

---

## ⚡ 4. Điểm Yếu Chí Mạng Của JWT & Cách Giải Quyết

Ưu điểm lớn nhất của JWT (Stateless - không lưu trạng thái) cũng chính là **điểm yếu nguy hiểm nhất** của nó:

### Vấn đề: "Làm sao thu hồi token trước hạn? (Token Revocation / Logout)"
- Với Session: User bấm Đăng xuất -> Server xóa session trong Redis -> User bị văng ra ngay.
- Với JWT: Token đã cấp ra với hạn 24 giờ. Nếu user bấm Logout hoặc bị hack mất token, **token đó vẫn có hiệu lực suốt 24 giờ**, Backend không có cách nào tự động hủy nó nếu không lưu trạng thái!

---

### 🛡️ Giải Pháp Chuẩn Công Nghiệp: Cặp đôi Access Token + Refresh Token

Thay vì cấp 1 token dùng mãi, ta chia làm 2 token:

```mermaid
sequenceDiagram
    autonumber
    actor Client
    participant Server
    participant Redis/DB

    Client->>Server: Login (username + password)
    Server-->>Client: Trả về cặp Token:<br/>1. Access Token (sống 15 phút)<br/>2. Refresh Token (sống 7 ngày, lưu vào DB/Redis)

    Note over Client,Server: Client dùng Access Token gọi API bình thường
    Client->>Server: GET /orders (Bearer AccessToken)
    Server-->>Client: 200 OK

    Note over Client,Server: Sau 15 phút, Access Token hết hạn!
    Client->>Server: GET /orders (Bearer AccessToken hết hạn)
    Server-->>Client: 401 Unauthorized (Token Expired)

    Note over Client,Server: Client âm thầm dùng Refresh Token để xin cấp lại
    Client->>Server: POST /auth/refresh (Gửi Refresh Token)
    Server->>Redis/DB: Kiểm tra Refresh Token có hợp lệ & chưa bị thu hồi?
    Redis/DB-->>Server: Hợp lệ
    Server-->>Client: Cấp Access Token MỚI (15 phút)

    Note over Client,Server: Nếu User bấm Logout:
    Client->>Server: POST /auth/logout
    Server->>Redis/DB: XÓA Refresh Token khỏi DB!
```

1. **Access Token (Ngắn hạn - 5 đến 15 phút)**:
   - Dùng để gửi kèm trong Header `Authorization: Bearer <token>` mỗi khi gọi API.
   - Nếu bị lộ, hacker chỉ dùng được trong vài phút rồi vô tác dụng.

2. **Refresh Token (Dài hạn - 7 ngày đến 30 ngày)**:
   - Được lưu an toàn ở Client (ví dụ `HttpOnly Cookie` chống bị XSS đọc trộm).
   - Server **có lưu Refresh Token vào Database hoặc Redis**.
   - Khi Access Token hết hạn, Client tự động gửi Refresh Token lên để đổi lấy Access Token mới mà người dùng không bị văng ra đăng nhập lại.
   - Khi User bấm **Đăng xuất** hoặc **Đổi mật khẩu**, Server chỉ cần **xóa Refresh Token trong Database** là xong!

---

## 🔑 5. Hai Thuật Toán Ký Phổ Biến: HS256 vs. RS256

| Thuật toán | Loại khóa | Cơ chế | Khi nào dùng? |
|---|---|---|---|
| **HS256** (HMAC + SHA256) | **Đối xứng** (Symmetric) | Dùng chung **1 Secret Key duy nhất** cho cả việc Ký (Sign) và Kiểm tra (Verify). | Ứng dụng Monolith (đơn khối) hoặc team nhỏ. |
| **RS256** (RSA + SHA256) | **Bất đối xứng** (Asymmetric) | Dùng **Private Key** để Ký (chỉ Auth Server giữ) và **Public Key** để Verify (chia sẻ rộng rãi cho mọi service). | **Microservices**, OAuth2, SSO (Single Sign-On). |

*Trong Microservices:*
- Service `Auth Service` giữ Private Key bí mật để sinh ra JWT khi user đăng nhập.
- Các service vệ tinh khác (`Order Service`, `Payment Service`, `Shipping Service`) chỉ cần cầm Public Key để kiểm tra tính hợp lệ của token mà **không có quyền giả mạo chữ ký**!
