# 14. Spring Boot Actuator & Production Monitoring

Spring Boot Actuator cung cấp các tính năng sẵn sàng cho Production (Production-Ready) giúp bạn giám sát (monitoring) và quản lý (management) tình trạng của ứng dụng đang chạy thông qua các HTTP REST Endpoints.

---

## 📊 Danh Sách Các Endpoints Quan Trọng

1. `/actuator/health`: Kiểm tra trạng thái ứng dụng (`UP`/`DOWN`), dung lượng đĩa cứng, kết nối DB, Redis.
2. `/actuator/metrics`: Xem chỉ số hiệu năng (CPU, RAM, JVM Garbage Collection, số lượng HTTP request).
3. `/actuator/env`: Xem các cấu hình môi trường đang nạp.
4. `/actuator/beans`: Danh sách toàn bộ Spring Beans đang được quản lý.
5. `/actuator/loggers`: Xem và **thay đổi Log Level (TRACE/DEBUG/INFO/ERROR) thời gian thực mà không cần restart server**.
6. `/actuator/prometheus`: Định dạng dữ liệu metrics theo chuẩn Prometheus để vẽ biểu đồ Dashboard trên Grafana.

---

## ⚙️ Cấu Hình Cơ Bản (`application.yml`)

```yaml
management:
  endpoints:
    web:
      exposure:
        include: "health,metrics,info,loggers" # Chỉ mở các endpoint an toàn
  endpoint:
    health:
      show-details: always # Hiển thị chi tiết kết nối DB, Disk Space
```

---

## 🔒 Bảo Mật Actuator Endpoints Với Spring Security

Luôn chặn các endpoint Actuator để tránh rò rỉ dữ liệu nhạy cảm:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health").permitAll() // Cho phép công khai Health Check
                .requestMatchers("/actuator/**").hasRole("ADMIN") // Chỉ ADMIN mới xem được metrics/env/beans
                .anyRequest().authenticated()
            );
        return http.build();
    }
}
```

---

## 💡 Tips & Tricks Thực Tế

1. **Thay đổi Log Level tức thì không cần Restart:**
   Gửi 1 request `POST /actuator/loggers/com.example.service` với Body `{"configuredLevel": "DEBUG"}` để bật log Debug ngay khi đang điều tra lỗi trên Production!

2. **Tích hợp Monitoring Stack chuẩn doanh nghiệp:**
   `Spring Boot Actuator` ➔ `Prometheus` (Thu thập dữ liệu định kỳ) ➔ `Grafana` (Vẽ biểu đồ đẹp mắt & Báo động qua Telegram/Slack khi server sập).
