# 🔐 Authentication Best Practices Cho Backend API

Trong bảo mật phần mềm, **Authentication (Xác thực - Bạn là ai?)** là cánh cổng phòng thủ đầu tiên ngăn chặn kẻ xấu thâm nhập vào hệ thống. Nếu tầng này bị chọc thủng, toàn bộ các lớp phân quyền (**Authorization**) và dữ liệu bên trong coi như "mở toang".

Dưới đây là **4 nguyên tắc bảo mật tối thượng** được đúc kết từ tiêu chuẩn **OWASP API Security Top 10** và các hệ thống phân tán cấp độ doanh nghiệp:

```mermaid
flowchart LR
    classDef rule fill:#fef3c7,stroke:#f59e0b,stroke-width:2px,color:#92400e;

    R1["1. Avoid Basic Auth<br/>Use Standard (JWT/OAuth2)"]:::rule
    R2["2. Don't Reinvent Wheel<br/>Use Audited Libraries"]:::rule
    R3["3. Max Retry & Jail<br/>Lockout & Rate Limit"]:::rule
    R4["4. Encrypt Sensitive Data<br/>Transit & Rest Protection"]:::rule

    R1 --> R2 --> R3 --> R4
```

---

## 🚫 1. Tránh Dùng "Basic Authentication" — Chuyển Sang Các Chuẩn Hiện Đại (JWT, OAuth 2.0)

### ⚠️ Vì sao Basic Authentication bị coi là "Bad Practice" trên Production?

HTTP Basic Authentication gửi thông tin đăng nhập theo mỗi request trong Header:
`Authorization: Basic dXNlcm5hbWU6cGFzc3dvcmQ=`

1. **Base64 không phải là mã hóa (Obfuscation $\neq$ Encryption):**
   - Chuỗi `dXNlcm5hbWU6cGFzc3dvcmQ=` có thể được giải mã ngược thành `username:password` trong 1 mili-giây bằng bất kỳ công cụ online nào hoặc lệnh `atob()` trong JavaScript console.
   - Nếu đường truyền không có HTTPS (hoặc bị tấn công Man-In-The-Middle / bóc chứng chỉ SSL), mật khẩu gốc sẽ bị lộ ngay lập tức.
2. **Gửi mật khẩu gốc trong MỌI request:**
   - Thay vì chỉ gửi credential 1 lần lúc đăng nhập, Basic Auth bắt Client phải lưu mật khẩu gốc (hoặc chuỗi user:pass) ở LocalStorage/Memory và bắn lên server ở từng cú click chuột.
3. **Không có thời hạn hết hạn (TTL / Expiration):**
   - Chuỗi Basic Auth có hiệu lực vĩnh viễn cho đến khi người dùng đổi mật khẩu. Kẻ xấu cướp được header này có thể tái sử dụng bất cứ lúc nào.
4. **Không hỗ trợ phân quyền chi tiết (Granular Scopes):**
   - Không thể cấp quyền: "Token này chỉ được đọc dữ liệu trong 15 phút mà không được phép chuyển tiền".
5. **Không thể hủy phiên (Revocation / Logout):**
   - Trừ khi Server đổi mật khẩu của User, không có cơ chế HTTP nào cho phép vô hiệu hóa phiên Basic Auth từ phía Backend.

### 🛡️ Chuẩn thay thế: Token-Based Authentication (JWT & OAuth 2.0 / OIDC)

```mermaid
sequenceDiagram
    autonumber
    actor User as Client (Web/Mobile)
    participant Auth as Auth Server (Token Issuer)
    participant API as Resource Server (Backend API)

    User->>Auth: Gửi credentials 1 lần duy nhất (Email + Password)
    Auth-->>Auth: Kiểm tra mật khẩu (BCrypt / Argon2id)
    Auth->>User: Cấp Access Token (JWT - 15 phút) + Refresh Token (7 ngày)
    
    Note over User,Auth: Client KHÔNG CÒN lưu trữ Password gốc!
    
    User->>API: Gửi Request kèm Header: Bearer <JWT_TOKEN>
    API-->>API: Xác thực chữ ký số (Verify Signature)
    API->>User: Trả dữ liệu (200 OK)
```

