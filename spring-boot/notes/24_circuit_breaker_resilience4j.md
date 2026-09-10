# 24. Circuit Breaker Pattern & Resilience4j

Trong kiến trúc Microservices, các dịch vụ giao tiếp liên tục với nhau qua mạng (Network calls). Khi một dịch vụ gặp sự cố (chết server, nghẽn mạng, database quá tải), sự cố này có thể lan rộng và kéo sập toàn bộ hệ thống. Hiện tượng này gọi là **Cascading Failure (Sập dây chuyền)**.

**Circuit Breaker (Mẫu thiết kế cầu dao điện)** sinh ra để giải quyết triệt để vấn đề trên. Trong hệ sinh thái Spring Boot hiện nay, **Resilience4j** là thư viện chuẩn chính thức thay thế cho Netflix Hystrix (đã bị khai tử / deprecated).

---

## 💥 1. Vấn Đề: Cascading Failure (Sập Dây Chuyền) Là Gì?

Hãy xem kịch bản khi `Order Service` gọi sang `Payment Service`:

```text
[Client] ──> [Order Service] ──(Gọi HTTP)──> [Payment Service] (BỊ TREO / TIMEOUT 30s)
                   │
                   ├── Request 1 tới ──> Giữ 1 Thread đợi 30s
                   ├── Request 2 tới ──> Giữ 1 Thread đợi 30s
                   └── 1000 Request tới ──> HẾT SẠCH TOMCAT THREAD POOL!
                                                     │
                                                     ▼
                                        [Order Service SẬP THEO!]
```

- Mặc dù `Payment Service` bị lỗi, nhưng `Order Service` mới là thằng chịu trận vì bị **cạn kiệt tài nguyên (Thread exhaustion)** do phải chờ đợi timeout.
- Các tính năng khác của `Order Service` (như xem lịch sử đơn, hủy đơn) dù không liên quan đến thanh toán cũng bị tê liệt hoàn toàn.

---

## ⚡ 2. Cơ Chế Hoạt Động Của Circuit Breaker (3 Trạng Thái)

Circuit Breaker hoạt động chính xác như một chiếc cầu dao điện trong nhà thông qua một máy trạng thái (State Machine):

```text
             ┌────────────────────────────────────────────────────────┐
             │                                                        │
             ▼                                                        │ Tỷ lệ thành công
    ┌──────────────────┐    Tỷ lệ lỗi > Threshold    ┌──────────────┐ │ đạt yêu cầu
    │      CLOSED      │ ──────────────────────────> │     OPEN     │ │
    │ (Mạch đóng: OK)  │                             │ (Ngắt mạch)  │ │
    └──────────────────┘                             └──────┬───────┘ │
             ▲                                              │         │
             │                               Cho phép thử   │ Hết     │
             │                              vài request qua │ thời gian chờ
             │                                              ▼         │
             │                                      ┌───────────────┐ │
             └───────────────────────────────────── │   HALF-OPEN   │ ┘
                       Vẫn còn lỗi                  │ (Nửa mở: Test)│
                                                    └───────────────┘
```

1. **CLOSED (Đóng mạch - Trạng thái bình thường):**
   - Mọi request được chuyển tiếp đến service đích bình thường.
   - Circuit Breaker âm thầm theo dõi và tính tỷ lệ lỗi (Failure Rate) trong một cửa sổ trượt (Sliding Window, ví dụ: 10 request gần nhất).
2. **OPEN (Ngắt mạch - Có sự cố):**
   - Khi tỷ lệ lỗi vượt ngưỡng (ví dụ: > 50% request bị lỗi hoặc timeout), cầu dao **ngắt ngay lập tức**.
   - Mọi request tiếp theo gọi tới sẽ bị **từ chối ngay tức khắc (Fail-fast)**, KHÔNG gửi request qua mạng nữa, giúp service đích có thời gian phục hồi và giải phóng tài nguyên cho service gọi.
   - Lập tức kích hoạt logic dự phòng (**Fallback Method**).
3. **HALF-OPEN (Nửa mở - Thử nghiệm khôi phục):**
   - Sau một khoảng thời gian chờ cấu hình trước (ví dụ: 10 giây), cầu dao hé mở cho một số lượng request giới hạn (ví dụ: 3 request) đi qua để thăm dò:
     - Nếu cả 3 request đều **thành công**: Cầu dao đóng lại $\rightarrow$ Chuyển về **CLOSED**.
     - Nếu vẫn **thất bại**: Cầu dao tiếp tục ngắt $\rightarrow$ Quay lại trạng thái **OPEN** và bắt đầu đếm lại thời gian chờ.

---

## 💻 3. Hướng Dẫn Cài Đặt & Viết Code Với Spring Boot 3+

