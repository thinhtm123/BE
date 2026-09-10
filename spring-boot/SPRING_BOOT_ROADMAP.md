# 🚀 Spring Boot Master Roadmap & Study Notes

Tài liệu tổng hợp ghi chép toàn bộ kiến thức, khái niệm và ví dụ thực tế trong quá trình học Spring Boot.

---

## 📚 Danh Mục Bài Học (Table of Contents)

1. [01. Spring Core Fundamentals](./notes/01_spring_core_fundamentals.md)
   - Spring Core, IoC (Inversion of Control), DI (Dependency Injection), Beans
   - AOP (Aspect-Oriented Programming)
   - Spring Event Model
   - Data Access & Transaction Management (`@Transactional`)
   - Task Execution & Scheduling (`@Scheduled`, `@Async`)

2. [02. Spring Boot Architecture (Kiến trúc phân tầng)](./notes/02_spring_boot_architecture.md)
   - 4 Tầng: Presentation, Business, Persistence, Database
   - Luồng dữ liệu qua các tầng
   - Phân biệt Interface (`UserService`) & Implementation (`UserServiceImpl`)
   - Best Practices & Tips (Lombok, Java `record` DTO, Layer Validation)

3. [03. Why Spring Boot & Key Features](./notes/03_why_spring_boot.md)
   - Embedded Application Server (Server nhúng Tomcat)
   - Starters (`spring-boot-starter-*`)
   - Auto-Configuration (`@EnableAutoConfiguration`)
   - Executable Fat JAR
   - Production Monitoring (Spring Boot Actuator)

4. [04. Spring Core Configuration](./notes/04_spring_configuration.md)
   - 3 Phong cách cấu hình: XML vs Annotation vs Java-based (`@Configuration`)
   - So sánh chuyên sâu: `@Component` vs `@Bean`
   - Giải mã cơ chế `@Configuration` & CGLIB Proxying (Singleton guarantee)
   - Externalized Configuration (`application.yml`, `@Value`, `@ConfigurationProperties`)

5. [05. Spring MVC Architecture & REST APIs](./notes/05_spring_mvc.md)
   - Kiến trúc tổng thể & 6 linh kiện cốt lõi quanh `DispatcherServlet`
   - Cây kế thừa Servlet, vai trò Apache Tomcat & Vòng đời xử lý Request
   - So sánh `@Controller` vs `@RestController` & Data Binding
   - Bẫy phỏng vấn: Thread-safety trong Controller, Filter vs Interceptor

6. [06. Spring Boot Annotations Cheat Sheet](./notes/06_spring_boot_annotations.md)
   - Core & Boot Annotations (`@SpringBootApplication`, `@Configuration`, `@Bean`, `@Enable...`)
   - Stereotype Annotations (`@Component`, `@Controller`, `@RestController`, `@Service`, `@Repository`)
   - Dependency Injection & Property Binding (`@Autowired`, `@Qualifier`, `@Value`)
   - Web Mapping & Data Access Annotations (`@GetMapping`, `@PathVariable`, `@Transactional`)

7. [07. Spring Bean Scopes](./notes/07_spring_bean_scopes.md)
   - 6 Loại Scope: Singleton, Prototype, Request, Session, Application, WebSocket
   - Ví dụ thực tế cho Singleton, Prototype và Session Scope
   - Bẫy phỏng vấn: Scoped Proxy khi inject Request Bean vào Singleton Bean

8. [08. Spring Security & Authentication](./notes/08_spring_security_authentication.md)
   - Phân biệt Authentication (Bạn là ai?) vs Authorization (Bạn được làm gì?)
   - Kiến trúc Security Filter Chain đứng trước DispatcherServlet
   - Các hình thức Authentication: Form Login, JWT Token, OAuth2
   - Cấu hình SecurityFilterChain & BCryptPasswordEncoder trong Spring Security 6+

9. [09. Spring Security Authorization](./notes/09_spring_security_authorization.md)
   - URL Matching vs Method-Level Authorization
   - Biểu thức `@PreAuthorize`, `@PostAuthorize`, `@Secured` với SpEL
   - Phân biệt `Role` (`ROLE_ADMIN`) vs `Authority` (`USER_DELETE`)

