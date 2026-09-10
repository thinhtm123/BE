# 22. Demo Thực Tế: Hiểu Nhanh Microservices & Spring Cloud qua Code

Để dễ hiểu "Microservices và Spring Cloud dùng để làm gì?", hãy tưởng tượng bạn đang xây dựng tính năng **Đặt Hàng (Shopee/Tiki)**.

Trong Microservices, chúng ta có 2 service độc lập hoàn toàn (2 project Spring Boot riêng biệt, chạy ở 2 cổng khác nhau, có 2 DB riêng):
1. **`order-service`** (Chạy ở port `8080`): Xử lý lưu đơn hàng.
2. **`payment-service`** (Chạy ở port `8081`): Xử lý trừ tiền tài khoản.

Vấn đề: Khi user bấm "Đặt hàng", **`order-service`** làm sao gọi được **`payment-service`** để trừ tiền? 

Dưới đây là cách Spring Cloud giải quyết bằng code!

---

## Bước 1: Khởi động "Danh Bạ Điện Thoại" (Eureka Server)
Giống như trang vàng danh bạ, đây là nơi các service báo cáo danh tính của mình.
- Bạn tạo 1 project tên là `eureka-server`.
- Chỉ cần gắn 1 Annotation:

```java
@SpringBootApplication
@EnableEurekaServer // <--- Biến project này thành Danh Bạ
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

---

## Bước 2: Payment Service (Bên bị gọi)
Dịch vụ thanh toán (chạy port `8081`) viết một API bình thường để trừ tiền.

```java
@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    
    @PostMapping("/process")
    public String processPayment(@RequestParam Long orderId, @RequestParam Double amount) {
        // Logic trừ tiền trong Payment Database
        return "Thanh toán thành công cho đơn " + orderId;
    }
}
```

Để `order-service` tìm thấy nó, `payment-service` tự "đăng ký" vào danh bạ (Eureka) thông qua file `application.yml`:
```yaml
spring:
  application:
    name: payment-service # Tên định danh trong danh bạ
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/ # Địa chỉ của Eureka Server
```

---

## Bước 3: Order Service (Bên gọi) nhờ Spring Cloud Feign
Làm sao `order-service` (port `8080`) gọi API của `payment-service` (port `8081`)? 
Thay vì dùng `RestTemplate` tự viết URL (`http://localhost:8081/api/payments/process`) rất thủ công và dễ gãy (nếu IP đổi), Spring Cloud cung cấp **OpenFeign**.

Bạn chỉ cần tạo một `Interface` như thế này ở `order-service`:

```java
@FeignClient(name = "payment-service") // <--- Nhờ Eureka tìm IP của payment-service hộ!
public interface PaymentClient {

    @PostMapping("/api/payments/process")
    String processPayment(@RequestParam("orderId") Long orderId, 
                          @RequestParam("amount") Double amount);
}
```

Sau đó ở class `OrderService` (chứa logic tạo đơn), bạn gọi nó hệt như gọi một class local:

```java
@Service
@RequiredArgsConstructor
public class OrderService {

    private final PaymentClient paymentClient; // Inject Feign Client
    private final OrderRepository orderRepository;

    public String createOrder(Long orderId, Double amount) {
        // 1. Lưu đơn hàng vào Database của Order (Trạng thái: PENDING)
        Order order = new Order(orderId, amount, "PENDING");
        orderRepository.save(order);

        // 2. Gọi qua Payment Service để trừ tiền (Bằng HTTP ẩn dưới nền)
        // Spring Cloud tự hỏi Eureka: "Ê, payment-service đang ở IP nào thế?" rồi tự bắn Request tới đó!
        String paymentResponse = paymentClient.processPayment(orderId, amount);

        // 3. Nếu thành công, đổi trạng thái
        order.setStatus("SUCCESS");
        orderRepository.save(order);

        return "Đặt hàng xong: " + paymentResponse;
    }
}
```

---

## 🎯 Tổng kết: "Cái này để làm gì?"

Nếu bạn code kiểu cũ (Monolith), `OrderService` và `PaymentService` nằm chung 1 cục code, bạn chỉ việc gọi hàm `paymentService.process()`. 
Nhưng vì chia làm 2 cục **chạy độc lập ở 2 server khác nhau**, bạn phải giao tiếp qua mạng (HTTP).

**Spring Cloud ra đời để:**
1. **Eureka:** Cứu bạn khỏi việc phải nhớ IP của nhau. `Order` không cần biết `Payment` ở IP nào, chỉ cần gọi tên `"payment-service"`. Nhỡ `Payment` đổi server, Eureka tự cập nhật!
2. **OpenFeign:** Giúp bạn gọi API của thằng khác mà code trông gọn gàng như đang gọi hàm Java thông thường, không phải tự hì hục build HTTP Client, parse JSON.
3. **Gateway:** Nếu App Mobile muốn gọi 2 thằng này, nó không cần biết 2 port 8080 và 8081. Nó gọi Gateway ở port 80, Gateway sẽ tự chuyển hướng.
