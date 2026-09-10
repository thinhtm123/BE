# 15. Spring Boot Embedded Servers

Embedded Server (Server Nhúng) là tính năng cho phép nhúng trực tiếp Servlet Container (như Tomcat, Jetty, Undertow) vào bên trong tập tin thực thi `.jar` của ứng dụng Spring Boot.

---

## 🌟 Lợi Ích Của Server Nhúng

1. **Đơn giản hóa Deployment:** Không cần tải, cài đặt và cấu hình phần mềm Tomcat độc lập trên server.
2. **Fat JAR Self-Contained:** Toàn bộ ứng dụng (code + thư viện + web server) đóng gói trong **duy nhất 1 file `.jar`**.
3. **Chạy ứng dụng bằng 1 dòng lệnh:**
   ```bash
   java -jar my-app-1.0.0.jar
   ```
4. **Cực kỳ tối ưu cho Containerization:** Giúp tạo Docker Container siêu nhẹ và nhanh chóng.

---

## 📊 3 Server Nhúng Phổ Biến

- **Tomcat (Mặc định trong `spring-boot-starter-web`):** Chuẩn mực phổ biến và ổn định nhất.
- **Jetty:** Nhẹ, tốn ít bộ nhớ RAM.
- **Undertow:** Hiệu năng xử lý I/O cao, thích hợp hệ thống tải lớn.
- **Netty:** Dùng cho Reactive Programming (`spring-boot-starter-webflux`).

---

## ⚙️ Cấu Hình Đổi Port & Server Nhúng (`application.yml`)

```yaml
server:
  port: 9090 # Đổi port chạy của server nhúng
  servlet:
    context-path: /api # Đổi prefix URL toàn bộ ứng dụng
  tomcat:
    max-threads: 200    # Cấu hình tối đa 200 thread xử lý đồng thời
    accept-count: 100   # Hàng đợi chờ request khi max thread
```

---

## 🛠️ Cách Đổi Server Nhúng Trong `pom.xml`

Loại bỏ `spring-boot-starter-tomcat` và thêm `spring-boot-starter-undertow`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-undertow</artifactId>
</dependency>
```
