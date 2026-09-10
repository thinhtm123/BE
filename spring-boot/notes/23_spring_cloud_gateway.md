# 23. Spring Cloud Gateway

**Spring Cloud Gateway** là một thư viện cung cấp giải pháp **API Gateway** chuyên dụng cho hệ sinh thái Spring Cloud, được xây dựng trên nền tảng **Spring WebFlux** (Reactive, Non-blocking) và server **Netty**.

Nó đóng vai trò là điểm chạm duy nhất (Single Point of Entry) tiếp nhận toàn bộ request từ phía Client (Web, Mobile App, Third-party) và điều phối đến các Microservices nội bộ.

> [!NOTE]
> Trong các phiên bản Spring Cloud cũ, Netflix Zuul 1.x từng được sử dụng phổ biến. Tuy nhiên, Zuul 1.x hoạt động theo mô hình Blocking (1 thread / 1 request) nên đã bị loại bỏ và thay thế hoàn toàn bởi **Spring Cloud Gateway** (Non-blocking, hiệu năng cao gấp nhiều lần).

---

## 🏛️ 1. Ba Khái Niệm Cốt Lõi Trong Gateway

Để định tuyến một request, Spring Cloud Gateway dựa trên 3 thành phần cốt lõi:

```text
 Client Request ──> [ Predicate (Khớp điều kiện?) ] ──YES──> [ Pre-Filter ] ──> Microservice
                                                                    │
 Client Response <── [ Post-Filter ] <──────────────────────────────┘
```

1. **Route (Định tuyến):** 
   - Là một cấu hình hoàn chỉnh gồm: `id`, `uri` (đích đến), danh sách các `predicates` (điều kiện) và `filters` (bộ lọc).
2. **Predicate (Điều kiện khớp):**
   - Kiểm tra xem request có thỏa mãn điều kiện hay không (Dựa vào Path, HTTP Method, Query param, Header, v.v.).
   - Nếu điều kiện trả về `true`, route đó sẽ được chọn để xử lý.
3. **Filter (Bộ lọc):**
   - Cho phép can thiệp và sửa đổi HTTP Request trước khi chuyển tiếp (Pre-filter) hoặc can thiệp vào HTTP Response trước khi trả về cho Client (Post-filter).

---

## ⚙️ 2. Cấu Hình Routing Trong `application.yml`

Spring Cloud Gateway có thể cấu hình hoàn toàn qua file YAML mà không cần viết code Java phức tạp:

```yaml
server:
  port: 8080 # Cổng đón tiếp duy nhất cho toàn bộ hệ thống

spring:
  application:
    name: api-gateway
  cloud:
    gateway:
      routes:
        # Route 1: Điều hướng các request /api/orders/** sang Order Service
        - id: order-service-route
          uri: lb://order-service # lb:// = Load Balancing qua Eureka
          predicates:
            - Path=/api/orders/**
            - Method=GET,POST
          filters:
            - AddRequestHeader=X-Gateway-Source, MobileApp

        # Route 2: Điều hướng các request /api/payments/** sang Payment Service
        - id: payment-service-route
          uri: lb://payment-service
          predicates:
            - Path=/api/payments/**
```

> [!TIP]
> Tiền tố `lb://order-service` nghĩa là Gateway sẽ tự kết nối với Eureka Discovery Server, lấy danh sách IP của `order-service` và tự động cân bằng tải (Client-side Load Balancing) giữa các instance đang chạy.

---

## 🛡️ 3. Viết Custom Filter (Xác Thực Token / Logging)

Trong thực tế, Gateway thường được dùng để **kiểm tra Authentication (JWT Token)** trước khi cho request lọt vào bên trong mạng nội bộ.

Ví dụ tạo một `GlobalFilter` ghi log và kiểm tra Header `Authorization`:

```java
@Component
public class AuthenticationGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // 1. Logic Pre-Filter: Kiểm tra trước khi chuyển tiếp request
        System.out.println("Incoming Request URL: " + request.getURI().getPath());

        // Bỏ qua xác thực với các public endpoints như đăng nhập / đăng ký
        if (request.getURI().getPath().contains("/auth/login")) {
            return chain.filter(exchange);
        }

        // Kiểm tra xem request có gửi kèm Authorization header không
        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            // Không có -> Trả về lỗi 401 Unauthorized ngay tại Gateway
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        // Có thể bổ sung logic validate JWT token tại đây...

        // 2. Chuyển tiếp request đến microservice tương ứng
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            // 3. Logic Post-Filter: Chạy sau khi microservice phản hồi
            System.out.println("Response Status: " + exchange.getResponse().getStatusCode());
        }));
    }

    @Override
    public int getOrder() {
        return -1; // Độ ưu tiên cao nhất, chạy đầu tiên
    }
}
```

---

## 🌐 4. Xử Lý CORS Tập Trung Tại Gateway

Khi làm việc với Frontend (React, Vue, Angular), lỗi **CORS (Cross-Origin Resource Sharing)** là nỗi ám ảnh. Thay vì phải cấu hình CORS ở từng service con, bạn chỉ cần cấu hình duy nhất tại Gateway:

```yaml
spring:
  cloud:
    gateway:
      globalcors:
        cors-configurations:
          '[/**]':
            allowedOrigins: "http://localhost:3000" # URL Frontend
            allowedMethods:
              - GET
              - POST
              - PUT
              - DELETE
              - OPTIONS
            allowedHeaders: "*"
            allowCredentials: true
```

---

## ⚖️ 5. So Sánh: Spring Cloud Gateway vs Netflix Zuul 1.x

| Tiêu chí | Spring Cloud Gateway | Netflix Zuul 1.x (Legacy) |
| :--- | :--- | :--- |
| **Nền tảng** | Spring WebFlux, Project Reactor, Netty | Spring MVC, Servlet API, Tomcat/Jetty |
| **Kiểu xử lý (IO Model)** | **Non-blocking / Asynchronous** | **Blocking / Synchronous** |
| **Hiệu năng & Tài nguyên** | Cực cao, tiêu tốn ít RAM và Thread | Giảm mạnh khi số lượng request đồng thời lớn |
| **Hỗ trợ WebSocket** | Có hỗ trợ native | Cần cấu hình phức tạp |
| **Trạng thái hiện tại** | Chuẩn khuyến nghị chính thức của Spring | Đã bị Deprecated trong hệ sinh thái Spring |

---

## 💡 Best Practices & Bẫy Phỏng Vấn (Gotchas)

1. **Tuyệt đối không mang dependency `spring-boot-starter-web` vào Gateway:**
   - Spring Cloud Gateway chạy trên **Spring WebFlux**. Nếu bạn vô tình import thêm `spring-boot-starter-web` (Tomcat), ứng dụng sẽ bị xung đột thư viện và **báo lỗi crash ngay khi khởi động**:
   > *`Spring MVC found on classpath, which is incompatible with Spring Cloud Gateway.`*

2. **Xác thực (AuthN) tại Gateway vs Phân quyền (AuthZ) tại Service:**
   - **Mô hình chuẩn:** Gateway chỉ giải mã JWT để kiểm tra xem Token có hợp lệ và còn hạn hay không (Authentication). Sau đó trích xuất `userId`, `roles` gắn vào Header gửi tiếp vào trong.
   - Các Service con sẽ đọc Header đó và quyết định xem User có được thực hiện thao tác hay không (Authorization). Cách này giúp giảm tải cho Gateway, tránh biến Gateway thành nút thắt cổ chai (bottleneck).
