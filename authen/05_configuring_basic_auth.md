# 🛠️ Hướng Dẫn Thực Hành: Cấu Hình Basic Auth Trong Thực Tế

Trong thực tế, bạn sẽ cấu hình Basic Auth theo 3 cách phổ biến nhất:
1. **Tại tầng Web Server / Reverse Proxy (Nginx)** — Cách phổ biến nhất trong DevOps để khóa web staging.
2. **Tại tầng Backend Framework (Spring Boot / Java)** — Cách chuẩn cho ứng dụng doanh nghiệp.
3. **Tự viết thủ công (Node.js / Express hoặc thuần code)** — Giúp hiểu bản chất cách bóc tách Header.

---

## 🌐 1. Cách 1: Cấu Hình Bằng Nginx (DevOps Thường Dùng Nhất)

Đây là cách nhanh nhất để "khóa" một website demo/staging mà không cần sửa một dòng code backend nào.

### Bước 1: Tạo file mật khẩu `.htpasswd`
Dùng công cụ `apache2-utils` (hoặc `httpd-tools` trên CentOS/RedHat) để tạo tài khoản `admin` với mật khẩu được mã hóa:

```bash
# Cài công cụ nếu chưa có (Ubuntu/Debian)
sudo apt update && sudo apt install -y apache2-utils

# Tạo file .htpasswd với user "admin"
sudo htpasswd -c /etc/nginx/.htpasswd admin
# (Terminal sẽ yêu cầu bạn nhập và xác nhận password, ví dụ: "Secret@123")
```

File `/etc/nginx/.htpasswd` sinh ra sẽ có dạng:
```text
admin:$apr1$xyz123$abCDefGhIjKlMnOpQrStU.
```

### Bước 2: Cấu hình trong file Nginx (`nginx.conf`)
Mở file cấu hình website trong Nginx và thêm 2 dòng:

```nginx
server {
    listen 80;
    server_name staging.mycompany.com;

    location / {
        # BẬT BASIC AUTH TẠI ĐÂY:
        auth_basic "Khu vuc noi bo - Vui long dang nhap";
        auth_basic_user_file /etc/nginx/.htpasswd;

        # Chuyển tiếp tới backend của bạn
        proxy_pass http://localhost:8080;
    }
}
```

### Bước 3: Reload Nginx
```bash
sudo nginx -t && sudo systemctl reload nginx
```
👉 Giờ đây khi bất kỳ ai truy cập vào trang web, trình duyệt sẽ tự động bật popup bắt đăng nhập ngay lập tức!

---

## ☕ 2. Cách 2: Cấu Hình Trong Spring Boot (Java)

Với Spring Boot 3.x, bạn dùng **Spring Security** để kích hoạt Basic Auth chỉ với vài dòng code.

### Bước 1: Thêm dependency vào `pom.xml`
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

### Bước 2: Tạo file cấu hình `SecurityConfig.java`
```java
package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Tắt CSRF nếu chỉ làm REST API
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/public/**").permitAll() // Cho phép tự do
                .anyRequest().authenticated()               // Các API còn lại phải xác thực
            )
            // KÍCH HOẠT HTTP BASIC AUTH:
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    // Tạo sẵn 1 tài khoản trong RAM để test:
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails admin = User.builder()
            .username("admin")
            .password(encoder.encode("admin123"))
            .roles("ADMIN")
            .build();

        return new InMemoryUserDetailsManager(admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

---

## 💻 3. Cách 3: Tự Code Thủ Công (Bản Chất Bóc Tách Header)

Để hiểu rõ nhất máy chủ xử lý chuỗi `Authorization: Basic ...` như thế nào, hãy xem code JavaScript (Node.js/Express) tự viết:

```javascript
const express = require('express');
const app = express();

// Middleware tự bắt và kiểm tra Basic Auth
function basicAuthMiddleware(req, res, next) {
    // 1. Lấy header Authorization
    const authHeader = req.headers['authorization'];

    // Nếu không có header hoặc không bắt đầu bằng "Basic "
    if (!authHeader || !authHeader.startsWith('Basic ')) {
        // Trả về 401 kèm header thách thức WWW-Authenticate
        res.setHeader('WWW-Authenticate', 'Basic realm="Secure Area"');
        return res.status(401).send('Yêu cầu đăng nhập!');
    }

    // 2. Cắt bỏ chữ "Basic " lấy chuỗi Base64
    const base64Credentials = authHeader.split(' ')[1];
    
    // 3. Giải mã Base64 ra chuỗi "username:password"
    const decoded = Buffer.from(base64Credentials, 'base64').toString('ascii');
    const [username, password] = decoded.split(':');

    // 4. Kiểm tra tài khoản mật khẩu
    if (username === 'admin' && password === 'secret123') {
        return next(); // Đúng pass -> cho qua
    }

    // Sai pass -> từ chối
    res.setHeader('WWW-Authenticate', 'Basic realm="Secure Area"');
    return res.status(401).send('Sai tài khoản hoặc mật khẩu!');
}

// Áp dụng middleware vào route cần bảo vệ
app.get('/admin', basicAuthMiddleware, (req, res) => {
    res.send('Chào mừng Admin! Dữ liệu mật ở đây.');
});

app.listen(3000, () => console.log('Server chạy tại port 3000'));
```

---

## 🧪 4. Cách Test & Gọi API Có Basic Auth

### 1. Dùng lệnh `curl`
Lệnh `curl` có cờ `-u` hỗ trợ sẵn tự biến đổi username:password thành Base64:

```bash
# Cách 1: Dùng flag -u (curl tự encode Base64 cho bạn)
curl -v -u admin:admin123 http://localhost:8080/api/users

# Cách 2: Tự đính kèm Header Authorization chuẩn
# (echo -n "admin:admin123" | base64 -> YWRtaW46YWRtaW4xMjM=)
curl -v -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" http://localhost:8080/api/users
```

### 2. Dùng Postman
1. Mở request trong Postman.
2. Chuyển sang tab **Authorization**.
3. Chọn Type: **Basic Auth**.
4. Điền **Username** và **Password**. Postman sẽ tự động sinh header `Authorization: Basic ...` khi gửi đi.
