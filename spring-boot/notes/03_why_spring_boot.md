# 03. Why Spring Boot & Key Features

Triết lý thiết kế của Spring Boot: **"Convention over Configuration" (Quy ước hơn Cấu hình)**.

---

## 🔑 5 Tính Năng Đột Phá

### 1. Embedded Application Server (Server nhúng)
- Tích hợp sẵn Web Server (Tomcat/Jetty/Netty) bên trong ứng dụng.
- Không cần cài Tomcat ngoài hay deploy file `.war`.
- Chạy trực tiếp bằng câu lệnh: `java -jar app.jar`. Rất phù hợp cho Docker/K8s.

### 2. Pre-configured Starters (`spring-boot-starter-*`)
- Gộp hàng chục dependencies liên quan thành 1 starter duy nhất.
- Ví dụ: `spring-boot-starter-web` tự động kéo về Spring MVC, Tomcat, Jackson JSON, Validation, Logging.

### 3. Automatic Configuration (`@EnableAutoConfiguration`)
- Tự động kiểm tra Classpath để cấu hình các Bean chuẩn.
- Ví dụ: Thấy driver MySQL ➔ Tự tạo `DataSource`.
- Dễ dàng ghi đè cấu hình qua `application.yml`.

### 4. Executable Fat JAR
- Đóng gói toàn bộ source code + thư viện bên thứ 3 + Tomcat Server nhúng vào **duy nhất 1 file `.jar`**.

### 5. Production-Ready Monitoring (Spring Boot Actuator)
- Thêm `spring-boot-starter-actuator` để có sẵn các endpoint giám sát:
  - `/actuator/health`: Kiểm tra sức khỏe hệ thống, DB connection.
  - `/actuator/metrics`: Đo CPU, RAM, HTTP request rate.
  - `/actuator/prometheus`: Tích hợp Prometheus + Grafana.

---

## 💡 Tips & Tricks
1. **Spring Initializr ([start.spring.io](https://start.spring.io)):** Trang web chuẩn để tạo nhanh dự án Spring Boot.
2. **Kiểm tra Auto-Config:** Thêm `--debug` khi chạy (`java -jar app.jar --debug`) để xem báo cáo chi tiết các Bean nào được tự động cấu hình.
