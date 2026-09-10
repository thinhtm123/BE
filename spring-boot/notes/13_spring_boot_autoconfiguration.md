# 13. Spring Boot Auto-configuration

Auto-configuration là tính năng cốt lõi giúp Spring Boot tự động khởi tạo và cấu hình các Bean dựa trên các thư viện có mặt trong classpath và các thuộc tính khai báo trong file `application.yml`.

---

## 🔍 Cơ Chế Hoạt Động

Khi ứng dụng bật lên, `@EnableAutoConfiguration` (nằm bên trong `@SpringBootApplication`) sẽ quét danh sách các class cấu hình tự động được định nghĩa tại:
`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`

---

## 🔑 Các `@Conditional` Annotations Quan Trọng

Spring Boot dùng các annotation điều kiện để quyết định có nên nạp 1 Bean tự động hay không:

1. **`@ConditionalOnClass`:** Kích hoạt nếu class chỉ định có trong classpath.
2. **`@ConditionalOnMissingBean`:** Chỉ tạo Bean nếu lập trình viên CHƯA tự khai báo Bean tương tự (Cho phép Override).
3. **`@ConditionalOnProperty`:** Kích hoạt dựa trên giá trị của thuộc tính trong `application.yml`.
4. **`@ConditionalOnWebApplication`:** Chỉ kích hoạt nếu ứng dụng là một Web Application.

---

## 📌 Ví dụ: Cách Spring Boot Tự Cấu Hình DataSource (Đơn giản hóa)

```java
@AutoConfiguration
@ConditionalOnClass({ DataSource.class, EmbeddedDatabaseType.class })
public class DataSourceAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean // Nếu bạn đã tự tạo Bean DataSource, hàm này sẽ BỊ BỎ QUA!
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder().build();
    }
}
```

---

## 🛠️ Tùy Chỉnh & Tắt Auto-configuration

### 1. Tắt hẳn một Auto-configuration cụ thể
```java
@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class,
    SecurityAutoConfiguration.class
})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### 2. Xem danh sách các Auto-configuration đã chạy (Debug Mode)
Thêm flag `--debug` khi chạy app để xem báo cáo chi tiết **Conditions Evaluation Report**:
```bash
java -jar app.jar --debug
```
Màn hình console sẽ in ra danh sách:
- **Positive matches:** Các cấu hình tự động ĐÃ ĐƯỢC BẬT và lý do tại sao.
- **Negative matches:** Các cấu hình tự động BỊ BỎ QUA và lý do tại sao.
