# 21. Microservices Architecture & Spring Cloud Overview

Microservices là một phong cách kiến trúc phần mềm trong đó một ứng dụng lớn và phức tạp được chia tách thành một tập hợp các dịch vụ nhỏ (small), hoạt động độc lập và có thể triển khai riêng biệt (independently deployable). 

Mỗi microservice đảm nhận một nghiệp vụ chuyên biệt duy nhất (Single Responsibility) và giao tiếp với các service khác thông qua các giao thức mạng chuẩn như HTTP/REST API, gRPC hoặc Message Queue.

Trong hệ sinh thái Java, **Spring Boot** đóng vai trò tạo dựng từng service riêng lẻ, kết hợp với **Spring Cloud** để cung cấp các giải pháp kết nối, cấu hình, quản lý và điều phối toàn bộ hệ thống microservices.

---

## ⚖️ 1. So Sánh: Monolithic vs Microservices

| Tiêu chí | Monolithic Architecture (Đơn khối) | Microservices Architecture (Vi dịch vụ) |
| :--- | :--- | :--- |
| **Cấu trúc** | Tất cả module nằm chung 1 codebase, 1 artifact (WAR/JAR) | Chia thành nhiều project nhỏ độc lập, mỗi service có repo/build riêng |
| **Database** | Dùng chung 1 cơ sở dữ liệu (Shared Database) | Mỗi service sở hữu database riêng (**Database-per-Service**) |
| **Deploy** | Deploy toàn bộ hệ thống, downtime ảnh hưởng toàn app | Deploy độc lập từng service, không làm gián đoạn các service khác |
| **Khả năng Scale** | Scale toàn bộ ứng dụng (kể cả module ít dùng) | Scale linh hoạt chỉ riêng service chịu tải cao (vd: `PaymentService`) |
| **Khả năng chịu lỗi** | Một module bị crash (out of memory) có thể kéo sập toàn app | Lỗi được cô lập (Fault Isolation), các service khác vẫn chạy bình thường |
| **Độ phức tạp** | Đơn giản khi bắt đầu, khó bảo trì khi dự án phình to | Phức tạp trong vận hành, mạng (network latency), đồng bộ dữ liệu |

---

## 🗺️ 2. Mô Hình Kiến Trúc Microservices Chuẩn (Architecture Diagram)

```text
               ┌───────────────────────┐
               │    Client (Web/App)   │
               └──────────┬────────────┘
                          │ (HTTPS Requests)
                          ▼
            ┌───────────────────────────┐
            │   API Gateway (Routing)   │◄────┐
            │   (Spring Cloud Gateway)  │     │
            └─────────────┬─────────────┘     │
                          │                   │ (Tra cứu địa chỉ)
     ┌────────────────────┼───────────────────┼────────────────────┐
     │                    │                   │                    │
     ▼                    ▼                   ▼                    │
┌──────────────┐   ┌──────────────┐   ┌──────────────┐             │
│ User Service │   │ Order Service│   │Payment Service              │
│ (Spring Boot)│   │ (Spring Boot)│   │ (Spring Boot)│             │
└──────┬───────┘   └──────┬───────┘   └──────┬───────┘             │
       │                  │                  │                     │
       ▼                  ▼                  ▼                     │
┌──────────────┐   ┌──────────────┐   ┌──────────────┐             │
│   User DB    │   │   Order DB   │   │  Payment DB  │             │
└──────────────┘   └──────────────┘   └──────────────┘             │
                                                                   │
                                                                   │
┌───────────────────────────┐         ┌────────────────────────────┴─┐
│ Spring Cloud Config Server│         │ Service Discovery (Eureka)   │
│  (Quản lý file cấu hình)  │         │ (Đăng ký & Khám phá dịch vụ) │
└───────────────────────────┘         └──────────────────────────────┘
```

---

## 🧩 3. Các Thành Phần Cốt Lõi Trong Spring Cloud

> **Spring Cloud** is a collection of libraries and tools for building cloud-native applications using the Spring Framework. It provides a set of abstractions and implementations for common patterns and best practices used in cloud-based applications, such as service discovery, configuration management, and circuit breaker patterns, among others.