| Tiêu Chí So Sánh | HTTP Basic Authentication | Token-Based (JWT / OAuth 2.0) |
| :--- | :--- | :--- |
| **Bảo mật Credential** | ❌ Gửi mật khẩu thô trong mọi request | ✅ Chỉ gửi mật khẩu 1 lần lúc lấy Token |
| **Thời hạn sống (TTL)** | ❌ Vĩnh viễn (đến khi đổi pass) | ✅ Hết hạn nhanh (15 - 30 phút qua claim `exp`) |
| **Phân quyền (Scopes)** | ❌ Không hỗ trợ | ✅ Tích hợp sẵn Roles, Scopes, Claims |
| **Thu hồi (Revocation)** | ❌ Rất khó (phải đổi password) | ✅ Dễ dàng quản lý qua Refresh Token & Blacklist |
| **Môi trường phù hợp** | ⚠️ Webhook nội bộ, Staging demo đơn giản | 🚀 Tiêu chuẩn bắt buộc cho Production Web & Mobile |

---

## 🎡 2. Đừng "Phát Minh Lại Bánh Xe" (Do Not Reinvent the Wheel)

Trong giới an ninh mạng có một nguyên lý nổi tiếng:
> **"Never roll your own crypto, and never write your own auth framework from scratch."**
> *(Đừng bao giờ tự chế thuật toán mã hóa, và đừng bao giờ tự viết hệ thống xác thực từ con số không).*

### 💥 Những "thảm họa" khi lập trình viên tự code Authentication:

```mermaid
graph TD
    classDef danger fill:#fee2e2,stroke:#ef4444,stroke-width:2px,color:#991b1b;
    classDef safe fill:#dcfce7,stroke:#22c55e,stroke-width:2px,color:#166534;

    A["Tự viết Auth Logic"] --> B["Băm password bằng MD5 / SHA-256 thuần<br/>(Dễ bị tra Rainbow Table trong vài giây)"]:::danger
    A --> C["Dùng phép so sánh chuỗi '==' kiểm tra token<br/>(Dính lỗ hổng Timing Attack)"]:::danger
    A --> D["Dùng Math.random() để sinh Token/OTP<br/>(Số ngẫu nhiên giả - Pseudo-random có thể đoán trước)"]:::danger
    A --> E["Tự parse chuỗi JWT bỏ qua kiểm tra thuật toán<br/>(Dính lỗ hổng bypass 'alg: none')"]:::danger
```

1. **Băm mật khẩu bằng thuật toán nhanh:** Nhiều dev tự dùng `MD5` hoặc `SHA-256` vì nghĩ nó an toàn. Nhưng các thuật toán này sinh ra để băm file nhanh, máy tính có thể thử **hàng tỷ phép thử mỗi giây** $\to$ Kẻ tấn công bẻ gãy mật khẩu dễ dàng qua **Rainbow Table** hoặc GPU Brute-force.
2. **Lỗ hổng Timing Attack (Tấn công chênh lệch thời gian):**
   ```java
   // ❌ NGUY HIỂM: Phép so sánh chuỗi thông thường trả về false ngay khi ký tự đầu tiên sai
   if (providedToken.equals(realToken)) { ... }

   // ✅ CHUẨN: So sánh thời gian hằng số (Constant-time comparison)
   MessageDigest.isEqual(providedToken.getBytes(), realToken.getBytes());
   ```
3. **Sinh chuỗi ngẫu nhiên không an toàn:** Dùng `Math.random()` (hoặc `Random` thông thường) thay vì `SecureRandom` chuẩn mật mã học khiến hacker đoán được mã OTP tiếp theo dựa trên chuỗi số trước đó.

### 🏆 Giải pháp chuẩn công nghiệp:
- **Ngôn ngữ & Framework:** Tận dụng tối đa các thư viện đã được hàng triệu chuyên gia kiểm thử (**Spring Security**, **Passport.js**, **ASP.NET Core Identity**, **Django Auth**).
- **Hệ thống Quản lý Danh tính (IAM / IdP):** Tích hợp giải pháp chuyên nghiệp như **Keycloak** (Open Source), **Auth0**, **Firebase Authentication**, **AWS Cognito**, **Ory Kratos**.
- **Thuật toán băm mật khẩu chuẩn:** Luôn dùng **Argon2id** (chuẩn khuyến nghị OWASP) hoặc **BCrypt** (với Cost Factor $\ge 12$).

---

## ⏳ 3. Sử Dụng "Max Retry" & Cơ Chế "Jail" Khi Đăng Nhập

Tấn công phổ biến nhất nhắm vào API đăng nhập là:
- **Brute-Force:** Thử lần lượt hàng trăm nghìn mật khẩu vào tài khoản `admin`.
- **Credential Stuffing:** Dùng danh sách hàng triệu cặp email/mật khẩu rò rỉ từ các vụ hack khác để thử đăng nhập tự động.

