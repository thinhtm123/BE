# 25. Tổng Quan Cấu Trúc Dự Án Microservices Chuẩn (Project Structure)

Khi xây dựng một hệ thống Microservices bằng Spring Boot & Spring Cloud, cách tổ chức thư mục dự án sẽ hoàn toàn khác so với ứng dụng Monolith (đơn khối). 

Thông thường trong thực tế và khi học tập, kiến trúc **Multi-module Maven (Monorepo)** là giải pháp được ưa chuộng nhất vì dễ quản lý phiên bản thư viện dùng chung, dễ build và dễ chạy thử nghiệm.

---

## 🏗️ 1. Cấu Trúc Tổng Thể Multi-Module (Root Project)

Một hệ sinh thái Microservices hoàn chỉnh thường được chia thành 2 nhóm service chính:
1. **Infrastructure Services (Dịch vụ hạ tầng):** Gateway, Discovery (Eureka), Config Server.
2. **Business Services (Dịch vụ nghiệp vụ):** Order, Payment, Product, Inventory, User...

```text
my-microservices-project/             <── Root Project (Parent Maven)
│
├── pom.xml                           <── Quản lý version chung (Spring Boot, Spring Cloud)
│
├── discovery-server/                 <── [Port 8761] Máy chủ danh bạ Eureka
│   ├── src/main/java/.../DiscoveryServerApplication.java
│   ├── src/main/resources/application.yml
│   └── pom.xml
│
├── api-gateway/                      <── [Port 8080] Cửa ngõ API Gateway duy nhất
│   ├── src/main/java/.../ApiGatewayApplication.java
│   ├── src/main/java/.../filter/AuthenticationGlobalFilter.java
│   ├── src/main/resources/application.yml
│   └── pom.xml
│
├── config-server/                    <── [Port 8888] Quản lý cấu hình tập trung
│   ├── src/main/java/.../ConfigServerApplication.java
│   ├── src/main/resources/application.yml
│   └── pom.xml
│
├── order-service/                    <── [Port 8081] Nghiệp vụ Quản lý Đơn hàng
│   ├── (Cấu trúc chi tiết bên dưới)
│   └── pom.xml
│
├── payment-service/                  <── [Port 8082] Nghiệp vụ Thanh toán
│   ├── (Cấu trúc chi tiết bên dưới)
│   └── pom.xml
│
└── common-library/                   <── [Optional] Chứa DTO, Exception, Utils dùng chung
    ├── src/main/java/.../dto/PaymentRequestDTO.java
    ├── src/main/java/.../exception/GlobalExceptionHandler.java
    └── pom.xml
```

---

## 🔍 2. Cấu Trúc Chi Tiết Bên Trong Một Business Service (`order-service`)

Đây là cách bố trí các package bên trong một service nghiệp vụ độc lập có sử dụng **OpenFeign** và **Circuit Breaker**:

```text
order-service/
├── src/
│   ├── main/
│   │   ├── java/com/myproject/orderservice/
│   │   │   │
│   │   │   ├── OrderServiceApplication.java    <── Chứa @SpringBootApplication, @EnableFeignClients
│   │   │   │
│   │   │   ├── controller/                     <── Tiếp nhận HTTP Request từ API Gateway
│   │   │   │   └── OrderController.java
│   │   │   │
│   │   │   ├── service/                        <── Chứa Business Logic & Circuit Breaker
│   │   │   │   ├── OrderService.java           (Interface)
│   │   │   │   └── impl/OrderServiceImpl.java  (Gắn @CircuitBreaker, gọi Feign)
│   │   │   │
│   │   │   ├── client/                         <── Gọi API sang service khác (OpenFeign)
│   │   │   │   └── PaymentClient.java          (Interface gắn @FeignClient(name="payment-service"))
│   │   │   │
│   │   │   ├── repository/                     <── Tầng truy vấn Database riêng của Order
│   │   │   │   └── OrderRepository.java
│   │   │   │
│   │   │   ├── entity/                         <── JPA Entity ánh xạ bảng Database
│   │   │   │   └── OrderEntity.java
│   │   │   │
│   │   │   ├── dto/                            <── Data Transfer Object (Request/Response)
│   │   │   │   ├── CreateOrderRequest.java
│   │   │   │   └── OrderResponse.java
│   │   │   │
│   │   │   └── config/                         <── Cấu hình Bean, OpenAPI/Swagger...
│   │   │       └── FeignConfig.java
│   │   │
│   │   └── resources/
│   │       └── application.yml                 <── Cấu hình Eureka client, port, DB, Resilience4j
│   │
│   └── test/                                   <── Unit tests & Integration tests
│
└── pom.xml                                     <── Kế thừa từ Parent POM
```