### 1. API Gateway (`Spring Cloud Gateway`)
- Đóng vai trò là "cửa ngõ" duy nhất tiếp nhận request từ bên ngoài vào hệ thống.
- Chức năng: Routing (điều hướng request đến đúng microservice), Rate Limiting (giới hạn tần suất gọi), Authentication/Authorization tập trung, SSL termination, ghi log & tracking.

### 2. Service Discovery & Registry (`Netflix Eureka` / `HashiCorp Consul`)
- Đóng vai trò như một "danh bạ điện thoại" tự động cập nhật.
- Mỗi microservice khi khởi động sẽ đăng ký IP và Port của mình lên Discovery Server (Eureka Server).
- Khi `Order-Service` muốn gọi `Payment-Service`, nó chỉ cần hỏi Eureka để lấy danh sách IP khả dụng mà không cần hardcode địa chỉ máy chủ.

### 3. Centralized Configuration (`Spring Cloud Config`)
- Quản lý cấu hình (`application.yml`) tập trung từ Git Repository hoặc Vault cho tất cả các service.
- Hỗ trợ đổi cấu hình runtime bằng Spring Cloud Bus mà không cần rebuild hoặc restart lại service.

### 4. Inter-Service Communication (Giao tiếp giữa các service)
- **Đồng bộ (Synchronous):** 
  - **Spring Cloud OpenFeign:** Khai báo interface declarative cực kỳ ngắn gọn, Spring tự động tạo code gọi HTTP REST API:
    ```java
    @FeignClient(name = "user-service")
    public interface UserClient {
        @GetMapping("/api/v1/users/{id}")
        UserDTO getUserById(@PathVariable("id") Long id);
    }
    ```
  - **Spring WebClient:** Reactive HTTP Client phi block (Non-blocking).
- **Bất đồng bộ (Asynchronous / Event-driven):**
  - **Spring Cloud Stream:** Tích hợp Apache Kafka hoặc RabbitMQ để phát/nhận sự kiện (Domain Events), giúp hệ thống loosely coupled (giảm phụ thuộc chặt).

### 5. Circuit Breaker & Resilience (`Resilience4j`)
- Ngăn chặn lỗi dây chuyền (Cascading Failure). Nếu `Payment-Service` bị sập hoặc quá tải, Circuit Breaker sẽ "ngắt mạch" (Open State) và trả về dữ liệu fallback thay vì để `Order-Service` treo timeout và sập theo.

### 6. Distributed Tracing & Observability (`Micrometer Tracing` + `Zipkin` / `Grafana Tempo`)
- Khi một request đi qua Gateway -> Service A -> Service B -> Service C, rất khó biết lỗi hoặc nghẽn ở đâu.
- Tracing gắn mỗi request một `Trace ID` và `Span ID` duy nhất để vẽ lại toàn bộ luồng đi và thời gian xử lý qua từng service.

---

## 💡 Best Practices & Bẫy Thường Gặp (Gotchas)

1. **Nguyên tắc "Database-per-Service":**
   - Tuyệt đối không cho phép Service A kết nối thẳng vào database của Service B. Muốn lấy dữ liệu, Service A bắt buộc phải gọi API hoặc lắng nghe Event từ Service B.

2. **Bài toán Distributed Transaction (Giao dịch phân tán):**
   - `@Transactional` chỉ có tác dụng trên 1 database duy nhất. Trong microservices, dữ liệu nằm ở nhiều database khác nhau nên không thể dùng ACID thông thường.
   - Giải pháp: Sử dụng mẫu thiết kế **Saga Pattern** (Choreography hoặc Orchestration) kết hợp bù trừ giao dịch (Compensating Transaction) để đảm bảo tính nhất quán sau cùng (Eventual Consistency).

3. **Đừng vội chia nhỏ khi chưa cần thiết (Microservices Premium):**
   - Microservices giải quyết bài toán scale tổ chức và hệ thống lớn, nhưng đánh đổi bằng chi phí vận hành (DevOps, CI/CD, Network latency, Monitoring) rất cao. Luôn bắt đầu từ **Modular Monolith** sạch sẽ trước khi bóc tách ra Microservices.
