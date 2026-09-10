# 08. Spring Security & Authentication

Spring Security là framework mạnh mẽ cung cấp các tính năng bảo mật toàn diện cho ứng dụng Java Web (Xác thực, Phân quyền, chống tấn công CSRF/XSS).

---

## 🔑 Phân biệt Authentication vs Authorization

- **Authentication (Xác thực):** Trả lời câu hỏi **"Bạn là ai?"**
  - Kiểm tra danh tính người dùng (Username/Password, JWT Token, OTP, OAuth2 Google/Facebook).
  - Trạng thái thất bại: **HTTP 401 Unauthorized** (Chưa đăng nhập).

- **Authorization (Phân quyền):** Trả lời câu hỏi **"Bạn có quyền làm gì?"**
  - Kiểm tra xem người dùng đã xác thực có được phép truy cập tài nguyên cụ thể hay không (Role `ROLE_ADMIN`, `ROLE_USER`).
  - Trạng thái thất bại: **HTTP 403 Forbidden** (Đã đăng nhập nhưng không đủ quyền).

---

## ⚡ Cơ Chế Hoạt Động: Security Filter Chain

Spring Security không nằm ở Controller hay Service. Nó đứng ở **Cửa ngoài cùng (Security Filter Chain)** trước khi HTTP Request chạm tới `DispatcherServlet`.

```
[ Client ] ──(HTTP Request)──► [ Security Filter Chain ] ──(Hợp lệ)──► [ DispatcherServlet / Controller ]
                                          │
                                     (Không hợp lệ)
                                          │
                                          ▼
                                [ 401 / 403 Error ]
```

---

## 🌐 3 Hình Thức Authentication Phổ Biến

1. **Form-based / Session Authentication (Truyền thống):**
   - Dùng cho ứng dụng Web Monolith (HTML/Thymeleaf).
   - Server lưu thông tin đăng nhập trong Session (`JSESSIONID`).

2. **JWT (JSON Web Token - Hiện đại & Stateless):**
   - Dùng phổ biến nhất cho **RESTful APIs & Mobile App**.
   - Server không cần lưu Session (Stateless). Client gửi Token kèm theo mỗi Request trong Header: `Authorization: Bearer <token>`.

3. **OAuth2 / OpenID Connect:**
   - Cho phép người dùng đăng nhập bằng tài khoản bên thứ 3 (Google, Facebook, GitHub).

---

## 💻 Cấu Hình Cơ Bản Spring Security (Spring Security 6+)

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Tắt CSRF cho REST API
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll() // Cho phép truy cập công khai endpoint Auth
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN") // Chỉ ADMIN mới được truy cập
                .anyRequest().authenticated() // Tất cả request khác phải đăng nhập
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Cấu hình Stateless cho JWT
            );

        return http.build();
    }

    @Bean // Bean mã hóa mật khẩu
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

---

## 💡 Tips & Tricks Thực Tế

1. **Luôn dùng `BCryptPasswordEncoder`:** 
   Không bao giờ lưu password dạng Plaintext vào Database. `BCrypt` tự động thêm muối (Salting) để chống tấn công Rainbow Table.

2. **Phân biệt `401 Unauthorized` và `403 Forbidden`:**
   - **401:** Chưa đăng nhập hoặc Token hết hạn ➔ Chuyển hướng người dùng về màn hình Login.
   - **403:** Đã đăng nhập rồi nhưng cố truy cập trang Admin ➔ Hiển thị thông báo "Bạn không có quyền truy cập".
