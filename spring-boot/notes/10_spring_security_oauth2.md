# 10. Spring Security OAuth2 & OpenID Connect

OAuth2 là chuẩn ủy quyền (Authorization Framework) cho phép ứng dụng truy cập tài nguyên của người dùng từ một dịch vụ bên thứ 3 (Google, Facebook, GitHub, Keycloak) mà không cần người dùng cung cấp mật khẩu trực tiếp.

---

## 👥 4 Thành Phần Trong Mô Hình OAuth2

1. **Resource Owner (Chủ tài nguyên):** Người dùng cuối (User).
2. **Client (Ứng dụng):** Ứng dụng Spring Boot hoặc Single Page App (React/Vue).
3. **Authorization Server (Máy chủ ủy quyền):** Nơi xác thực người dùng và cấp Token (Google, Auth0, Keycloak).
4. **Resource Server (Máy chủ tài nguyên):** Nơi chứa API dữ liệu bảo mật (Spring Boot Backend API).

---

## 🛠️ 2 Kịch Bản Tích Hợp OAuth2 Phổ Biến Trong Spring Boot

### 1. Spring Boot làm OAuth2 Client (Đăng nhập mạng xã hội)
Sử dụng dependency `spring-boot-starter-oauth2-client`.

```yaml
# application.yml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${GOOGLE_CLIENT_ID}
            client-secret: ${GOOGLE_CLIENT_SECRET}
            scope:
              - email
              - profile
```

Spring Security tự động xử lý luồng đổi Code lấy Token và trả về đối tượng `OAuth2User` trong Controller:

```java
@RestController
public class UserController {

    @GetMapping("/user-info")
    public Map<String, Object> getUserInfo(@AuthenticationPrincipal OAuth2User principal) {
        // Lấy thông tin email, name từ Google
        return principal.getAttributes();
    }
}
```

---

### 2. Spring Boot làm OAuth2 Resource Server (Xác thực JWT Token cho REST API)
Sử dụng dependency `spring-boot-starter-oauth2-resource-server`.

Dùng khi Frontend (React/Angular/Mobile) tự đăng nhập và đính kèm JWT Token vào Header `Authorization: Bearer <token>` mỗi khi gọi API.

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/public/**").permitAll()
                .anyRequest().authenticated()
            )
            // Cấu hình ứng dụng làm Resource Server xác thực JWT Token tự động
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }
}
```

---

## 💡 Tips & Tricks Khi Làm Việc Với OAuth2

1. **Không bao giờ lưu Client Secret trong Git Repository:**
   Luôn sử dụng biến môi trường (Environment Variables): `${GOOGLE_CLIENT_SECRET}`.

2. **Dùng Keycloak / Auth0 cho hệ thống Microservices:**
   Thay vì tự viết hệ thống Đăng nhập/Đăng ký từ đầu, dựng 1 Server Keycloak (Mã nguồn mở) làm Authorization Server tập trung cho toàn bộ hệ thống.
