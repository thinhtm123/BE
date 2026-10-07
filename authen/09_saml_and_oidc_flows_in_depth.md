# 🔄 Luồng Chạy Chi Tiết: SAML 2.0 SSO vs. OpenID Connect (OIDC)

Để hiểu tận cùng "dưới nắp ca-pô" SAML và OIDC chạy khác nhau như thế nào, hãy xét một ví dụ thực tế hàng ngày:

> 🏢 **Tình huống thực tế:**  
> Bạn là một kỹ sư phần mềm tại tập đoàn. Sáng nay bạn mở trình duyệt và truy cập vào **Jira** (`jira.mycompany.com`) để xem task.  
> Công ty của bạn dùng **Okta** (hoặc **Microsoft Azure AD / Entra ID**) để quản lý tài khoản nhân viên tập trung.

---

## 🏛️ 1. Luồng Chạy Của SAML 2.0 (SP-Initiated SSO Flow)

Đây là luồng 95% các tập đoàn dùng để đăng nhập vào Jira, Zoom, Salesforce, Workday:

```mermaid
sequenceDiagram
    autonumber
    actor User as 👤 Nhân Viên (Browser)
    participant SP as 🛠️ Jira (Service Provider)
    participant IdP as 🏢 Okta (Identity Provider)

    User->>SP: 1. Truy cập https://jira.mycompany.com
    Note over SP: Jira kiểm tra: Chưa có Session!
    SP-->>User: 2. HTTP 302 Redirect sang Okta kèm:<br/>?SAMLRequest=<chuỗi XML đã Base64>
    
    User->>IdP: 3. Trình duyệt chuyển hướng đến Okta
    IdP-->>User: 4. Hiển thị form: Nhập mật khẩu tập đoàn + Bấm Approve OTP trên điện thoại
    User->>IdP: 5. Xác thực thành công!

    Note over IdP: Okta tạo gói tin XML SAML Response.<br/>Dùng Private Key ký số (Digital Signature).
    IdP-->>User: 6. Trả về trang HTML ẩn (chứa SAMLResponse)<br/>kèm đoạn JavaScript tự động submit: document.forms[0].submit()

    User->>SP: 7. Trình duyệt tự động POST SAMLResponse về Jira<br/>(vào endpoint: /saml/acs)
    Note over SP: Jira dùng Public Key (X.509 Cert) của Okta<br/>để verify chữ ký. Đọc ra email: nam@company.com
    SP-->>User: 8. Cấp Session Cookie và mở trang Dashboard của Jira!
```

---

### 🔍 Giải Phẫu 2 Gói Tin XML Thực Tế Của SAML

#### Gói tin 1: Jira gửi yêu cầu (`SAMLRequest`)
Được Jira tạo ra khi bạn vừa vào web, đóng gói dưới dạng XML:

```xml
<samlp:AuthnRequest xmlns:samlp="urn:oasis:names:tc:SAML:2.0:protocol"
                    ID="_d7b2a9e1..."
                    Version="2.0"
                    IssueInstant="2026-09-30T08:00:00Z"
                    Destination="https://mycompany.okta.com/app/sso/saml"
                    AssertionConsumerServiceURL="https://jira.mycompany.com/saml/acs">
    <saml:Issuer xmlns:saml="urn:oasis:names:tc:SAML:2.0:assertion">
        https://jira.mycompany.com
    </saml:Issuer>
</samlp:AuthnRequest>
```
*Dịch nghĩa:* "Tôi là Jira. Hãy xác thực người dùng này giúp tôi, xác thực xong thì gửi kết quả về địa chỉ `AssertionConsumerServiceURL` nhé!"

---

#### Gói tin 2: Okta trả kết quả (`SAMLResponse` / `SAML Assertion`)
Sau khi bạn đăng nhập thành công, Okta phát hành một tệp XML có chữ ký số điện tử:

