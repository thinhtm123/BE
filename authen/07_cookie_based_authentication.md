# 🍪 Cookie-Based Authentication (Session-Cookie Truyền Thống)

> **Cookie-based Authentication** (hay còn gọi là **Session-based Authentication**) là cơ chế xác thực cổ điển và được kiểm chứng lâu đời nhất trên Web.  
> Do giao thức HTTP vốn là **Stateless (vô trạng thái)**, cơ chế này ra đời nhằm giúp Server và Browser có thể "nhận diện" được nhau xuyên suốt nhiều lượt bấm chuột của người dùng.

---

## 🗄️ 1. Ẩn Dụ Thực Tế: "Tủ Gửi Đồ Siêu Thị"

Hãy tưởng tượng bạn đi siêu thị BigC/Co.opmart:
1. Bạn đến quầy gửi đồ, đưa ba lô của bạn cho nhân viên.
2. Nhân viên cất ba lô vào ô tủ số **105**, khóa lại và đưa cho bạn một chiếc **Thẻ giữ đồ bằng nhựa có khắc số 105** *(đây chính là Session ID lưu trong Cookie)*.
3. Bạn đi mua sắm trong siêu thị. Chiếc thẻ nhựa hoàn toàn vô nghĩa và nhẹ tênh, nó không chứa bất kỳ món đồ nào bên trong.
4. Khi ra về, bạn đưa chiếc thẻ số **105** ra. Nhân viên nhìn số thẻ, tra đúng ngăn tủ 105 và trả lại ba lô cho bạn *(Server tra cứu Session Data)*.
5. Bạn trả lại thẻ, nhân viên mở ngăn tủ cho người khác dùng *(Đăng xuất - Hủy Session)*.

---

## 🔄 2. Luồng Hoạt Động Chi Tiết (Từng Header HTTP)

```mermaid
sequenceDiagram
    autonumber
    actor Browser as 🌐 Trình Duyệt Web
    participant Server as ⚙️ Backend Server
    participant SessionStore as 🗄️ Session Store (RAM / Redis)

    Note over Browser,Server: BƯỚC 1: ĐĂNG NHẬP
    Browser->>Server: POST /login (username + password)
    Server->>Server: Kiểm tra password đúng!
    
    Note over Server,SessionStore: BƯỚC 2: TẠO SESSION TRÊN SERVER
    Server->>SessionStore: Lưu { sessionId: "sess_999", userId: 12, role: "ADMIN" }
    
    Note over Server,Browser: BƯỚC 3: SERVER TRẢ VỀ COOKIE
    Server-->>Browser: 200 OK<br/>Header: Set-Cookie: JSESSIONID=sess_999; HttpOnly; Secure; SameSite=Lax

    Note over Browser,SessionStore: BƯỚC 4: CÁC REQUEST TIẾP THEO (TỰ ĐỘNG ĐÍNH KÈM)
    Browser->>Server: GET /cart<br/>Header: Cookie: JSESSIONID=sess_999 (Trình duyệt TỰ ĐỘNG gửi)
    Server->>SessionStore: Tra cứu: "sess_999" là ai?
    SessionStore-->>Server: Là User ID 12 (ADMIN)
    Server-->>Browser: 200 OK (Trả về giỏ hàng)

    Note over Browser,SessionStore: BƯỚC 5: ĐĂNG XUẤT (LOGOUT)
    Browser->>Server: POST /logout
    Server->>SessionStore: XÓA "sess_999" khỏi bộ nhớ
    Server-->>Browser: Set-Cookie: JSESSIONID=; Max-Age=0 (Ra lệnh trình duyệt xóa cookie)
```

---

## 🛡️ 3. "Bộ Ba Lá Chắn" Bảo Mật Cho Cookie (Bắt Buộc Phải Bật)

Khi Server phát hành Cookie thông qua header `Set-Cookie`, bạn bắt buộc phải cấu hình 3 cờ (flags) quan trọng này:

```http
Set-Cookie: sessionId=sess_999; Path=/; HttpOnly; Secure; SameSite=Lax
```

| Cờ (Flag) | Tác dụng | Ngăn chặn loại tấn công nào? |
|---|---|---|
| **`HttpOnly`** | **Cấm tuyệt đối JavaScript chạm vào Cookie.** Code JS (`document.cookie`) không thể đọc được giá trị này. | 🛡️ **Chống XSS 100%**: Dù hacker chèn được script độc hại vào web, chúng cũng không thể ăn cắp được Session ID! |
| **`Secure`** | Trình duyệt **chỉ gửi Cookie qua đường truyền đã mã hóa HTTPS**, cấm gửi qua HTTP thường. | 🛡️ **Chống Man-in-the-Middle (Sniffing)**: Chống bị hacker nghe lén gói tin trên mạng Wi-Fi công cộng. |
| **`SameSite`** | Kiểm soát xem Cookie có được phép gửi đi khi có một trang web khác kích hoạt request hay không. | 🛡️ **Chống CSRF (Giả mạo yêu cầu)**. |

