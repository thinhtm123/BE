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

6. 🎫 **[Token-Based Authentication (Toàn Cảnh Kiến Trúc)](./06_token_based_authentication.md)**
   - Ẩn dụ "Con dấu dạ quang công viên nước" vs Session-Cookie truyền thống.
   - Phân biệt 2 loại Token: Opaque Token (By-Reference qua Redis) vs Self-Contained Token (By-Value như JWT).
   - Vì sao Token áp đảo Cookie trong Mobile App, Microservices và CORS.
   - Vấn đề bảo mật sống còn: Lưu Token ở đâu trên Web? (LocalStorage vs HttpOnly Cookie, XSS vs CSRF).

7. 🍪 **[Cookie-Based Authentication (Session-Cookie Truyền Thống)](./07_cookie_based_authentication.md)**
   - Ẩn dụ "Thẻ gửi đồ siêu thị" và bản chất Stateful của Session.
   - Bộ ba cờ bảo vệ bắt buộc: `HttpOnly` (chống XSS), `Secure` (HTTPS), `SameSite` (chống CSRF).
   - 2 Thách thức lớn: Nguy cơ tấn công CSRF và bài toán mở rộng cụm server (Session Clustering với Redis).
   - So sánh toàn diện: Cookie-based vs Token-based & Khi nào Cookie vẫn là "vua".

8. 🏢 **[Single Sign-On (SSO): OpenID Connect (OIDC) vs. SAML 2.0](./08_openid_and_saml_sso.md)**
   - Bài toán Single Sign-On (SSO) trong doanh nghiệp: Một tài khoản mở mọi ứng dụng.
   - SAML 2.0: "Lão làng" Enterprise (2005) dùng XML, Chữ ký số X.509, IdP và SP.
   - OpenID Connect (OIDC): "Kẻ kế vị" hiện đại dùng JSON, REST, OAuth 2.0 và JWT `id_token`.
   - So sánh trực diện và lãnh địa thực tế (B2B Enterprise vs B2C/Mobile App).

9. 🔄 **[Luồng Chạy Chi Tiết: SAML 2.0 SSO vs. OIDC Flow](./09_saml_and_oidc_flows_in_depth.md)**
   - Kịch bản thực tế: Nhân viên vào Jira qua cổng quản lý tài khoản Okta.
   - Giải phẫu 2 gói tin XML cốt lõi: `<samlp:AuthnRequest>` và `<samlp:Response>`.
   - Cơ chế "Auto-Submit Form" độc đáo của SAML (tại sao trình duyệt hay bị chớp trắng 1 giây?).
   - So sánh bản chất Front-channel (SAML) vs Back-channel (OIDC REST/JSON).
