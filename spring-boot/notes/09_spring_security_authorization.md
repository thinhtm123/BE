# 09. Spring Security Authorization (Phân quyền)

Authorization (Phân quyền) kiểm tra xem một người dùng đã xác thực (Authenticated) có đủ quyền hạn để truy cập tài nguyên hoặc thực thi phương thức cụ thể hay không.

---

## 🔒 2 Cách Phân Quyền Trong Spring Security

### 1. Phân quyền theo URL (URL Matching)
Cấu hình tập trung tại `SecurityFilterChain`:

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(auth -> auth
        .requestMatchers("/api/v1/public/**").permitAll()
        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
        .requestMatchers(HttpMethod.DELETE, "/api/v1/products/**").hasAuthority("PRODUCT_DELETE")
        .anyRequest().authenticated()
    );
    return http.build();
}
```

---

### 2. Phân quyền theo Method (Method-Level Authorization)
Bật tính năng trong class cấu hình với `@EnableMethodSecurity`:

```java
@Configuration
@EnableMethodSecurity // Bật phân quyền theo Annotation trên Method
public class SecurityConfig { }
```

---

## 🏷️ Annotations Phân Quyền Trên Method

### 1. `@PreAuthorize` (Phổ biến nhất)
Kiểm tra quyền **TRƯỚC KHI** chạy hàm. Hỗ trợ biểu thức SpEL (Spring Expression Language):

```java
@Service
public class UserService {

    // Chỉ ADMIN mới được gọi
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(Long id) { }

    // ADMIN hoặc Chính chủ của tài khoản mới được cập nhật
    @PreAuthorize("hasRole('ADMIN') or #username == authentication.name")
    public void updateProfile(String username, UserDTO dto) { }
}
```

### 2. `@PostAuthorize`
Cho hàm chạy xong, rồi kiểm tra quyền dựa trên **kết quả trả về (`returnObject`)**:

```java
@PostAuthorize("returnObject.ownerEmail == authentication.name")
public Document getDocumentById(Long id) {
    return documentRepository.findById(id).orElseThrow();
}
```

### 3. `@Secured` (Kiểu cũ)
Cú pháp ngắn hơn nhưng không hỗ trợ SpEL:

```java
@Secured({"ROLE_ADMIN", "ROLE_MANAGER"})
public void exportReport() { }
```

---

## 💡 Tips & Tricks: Role vs Authority

- **Role (Vai trò):** Tự động thêm tiền tố `ROLE_`.
  - Cú pháp: `hasRole("ADMIN")` ➔ Kiểm tra authority `ROLE_ADMIN`.
- **Authority (Quyền hạn):** Không có tiền tố, dùng cho quyền thao tác chi tiết.
  - Cú pháp: `hasAuthority("USER_DELETE")` ➔ Kiểm tra đúng chuỗi `USER_DELETE`.
