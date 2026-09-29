# 🔐 Chuyên Đề: Authentication & Authorization Trong Backend

Thư mục này tập trung vào các kiến thức cốt lõi, chuẩn giao thức và chiến lược triển khai bảo mật, xác thực (Authentication - Ai đây?) và phân quyền (Authorization - Được làm gì?) trong hệ thống Backend hiện đại.

---

## 📚 Danh Mục Tài Liệu

1. 🎫 **[JWT - JSON Web Token (Cơ Chế, Cấu Trúc & Thực Tế)](./01_jwt_fundamentals.md)**
   - Bản chất Stateless, giải phẫu 3 phần: Header, Payload, Signature.
   - Hiểu lầm kinh điển: "JWT không hề được mã hóa (encrypt), nó chỉ được ký số (sign)!".
   - Chiến lược Access Token (ngắn hạn) + Refresh Token (dài hạn).
   - So sánh thuật toán đối xứng (HS256) vs bất đối xứng (RS256 trong Microservices).
   - Bài toán thu hồi token (Token Revocation / Logout).

2. 🛡️ **[OAuth 2.0 & OpenID Connect - OIDC](./02_oauth2_and_oidc.md)**
   - Ẩn dụ kinh điển: Thẻ chìa khóa phụ Valet Key khách sạn.
   - 4 Vai trò cốt lõi: Resource Owner, Client, Authorization Server, Resource Server.
   - Luồng chuẩn: Authorization Code Flow (tại sao phải dùng mã tạm `code`?).
   - Các Grant Types hiện đại (PKCE cho SPA/Mobile, Client Credentials cho M2M).
   - Phân biệt sống còn: OAuth 2.0 (Ủy quyền - Authorization) vs OIDC (Đăng nhập - Authentication).

3. 🌐 **[Case Study: Chuyện Gì Xảy Ra Khi Bấm "Đăng Nhập Bằng Google"?](./03_login_with_google_flow.md)**
   - Kịch bản 8 bước chi tiết từ lúc bấm nút trên Frontend đến khi có tài khoản trong DB.
   - Request & Response thực tế: `client_id`, `redirect_uri`, mã `code`, `id_token`.
   - Hiểu lầm phổ biến nhất: Dùng token của Google hay token của chính hệ thống mình?

4. 🔑 **[HTTP Basic Authentication (Cơ Bản & Thực Dụng)](./04_basic_authentication.md)**
   - Cơ chế Thách thức & Phản hồi (Challenge - Response qua status 401 & header `WWW-Authenticate`).
   - Công thức Base64 `username:password` và cảnh báo an toàn sống còn (Luôn dùng chung với HTTPS).
   - Ưu nhược điểm và các trường hợp sử dụng thực tế (Nginx staging, Webhook, Swagger lock).

5. 🛠️ **[Thực Hành: Cấu Hình Basic Auth](./05_configuring_basic_auth.md)**
   - Cách 1: Nginx Web Server (Dùng `htpasswd` và `auth_basic` để khóa website Staging).
   - Cách 2: Spring Boot 3.x Java (Kích hoạt với Spring Security `httpBasic()`).
   - Cách 3: Tự code thủ công bóc tách Header `Authorization` (Node.js/Express).
   - Cách test bằng `curl` và Postman.