```xml
<samlp:Response xmlns:samlp="urn:oasis:names:tc:SAML:2.0:protocol">
    <saml:Issuer>https://mycompany.okta.com</saml:Issuer>
    <samlp:Status>
        <samlp:StatusCode Value="urn:oasis:names:tc:SAML:2.0:status:Success"/>
    </samlp:Status>
    
    <!-- CHỮ KÝ SỐ ĐIỆN TỬ BẰNG CERTIFICATE X.509 -->
    <ds:Signature xmlns:ds="http://www.w3.org/2000/09/xmldsig#">
        <ds:SignatureValue>i8B3mZ9... (chữ ký mã hóa cực dài)</ds:SignatureValue>
    </ds:Signature>

    <!-- NỘI DUNG KHẲNG ĐỊNH DANH TÍNH (ASSERTION) -->
    <saml:Assertion ID="_a9b8c7...">
        <saml:Subject>
            <saml:NameID>nam.nguyen@company.com</saml:NameID>
        </saml:Subject>
        <saml:AttributeStatement>
            <saml:Attribute Name="Role"><saml:AttributeValue>DEVELOPER</saml:AttributeValue></saml:Attribute>
            <saml:Attribute Name="Department"><saml:AttributeValue>IT_BACKEND</saml:AttributeValue></saml:Attribute>
        </saml:AttributeStatement>
    </saml:Assertion>
</samlp:Response>
```

#### 💡 Điểm đặc biệt của SAML: Kỹ thuật "Auto-Submit Form" (Bước 6 & 7)
Vì gói tin XML này rất lớn (vài KB), không thể nhét vừa trên URL của trình duyệt (GET), nên Okta trả về cho trình duyệt một đoạn code HTML như sau:
```html
<form method="POST" action="https://jira.mycompany.com/saml/acs">
    <input type="hidden" name="SAMLResponse" value="PHNhbWxwOlJlc3BvbnNl..." />
</form>
<script>
    // Trình duyệt tự chạy dòng này ngay lập tức mà người dùng không cần bấm nút!
    document.forms[0].submit(); 
</script>
```
Đó là lý do khi bạn đăng nhập SSO ở công ty, bạn thường thấy màn hình chớp trắng 1 giây với dòng chữ: *"Please wait while we redirect you..."*.

---

## ⚡ 2. Luồng Chạy Của OpenID Connect (OIDC) Để So Sánh

Trong khi SAML bắt trình duyệt phải ôm gói tin XML cồng kềnh bay qua bay lại, thì OIDC xử lý thanh thoát hơn rất nhiều:

```mermaid
sequenceDiagram
    autonumber
    actor User as 👤 Người Dùng (Mobile / React App)
    participant Client as 🖥️ Client App
    participant OP as 🌐 Identity Server (Auth0 / Google)

    User->>Client: 1. Bấm "Login with Google / SSO"
    Client-->>User: 2. Redirect sang Google kèm: scope=openid email profile
    User->>OP: 3. Đăng nhập tại Google
    OP-->>User: 4. Redirect về Client kèm mã tạm: ?code=XYZ123
    User->>Client: 5. Gửi mã code cho Backend Client
    
    Note over Client,OP: GIAO TIẾP NỘI BỘ GIỮA 2 SERVER (REST / JSON)
    Client->>OP: 6. POST /token (code + client_secret)
    OP-->>Client: 7. Trả về JSON chứa ID TOKEN (JWT):<br/>{ "id_token": "eyJhbGciOiJSUzI1Ni..." }
    
    Note over Client: Client giải mã JWT: Có ngay user_id, email, name.<br/>Không cần phân tích XML phức tạp!
    Client-->>User: 8. Đăng nhập thành công!
```

---

## 🥊 3. So Sánh Bản Chất 2 Luồng: Khác Nhau Ở Đâu?

| Khâu | SAML 2.0 | OpenID Connect (OIDC) |
|---|---|---|
| **Kênh trao đổi dữ liệu** | **100% qua Trình duyệt (Front-channel)**: Trình duyệt phải "gánh" gói tin XML từ IdP ném sang SP bằng Auto-submit form. | **Kênh nội bộ (Back-channel)**: Trình duyệt chỉ nhận mã tạm `code`, còn việc lấy token thực tế diễn ra qua kênh kín giữa 2 máy chủ. |
| **Độ an toàn token** | XML Assertion đi xuyên qua trình duyệt (dễ bị extension hoặc proxy đọc). | `id_token` được Backend lấy trực tiếp từ Identity Server qua HTTPS bảo mật. |
| **Công cụ xử lý** | Backend phải dùng các parser XML khổng lồ và xử lý xác thực chữ ký X.509 phức tạp. | Bất kỳ thư viện JWT nào cũng giải mã được chuỗi JSON trong 1 mili-giây. |
| **Ứng dụng Mobile App** | ❌ Gần như bế tắc vì Mobile App không có form submit HTML tự nhiên như trình duyệt. | ✅ Cực kỳ mượt mà vì chỉ là gọi API REST nhận JSON. |
