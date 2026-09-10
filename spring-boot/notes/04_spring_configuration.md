# 04. Spring Core Configuration

Configuration là "Bản thiết kế" hướng dẫn Spring Container tạo và quản lý các Bean.

---

## 📜 3 Phong Cách Cấu Hình

1. **XML Configuration (Legacy):** Cấu hình qua file `beans.xml` dài dòng, không Type-safe.
2. **Annotation-based Configuration:** Đánh dấu `@Component`, `@Service`, `@Repository` trực tiếp trên class (dùng Component Scanning).
3. **Java-based Configuration (Modern & Recommended):** Dùng class `@Configuration` và các method `@Bean`.

---

## ❓ `@Component` vs `@Bean`

| Tiêu chí | `@Component` (`@Service`, `@Repository`) | `@Bean` (trong `@Configuration`) |
| :--- | :--- | :--- |
| **Vị trí** | Đánh dấu trên **Class** | Đánh dấu trên **Method** |
| **Sở hữu Source** | Code **do bạn tự viết** (có thể mở file ra sửa được). | Code **thư viện bên thứ 3** (không thể mở file `.class` để gắn `@Component`). |
| **Khởi tạo** | Tự động qua Component Scan. | Viết hàm Java trả về object để Spring quản lý. |

---

## 🔍 Cơ Chế Bắt Lỗi & CGLIB Proxying trong `@Configuration`

Khi gắn `@Configuration` lên một Class:
1. Spring coi class đó là **Xưởng sản xuất Bean**.
2. Spring quét các method có gắn `@Bean`, gọi các method đó đúng 1 lần và cất đối tượng trả về vào **Kho lưu trữ (ApplicationContext)**.
3. Spring sử dụng **CGLIB Proxy** bọc class lại để đảm bảo tính **Singleton** (duy nhất).

```java
@Configuration
public class AppConfig {

    @Bean
    public ServiceA serviceA() {
        // Gọi hàm databaseConfig() ở bên dưới
        return new ServiceA(databaseConfig()); 
    }

    @Bean
    public ServiceB serviceB() {
        // Lại gọi hàm databaseConfig() lần 2
        return new ServiceB(databaseConfig()); 
    }

    @Bean
    public DatabaseConfig databaseConfig() {
        return new DatabaseConfig(); // Dòng này CHỈ CHẠY 1 LẦN duy nhất!
    }
}
```
*Lần gọi `databaseConfig()` thứ 2 ở `serviceB()` sẽ bị CGLIB Proxy chặn lại và lấy lại Object đã tạo từ Kho, không chạy lại lệnh `new DatabaseConfig()`.*

---

## ⚙️ Cấu Hình Ngoại Vi (Externalized Configuration)

### `@Value` (Lấy giá trị đơn)
```java
@Value("${app.jwt.secret}")
private String jwtSecret;
```

### `@ConfigurationProperties` (Type-safe Config gộp nhóm - Best Practice)
```java
@Configuration
@ConfigurationProperties(prefix = "app.jwt")
@Getter
@Setter
public class JwtProperties {
    private String secret;
    private long expirationMs;
}
```

---

## 💡 Tips & Tricks
1. Dùng **`application.yml`** thay vì `application.properties` để cấu hình phân cấp rõ ràng hơn.
2. Dùng **`@Profile("dev")` / `@Profile("prod")`** để chạy các Bean cấu hình riêng cho từng môi trường (Dev vs Production).
