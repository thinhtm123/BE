# 12. Spring Boot Starters

Spring Boot Starters là các gói phụ thuộc (Dependency Descriptors) gom sẵn các thư viện cần thiết theo từng chủ đề tính năng, giúp giảm thiểu tối đa code cấu hình và loại bỏ xung đột phiên bản.

---

## 🏷️ Quy Tắc Đặt Tên

- **Dự án chính thức của Spring:** `spring-boot-starter-*`
- **Dự án bên thứ ba (Third-party):** `*-spring-boot-starter`

---

## 📊 Danh Sách Starters Phổ Biến

### 1. Web & REST API
- `spring-boot-starter-web`: Tích hợp Spring MVC, Server Tomcat nhúng, Jackson JSON Parser.
- `spring-boot-starter-webflux`: Xây dựng ứng dụng Web bất đồng bộ (Reactive Web / Netty).

### 2. Data & Storage
- `spring-boot-starter-data-jpa`: Tích hợp Hibernate, Spring Data JPA, HikariCP Connection Pool.
- `spring-boot-starter-data-redis`: Tương tác với Redis In-memory Database.
- `spring-boot-starter-data-mongodb`: Tương tác với NoSQL MongoDB.

### 3. Security & Validation
- `spring-boot-starter-security`: Tích hợp Spring Security framework.
- `spring-boot-starter-oauth2-client`: Đăng nhập bằng Google/Facebook/GitHub.
- `spring-boot-starter-validation`: Kích hoạt Jakarta Validation (`@NotNull`, `@Email`, `@Valid`).

### 4. Testing & Operations
- `spring-boot-starter-test`: Gom sẵn JUnit 5, Mockito, AssertJ, Spring Test Context.
- `spring-boot-starter-actuator`: Endpoint giám sát tình trạng ứng dụng (Health, Metrics, Info).

---

## 💡 Quản Lý Phiên Bản Tự Động (BOM - Bill of Materials)

Spring Boot tự động quản lý version cho hơn 100+ thư viện phổ biến thông qua `spring-boot-dependencies` BOM.

```xml
<!-- Không cần ghi <version>1.2.3</version> -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

---

## 💡 Tips & Tricks

1. **Cách kiểm tra cây thư viện (Dependency Tree):**
   Chạy lệnh Maven trong terminal để xem chính xác Starter đó đã kéo những thư viện con nào về:
   ```bash
   mvn dependency:tree
   ```

2. **Loại bỏ (Exclude) thư viện con không muốn dùng:**
   Nếu muốn dùng Server Undertow thay vì Tomcat trong `spring-boot-starter-web`:
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