### Bước 1: Khai báo Dependency
Thêm starter của Resilience4j vào file `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

---

### Bước 2: Cấu hình `application.yml`

```yaml
resilience4j:
  circuitbreaker:
    instances:
      paymentServiceCB: # Tên định danh Circuit Breaker
        sliding-window-type: COUNT_BASED       # Đếm theo số lượng request
        sliding-window-size: 10                # Xét 10 request gần nhất
        minimum-number-of-calls: 5             # Tối thiểu 5 call mới bắt đầu tính %
        failure-rate-threshold: 50             # Lỗi >= 50% thì nhảy sang OPEN
        wait-duration-in-open-state: 10000ms   # Nằm ở trạng thái OPEN 10s rồi mới thử HALF-OPEN
        permitted-number-of-calls-in-half-open-state: 3 # Cho phép 3 request thử ở HALF-OPEN
        automatic-transition-from-open-to-half-open-enabled: true
```

---

### Bước 3: Sử dụng Annotation `@CircuitBreaker` & Viết `fallbackMethod`

```java
@Service
@RequiredArgsConstructor
public class OrderService {

    private final PaymentClient paymentClient;

    @CircuitBreaker(name = "paymentServiceCB", fallbackMethod = "paymentFallback")
    public String processOrderPayment(Long orderId, Double amount) {
        // Gọi HTTP sang Payment Service (có thể ném ra Exception nếu Payment sập)
        return paymentClient.callPaymentApi(orderId, amount);
    }

    /**
     * QUY TẮC CỦA HÀM FALLBACK:
     * 1. Cùng kiểu trả về với hàm chính (String)
     * 2. Danh sách tham số đầu vào phải giống hệt hàm chính (Long orderId, Double amount)
     * 3. BẮT BUỘC có thêm tham số cuối cùng là Throwable (hoặc Exception cụ thể)
     */
    public String paymentFallback(Long orderId, Double amount, Throwable throwable) {
        System.err.println("LỖI GỌI PAYMENT: " + throwable.getMessage());
        
        // Trả về dữ liệu dự phòng an toàn thay vì quăng lỗi 500 sập app
        return "Cổng thanh toán đang bảo trì. Đơn hàng " + orderId + " đã được ghi nhận ở trạng thái CHỜ XỬ LÝ.";
    }
}
```

---

## 🧰 4. Các "Vũ Khí" Khác Của Bộ Thư Viện Resilience4j

Ngoài Circuit Breaker, Resilience4j còn cung cấp một bộ công cụ phòng thủ toàn diện:

| Module | Annotation | Tác dụng |
| :--- | :--- | :--- |
| **Retry** | `@Retry` | Tự động gọi lại một số lần (ví dụ: thử lại 3 lần cách nhau 2s) khi gặp lỗi mạng tạm thời (Transient Fault). |
| **RateLimiter** | `@RateLimiter` | Giới hạn số lượng request tối đa trong một chu kỳ thời gian (ví dụ: tối đa 100 request / giây để chống DDOS / Spam). |
| **Bulkhead** | `@Bulkhead` | Chia nhỏ Thread Pool hoặc Semaphore cho từng service riêng để một service nghẽn không chiếm hết toàn bộ CPU / RAM của app. |
| **TimeLimiter** | `@TimeLimiter` | Giới hạn thời gian tối đa để xử lý một lời gọi (Timeout). |

Có thể kết hợp nhiều annotation trên cùng một hàm:
```java
@CircuitBreaker(name = "paymentCB", fallbackMethod = "fallback")
@Retry(name = "paymentRetry")
public String callApi() { ... }
```

---

## 💡 Best Practices & Bẫy Phỏng Vấn (Gotchas)

1. **Bẫy chữ ký của hàm Fallback (Signature Mismatch):**
   - Lỗi phổ biến nhất của người mới học là viết hàm fallback thiếu tham số `Throwable` ở cuối. Spring AOP sẽ không tìm thấy hàm và quăng lỗi:
   > `NoSuchMethodException: paymentFallback(Long, Double)`

2. **Bẫy gọi nội bộ trong cùng một Service (Self-Invocation):**
   - `@CircuitBreaker` hoạt động dựa trên cơ chế **Spring AOP Proxy**. Nếu method A gọi method B trong cùng 1 Class `this.methodB()`, AOP sẽ bị bỏ qua và Circuit Breaker **sẽ KHÔNG hoạt động**.

3. **Fallback chỉ nên xử lý nhẹ nhàng:**
   - Tuyệt đối không thực hiện các thao tác nặng (như gọi tiếp một API bên thứ ba khác) bên trong hàm fallback vì nếu thao tác đó lại treo, mục đích cứu hộ của Circuit Breaker sẽ hoàn toàn phá sản.