```mermaid
stateDiagram-v2
    [*] --> Active: Đăng ký thành công
    Active --> Active: Đăng nhập sai (< 5 lần)
    Active --> Jail: Đăng nhập sai liên tiếp >= 5 lần
    state Jail {
        [*] --> Temporary_Lock: Khóa tài khoản trong 15 phút
        Temporary_Lock --> Captcha_Challenge: Hết 15 phút hoặc mở khóa qua Email
    }
    Jail --> Active: Xác thực thành công (OTP / CAPTCHA)
```

### 3 Tầng phòng thủ phối hợp:

#### 1️⃣ Cơ chế Max Retry & Account Jail (Khóa tài khoản theo mức độ)
- **Quy tắc 5 lần:** Cho phép người dùng đăng nhập sai tối đa 5 lần liên tiếp.
- **Khóa mềm (Temporary Jail):** Sau lần thứ 5, tài khoản bị đóng băng tạm thời trong **15 - 30 phút**. Mọi request đăng nhập vào email này đều bị từ chối ngay lập tức mà không thèm truy vấn vào database (giảm tải CPU).
- **Khóa cứng (Hard Jail):** Sau nhiều đợt khóa mềm liên tiếp, chuyển sang khóa vĩnh viễn và bắt buộc người dùng xác thực qua **Link gửi về Email / SMS OTP**.

#### 2️⃣ Kỹ thuật Progressive Delay (Exponential Backoff)
Mỗi lần đăng nhập sai, server cố tình kéo dài thời gian phản hồi:
- Sai lần 1: Trả về sau **0.5s**
- Sai lần 2: Trả về sau **1s**
- Sai lần 3: Trả về sau **2s**
- Sai lần 4: Trả về sau **4s**
👉 Làm tê liệt hoàn toàn các con bot quét hàng trăm request mỗi giây.

#### 3️⃣ IP Jail & Rate Limiting (Chặn ở tầng mạng / Gateway)
Nếu một IP lạ thử hàng trăm tài khoản khác nhau trong 1 phút (Credential Stuffing):
- Sử dụng **Fail2ban** hoặc cấu hình **Rate Limiting** trên Nginx / Cloudflare WAF: Giới hạn tối đa 5 requests/phút vào endpoint `/api/v1/auth/login`.
- Nếu vượt ngưỡng: Bắt buộc giải **Cloudflare Turnstile** (hoặc reCAPTCHA v3) hoặc đưa IP vào danh sách đen (IP Jail) trong 1 giờ.

#### 💻 Mẫu triển khai Max Retry & Lockout bằng Redis + Spring Boot:

```java
@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private final StringRedisTemplate redisTemplate;
    private static final int MAX_ATTEMPTS = 5;
    private static final Duration LOCK_DURATION = Duration.ofMinutes(15);

    // Kiểm tra xem tài khoản có đang bị giam (Jail) không
    public boolean isBlocked(String email) {
        String key = "login:blocked:" + email;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    // Ghi nhận đăng nhập thất bại
    public void loginFailed(String email) {
        String attemptKey = "login:attempts:" + email;
        Long attempts = redisTemplate.opsForValue().increment(attemptKey);
        
        // Đặt TTL 15 phút cho cụm đếm
        if (attempts != null && attempts == 1) {
            redisTemplate.expire(attemptKey, LOCK_DURATION);
        }

        // Vượt quá Max Retry -> Đưa vào Jail
        if (attempts != null && attempts >= MAX_ATTEMPTS) {
            redisTemplate.opsForValue().set("login:blocked:" + email, "LOCKED", LOCK_DURATION);
            redisTemplate.delete(attemptKey); // reset bộ đếm
        }
    }

    // Đăng nhập thành công -> Xóa cờ lỗi
    public void loginSucceeded(String email) {
        redisTemplate.delete("login:attempts:" + email);
        redisTemplate.delete("login:blocked:" + email);
    }
}
```

---

## 🔒 4. Mã Hóa Toàn Diện Mọi Dữ Liệu Nhạy Cảm (Encrypt All Sensitive Data)

Một hệ thống an toàn phải bảo vệ dữ liệu ở cả 2 trạng thái: **Đang di chuyển (In Transit)** và **Đang lưu trữ (At Rest)**.

```mermaid
flowchart TD
    subgraph S1["Data in Transit (Trên đường truyền)"]
        T1["Bắt buộc HTTPS / TLS 1.3"]
        T2["Strict-Transport-Security (HSTS)"]
        T3["Chống Man-in-the-Middle (MITM)"]
    end

    subgraph S2["Data at Rest (Trong ổ đĩa / CSDL)"]
        R1["Mật khẩu: Băm một chiều có muối (BCrypt / Argon2id)"]
        R2["Dữ liệu cá nhân PII (CCCD, Thẻ tín dụng): AES-256-GCM"]
        R3["Quản lý khóa tập trung bằng KMS / Vault"]
    end
```