### Các mức độ của `SameSite`:
- **`SameSite=Strict`**: Cookie chỉ được gửi đi khi người dùng đang ở chính trang web đó. (Bảo mật nhất nhưng click link từ email/Facebook vào sẽ bị coi là chưa đăng nhập).
- **`SameSite=Lax`** *(Mặc định hiện đại)*: Cho phép gửi cookie khi click link chuyển trang (GET), nhưng chặn gửi cookie với các lệnh ngầm (POST/PUT chéo trang).
- **`SameSite=None`**: Gửi cookie trong mọi hoàn cảnh (bắt buộc phải kèm theo cờ `Secure`).

---

## ⚠️ 4. Hai "Nỗi Ám Ảnh" Lớn Nhất Của Cookie-Based Auth

### 1. Cuộc tấn công CSRF (Cross-Site Request Forgery)
Vì trình duyệt có thói quen **tự động đính kèm Cookie** mỗi khi có request gửi tới domain đó:
- Bạn vừa đăng nhập vào ngân hàng `bank.com`.
- Sau đó bạn vô tình bấm vào một link bẫy của hacker: `evil.com`.
- Trang `evil.com` chạy một đoạn code ngầm: `POST bank.com/transfer?to=hacker&amount=100tr`.
- Trình duyệt ngây thơ **tự động đính kèm Cookie của bank.com** vào request đó!
- **Giải pháp**: Phải dùng **Anti-CSRF Token** (như Spring Security CSRF token) hoặc bật cờ `SameSite=Lax/Strict`.

### 2. Khó khăn khi Mở Rộng Server (Session Clustering / Horizontal Scaling)
Nếu bạn có 3 server backend chạy đằng sau Load Balancer:

```
                  ┌──> [ Server 1: Lưu Session của Nam ]
[ Client (Nam) ] ─┼──> [ Server 2: KHÔNG CÓ Session của Nam -> Báo lỗi chưa đăng nhập! ]
                  └──> [ Server 3: KHÔNG CÓ Session của Nam ]
```

- **Giải pháp**: 
  - Hoặc dùng **Sticky Session** (Load balancer luôn điều hướng Nam vào đúng Server 1 - nhưng nếu Server 1 sập là Nam mất session).
  - Hoặc dùng **Redis tập trung (Spring Session Redis)**: Cả 3 server cùng trỏ vào 1 cụm Redis chung để tra cứu session.

---

## ⚔️ 5. So Sánh: Cookie-Based vs. Token-Based

| Tiêu chí | Cookie-Based (Session) | Token-Based (JWT) |
|---|---|---|
| **Lưu trạng thái** | **Stateful** (Server/Redis phải lưu). | **Stateless** (Server không cần lưu). |
| **Độ an toàn trước XSS** | Rất cao (nếu dùng `HttpOnly`). | Dễ bị trộm nếu lưu trong LocalStorage. |
| **Độ an toàn trước CSRF** | Dễ bị tấn công nếu không có CSRF Token. | Miễn nhiễm (nếu đính kèm qua header Authorization). |
| **Mở rộng (Scale)** | Tốn chi phí hạ tầng Redis để chia sẻ Session. | Dễ dàng scale ngang hàng trăm server. |
| **Đa nền tảng (Mobile/IoT)** | Kém (Mobile app quản lý cookie rất vất vả). | Hoàn hảo (Header `Bearer` dùng cho mọi nơi). |
| **Đăng xuất (Revoke)** | Xóa session trong Redis là user bị đá ra ngay lập tức. | Rất khó thu hồi trước hạn trừ khi dùng Blacklist. |

---

## 🎯 6. Khi Nào Cookie-Based Vẫn Là "Vua"?

Dù Token/JWT rất hot, Cookie-based vẫn là **sự lựa chọn số 1** cho:
1. **Các trang web truyền thống / Server-Side Rendering (SSR)**: Dùng Thymeleaf, Next.js, Django, Laravel, Rails.
2. **Hệ thống Ngân hàng, Sàn chứng khoán, Quản trị nội bộ**: Nơi cần quyền **thu hồi tài khoản lập tức** nếu phát hiện nghi vấn (Admin bấm 1 nút là đá văng user ra khỏi hệ thống ngay).
3. **Ứng dụng chỉ chạy trên Trình duyệt Web** (không có mobile app riêng biệt).