10. [10. Spring Security OAuth2 & OpenID Connect](./notes/10_spring_security_oauth2.md)
   - Khái niệm OAuth2 & 4 nhân vật (Resource Owner, Client, Auth Server, Resource Server)
   - OAuth2 Client (`spring-boot-starter-oauth2-client` - Đăng nhập Google/GitHub)
   - OAuth2 Resource Server (`spring-boot-starter-oauth2-resource-server` - Xác thực JWT)

11. [11. JWT Authentication & SecurityContext](./notes/11_jwt_authentication.md)
   - Cấu trúc JWT (Header, Payload, Signature)
   - Tự viết `JwtAuthenticationFilter` với `OncePerRequestFilter`
   - Nạp thông tin User vào `SecurityContextHolder` & Đăng ký với `addFilterBefore`

12. [12. Spring Boot Starters](./notes/12_spring_boot_starters.md)
   - Khái niệm & Quy tắc đặt tên `spring-boot-starter-*` vs `*-spring-boot-starter`
   - Top các Starters phổ biến (Web, Data JPA, Security, Validation, Actuator)
   - Quản lý phiên bản tự động bằng Spring Boot BOM & Lệnh `mvn dependency:tree`

13. [13. Spring Boot Auto-configuration](./notes/13_spring_boot_autoconfiguration.md)
   - Khái niệm Auto-configuration & Các `@Conditional` Annotations (`@ConditionalOnClass`, `@ConditionalOnMissingBean`)
   - Cơ chế Override & Tắt cấu hình tự động bằng `exclude`
   - Kiểm tra báo cáo cấu hình bằng cờ `--debug`

14. [14. Spring Boot Actuator & Production Monitoring](./notes/14_spring_boot_actuator.md)
   - Giám sát ứng dụng Production qua các Endpoints (`/health`, `/metrics`, `/loggers`)
   - Cấu hình mở Endpoints trong `application.yml` & Cảnh báo bảo mật
   - Đổi Log Level thời gian thực & Tích hợp Bộ đôi Prometheus + Grafana

15. [15. Spring Boot Embedded Servers](./notes/15_embedded_servers.md)
   - So sánh Server Độc lập vs Server Nhúng (Fat JAR `java -jar app.jar`)
   - 3 Server Nhúng phổ biến: Tomcat (Mặc định), Jetty, Undertow
   - Cách đổi Server Nhúng qua Exclusions & Cấu hình `application.yml`

16. [16. Hibernate & ORM Framework](./notes/16_hibernate_orm.md)
   - Khái niệm ORM & So sánh JPA (Interface) vs Hibernate (Implementation)
   - Ánh xạ Entity, Khóa chính, Khóa ngoại (`@OneToMany`, `@ManyToOne`)
   - Cấu hình Hibernate DDL-Auto & Cảnh báo lỗi N+1 Query Problem

17. [17. Database Transactions & ACID Properties](./notes/17_database_transactions_acid.md)
   - Khái niệm Transaction & 4 tính chất ACID (Atomicity, Consistency, Isolation, Durability)
   - Các cấp độ cô lập Isolation Levels (`READ_COMMITTED`, `REPEATABLE_READ`, `SERIALIZABLE`)
   - Sử dụng `@Transactional` trong Spring Boot & Cản bẫy Rollback với Checked Exception

18. [18. Hibernate Entity Relationships & Cascading](./notes/18_hibernate_entity_relationships.md)
   - Mối quan hệ 4 loại: `@OneToOne`, `@ManyToOne`, `@OneToMany`, `@ManyToMany`
   - Lan truyền thao tác Cascading (`CascadeType.ALL`, `orphanRemoval`)
   - Phân biệt Fetching Strategy `FetchType.LAZY` vs `FetchType.EAGER`