### 1️⃣ Dữ liệu trên đường truyền (Data in Transit)
- **Bắt buộc 100% kết nối qua HTTPS:** Tuyệt đối không phục vụ bất kỳ endpoint API nào qua `http://` trần.
- **Kích hoạt Header `Strict-Transport-Security` (HSTS):** Ép buộc trình duyệt chỉ được gửi request bằng HTTPS trong vòng 1 năm (`max-age=31536000; includeSubDomains`).
- **TLS 1.2 / TLS 1.3:** Tắt các giao thức lỗi thời (SSLv3, TLS 1.0, TLS 1.1) vì chứa nhiều lỗ hổng nguy hiểm như POODLE, BEAST.

### 2️⃣ Dữ liệu lưu trữ (Data at Rest)
Dữ liệu nhạy cảm được chia thành 2 nhóm với nguyên tắc xử lý hoàn toàn khác biệt:

#### Nhóm 1: Mật khẩu người dùng (Passphrases, PINs)
- **Nguyên tắc:** **KHÔNG BAO GIỜ ĐƯỢC PHÉP GIẢI MÃ!**
- Sử dụng thuật toán **Băm một chiều có muối (One-way Salted Hashing)**:
  - Muối (Salt): Chuỗi ngẫu nhiên sinh riêng cho từng người dùng, chống tra cứu bảng băm tính sẵn.
  - Thuật toán chuẩn: **Argon2id** hoặc **BCrypt**.

#### Nhóm 2: Thông tin định danh cá nhân PII (CCCD/CMND, Số thẻ tín dụng, API Secret Keys của khách)
- **Nguyên tắc:** Dữ liệu này hệ thống cần đọc lại được để xử lý nghiệp vụ $\to$ Bắt buộc dùng **Mã hóa đối xứng hiện đại (Symmetric Encryption)**:
  - Thuật toán khuyến nghị: **AES-256-GCM** (Galois/Counter Mode — vừa mã hóa vừa xác thực toàn vẹn dữ liệu, chống can thiệp ciphertext).
  - **Quản lý khóa (Key Management):** KHÔNG BAO GIỜ lưu khóa giải mã (Secret Key) trực tiếp trong file code hoặc file `application.properties`. Sử dụng dịch vụ bảo mật chuyên dụng:
    - **AWS KMS / Google Cloud KMS / Azure Key Vault**
    - **HashiCorp Vault**
    - Biến môi trường bảo mật (Environment Secrets) trên Server.

#### Nhóm 3: Che giấu dữ liệu (Data Masking / Redaction)
- Khi trả dữ liệu ra API response hoặc ghi nhật ký hệ thống (Application Logs):
  - Số điện thoại: `090****123`
  - Số thẻ tín dụng: `**** **** **** 5678`
  - CCCD: `0010******89`
  - Mật khẩu, Token, CVV: **TUYỆT ĐỐI KHÔNG GHI VÀO LOG FILE!**

---

## 📋 Checklist Tổng Kết Thực Chiến Cho Backend Developer

| Hạng Mục | Tiêu Chuẩn Thực Hiện | Trạng Thái Kiểm Tra |
| :--- | :--- | :---: |
| **Giao thức xác thực** | Đã loại bỏ Basic Auth trên Public API; Dùng Bearer JWT / OAuth2 | [ ] |
| **Thư viện Auth** | Dùng thư viện chuẩn (Spring Security / Passport / Keycloak); Không tự chế crypto | [ ] |
| **Thuật toán mật khẩu** | Đã băm bằng BCrypt (Cost $\ge 12$) hoặc Argon2id kèm Salt ngẫu nhiên | [ ] |
| **Phòng thủ Brute-force** | Đã giới hạn Max 5 lần thử sai; Khóa tạm thời 15 phút (Jail) bằng Redis | [ ] |
| **Chống DoS / Bot** | Endpoint login có Rate Limiting (5 req/phút/IP) và CAPTCHA Turnstile | [ ] |
| **Đường truyền an toàn** | 100% HTTPS TLS 1.3, bật header HSTS | [ ] |
| **Mã hóa dữ liệu tĩnh** | Dữ liệu PII (Thẻ, CCCD) được mã hóa AES-256-GCM; Key lưu tại KMS/Vault | [ ] |
| **An toàn Log** | Không in mật khẩu, JWT token, thẻ tín dụng ra log file | [ ] |