---

## 🔌 3. Bảng Phân Bổ Cổng (Port Mapping) & Nhiệm Vụ

| Service | Port mặc định | Vai trò | Người gọi tới nó |
| :--- | :--- | :--- | :--- |
| **`discovery-server`** | `8761` | Eureka Registry: Quản lý danh bạ IP của mọi service | Mọi service khi khởi động |
| **`api-gateway`** | `8080` | Cổng duy nhất tiếp nhận request từ bên ngoài | Frontend, Mobile App |
| **`config-server`** | `8888` | Phân phối file `application.yml` từ kho Git | Mọi service khi khởi động |
| **`order-service`** | `8081` | Nghiệp vụ tạo & xem đơn hàng | API Gateway |
| **`payment-service`** | `8082` | Nghiệp vụ trừ tiền & hoàn tiền | `order-service` (qua Feign) |
| **`product-service`** | `8083` | Nghiệp vụ danh mục & tồn kho | API Gateway |

---

## 🚀 4. Thứ Tự Khởi Động Chuẩn (Startup Order)

> [!CAUTION]
> Một lỗi kinh điển của người mới bắt đầu làm Microservices là bật lung tung các service dẫn đến việc service bị văng Exception (Connection Refused) ngay khi vừa khởi động!

Thứ tự chuẩn khi khởi chạy toàn bộ hệ thống bằng tay trên IDE:

1. **Bước 1: Bật `config-server` (Port 8888)** *(Nếu có dùng)*: Đảm bảo nguồn cấp cấu hình hoạt động trước.
2. **Bước 2: Bật `discovery-server` (Eureka - Port 8761)**: Mở "danh bạ điện thoại" lên trước để các service khác có chỗ đăng ký.
3. **Bước 3: Bật các Business Services (`payment-service`, `order-service`...)**: Các service con sẽ khởi động và tự đăng ký tên của mình lên Eureka.
4. **Bước 4: Bật `api-gateway` (Port 8080)**: Gateway khởi động sau cùng, tải danh sách định tuyến từ Eureka và sẵn sàng tiếp nhận request từ Client.

---

## 💡 So Sánh: Monorepo vs Polyrepo (Khi Nào Dùng Gì?)

- **Monorepo (Tất cả modules nằm chung 1 Git Repository):**
  - *Ưu điểm:* Cực kỳ tiện cho cá nhân học tập hoặc team vừa/nhỏ. Dễ dàng sửa code ở 2 service cùng lúc và commit chung 1 PR.
  - *Nhược điểm:* Khi team lên đến hàng trăm người, Git repo sẽ trở nên rất nặng và dễ xung đột merge code.
- **Polyrepo (Mỗi Service là 1 Git Repository độc lập):**
  - *Ưu điểm:* Chuẩn lý thuyết Microservices thuần túy. Mỗi team quản lý 1 repo riêng, CI/CD riêng, deploy độc lập hoàn toàn không phụ thuộc ai.
  - *Nhược điểm:* Khó đồng bộ cấu hình, khó test luồng tích hợp end-to-end trên máy cục bộ.