19. [19. Hibernate Entity Lifecycle & Dirty Checking](./notes/19_hibernate_entity_lifecycle.md)
   - 4 Trạng thái Vòng đời: Transient, Persistent, Detached, Removed
   - Cơ chế Dirty Checking tự động bắn lệnh `UPDATE` khi Commit Transaction
   - Chuyển đổi trạng thái qua các hàm `persist`, `merge`, `detach`, `remove`

20. [20. Spring Data & Spring Data JPA](./notes/20_spring_data_jpa.md)
   - Cây kế thừa Repository (`CrudRepository` -> `PagingAndSortingRepository` -> `JpaRepository`)
   - Derived Query Methods (Tự động tạo SQL từ tên hàm `findBy...`)
   - 3 Cách viết Query (Derived, JPQL, Native Query) & Phân trang `Pageable`

21. [21. Microservices Architecture & Spring Cloud Overview](./notes/21_microservices_overview.md)
   - So sánh Monolith vs Microservices & Nguyên tắc Database-per-Service
   - Kiến trúc tổng thể và luồng đi của request
   - Bộ công cụ Spring Cloud: Gateway, Eureka Discovery, Config Server, OpenFeign, Resilience4j, Tracing
   - Best practices & Bài toán Distributed Transaction (Saga Pattern)

22. [22. Demo Thực Tế: Hiểu Nhanh Microservices & Spring Cloud qua Code](./notes/22_microservices_demo.md)
   - Trực quan hóa tương tác giữa Order Service và Payment Service.
   - Ứng dụng thực tế của Eureka Server (Danh bạ) và OpenFeign (Giao tiếp HTTP).

23. [23. Spring Cloud Gateway](./notes/23_spring_cloud_gateway.md)
   - Kiến trúc Reactive (WebFlux + Netty) và 3 khái niệm cốt lõi: Route, Predicate, Filter
   - Cấu hình Routing & Load Balancing với Eureka (`lb://service-name`)
   - Viết Custom GlobalFilter (Xác thực JWT Token, Logging)
   - Xử lý CORS tập trung & So sánh với Netflix Zuul 1.x (Blocking vs Non-blocking)

24. [24. Circuit Breaker Pattern & Resilience4j](./notes/24_circuit_breaker_resilience4j.md)
   - Vấn đề sập dây chuyền (Cascading Failure) & Giải pháp Cầu dao tự ngắt
   - 3 Trạng thái hoạt động: CLOSED, OPEN, HALF-OPEN
   - Cấu hình & Viết code với `@CircuitBreaker`, quy tắc hàm `fallbackMethod`
   - Bộ công cụ phòng thủ toàn diện: Retry, RateLimiter, Bulkhead, TimeLimiter

25. [25. Tổng Quan Cấu Trúc Dự Án Microservices Chuẩn](./notes/25_microservices_project_structure.md)
   - Cấu trúc Multi-module Maven (Monorepo) vs Polyrepo
   - Bố trí thư mục Infrastructure Services (Gateway, Eureka, Config) vs Business Services
   - Cấu trúc chi tiết các tầng bên trong một Business Service (`order-service`)
   - Bảng phân bổ Port & Thứ tự khởi động chuẩn (Startup Order)

26. [26. Spring Boot Testing: Unit Testing & Integration Testing](./notes/26_spring_boot_testing.md)
   - Kim tự tháp kiểm thử (Unit Test vs Slice Test vs Integration Test)
   - Bộ công cụ `spring-boot-starter-test`: JUnit 5, Mockito, AssertJ, MockMvc
   - Test 3 tầng: Controller (`@WebMvcTest`), Service (Mockito thuần), Repository (`@DataJpaTest`)
   - Integration Test toàn diện với `@SpringBootTest` & Best practices (Given-When-Then)

---

## 🎯 Hướng dẫn học & Tra cứu
- **Đọc lý thuyết:** Mở từng bài học trong thư mục `notes/`.
- **Thực hành:** Viết code ví dụ tương ứng trực tiếp trong thư mục dự án này.
- **Cập nhật:** Tiếp tục thêm các bài học tiếp theo (Spring Data JPA, REST APIs, Security, Microservices...) vào cây thư mục này.
