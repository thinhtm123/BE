# 11. JWT Authentication & SecurityContext

JWT (JSON Web Token) là chuẩn mở (RFC 7519) dùng để truyền tải thông tin an toàn giữa các bên dưới dạng JSON. Trong Spring Security, JWT được dùng để thực thi cơ chế xác thực **Stateless** (không lưu Session phía Server) cho RESTful APIs.

---

## 🎫 1. Cấu Trúc JWT (3 Phần)

```text
[Header].[Payload].[Signature]
```
- **Header:** Chứa loại token (`JWT`) và thuật toán ký (`HS256` hoặc `RS256`).
- **Payload:** Chứa thông tin người dùng (Claims) như `sub` (username), `roles`, `exp` (thời gian hết hạn).
- **Signature:** Chữ ký bí mật đảm bảo Token không bị can thiệp/sửa đổi bởi bên thứ 3.

---

## 🛠️ 2. Viết Custom `JwtAuthenticationFilter` trong Spring Security

Filter này kế thừa `OncePerRequestFilter` để đảm bảo nó chỉ chạy **đúng 1 lần duy nhất trên mỗi HTTP Request**.

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        // 1. Trích xuất Token từ Header "Authorization: Bearer <token>"
        String token = getJwtFromRequest(request);

        // 2. Validate Token (Kiểm tra chữ ký & hạn sử dụng)
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            String username = jwtTokenProvider.getUsernameFromToken(token);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 3. Tạo đối tượng Authentication
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 4. NẠP VÀO SECURITY CONTEXT HOLDER
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 5. Cho phép Request đi tiếp tới Filter tiếp theo hoặc Controller
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

---

## ⚙️ 3. Đăng Ký JwtFilter Vào `SecurityFilterChain`

Cần đặt `JwtAuthenticationFilter` lên **trước** `UsernamePasswordAuthenticationFilter` mặc định của Spring:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            // THÊM JWT FILTER VÀO TRƯỚC HÀNG RÀO MẶC ĐỊNH
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

---

## 💡 Tips & Tricks Khi Dùng JWT

1. **Không lưu thông tin nhạy cảm vào Payload:**
   Payload của JWT chỉ mã hóa Base64 đơn giản (ai cũng mở ra đọc được). **Tuyệt đối KHÔNG lưu Password hay SSN vào Payload**.

2. **Luôn thiết lập Expiration Time (Thời gian hết hạn):**
   Gắn thời hạn hết hạn ngắn cho Access Token (ví dụ: 15-30 phút) và sử dụng **Refresh Token** để xin cấp lại Access Token mới.
