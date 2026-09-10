# 06. Spring Boot Annotations Cheat Sheet

Annotations là trái tim của Spring Boot, giúp loại bỏ các file cấu hình XML dài dòng và kích hoạt các tính năng của ứng dụng một cách declarative (khai báo).

---

## 🚀 1. Core & Boot Annotations (Khởi tạo & Cấu hình)

- **`@SpringBootApplication`**: Annotation quan trọng nhất nằm ở main class (`@SpringBootConfiguration` + `@EnableAutoConfiguration` + `@ComponentScan`).
- **`@Configuration`**: Đánh dấu class là "Xưởng chế tạo Bean".
- **`@Bean`**: Đặt trên phương thức để tạo và đăng ký một Bean (thường dùng cho thư viện ngoài).
- **Các `@Enable...` Annotation:** Bật các tính năng mở rộng (`@EnableScheduling`, `@EnableAsync`, `@EnableJpaRepositories`).

### 📌 Ví dụ thực tế:
```java
@SpringBootApplication // Main entry point + ComponentScan + AutoConfig
@EnableScheduling      // Bật tính năng chạy task định kỳ
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

@Configuration // Xưởng tạo Bean
public class AppConfig {

    @Bean // Tạo Bean cho thư viện RestTemplate của Spring
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

---

## 🏛️ 2. Stereotype Annotations (Đăng ký Bean theo vai trò)

- **`@Component`**: Đánh dấu một class thông thường là một Spring Bean.
- **`@Controller`**: Đánh dấu tầng Presentation xử lý giao diện Web (trả về HTML).
- **`@RestController`**: Đánh dấu tầng Presentation xử lý RESTful API (trả về JSON).
- **`@Service`**: Đánh dấu tầng Business Layer chứa logic nghiệp vụ.
- **`@Repository`**: Đánh dấu tầng Persistence Layer thao tác với Database.

### 📌 Ví dụ thực tế:
```java
@Repository // Tầng Persistence
public class UserRepository { ... }

@Service // Tầng Business Logic
public class UserService { ... }

@RestController // Tầng Presentation REST API
public class UserController { ... }

@Component // Helper / Utility class thông thường
public class JwtTokenHelper { ... }
```

---

## 💉 3. Dependency Injection & Property Binding

- **`@Autowired`**: Tiêm (inject) Bean phụ thuộc vào.
- **`@Qualifier("beanName")`**: Chỉ định rõ tên Bean muốn inject khi có nhiều Bean cùng kiểu.
- **`@Primary`**: Đánh dấu Bean ưu tiên mặc định.
- **`@Value("${config.key}")`**: Đọc giá trị đơn lẻ từ file `application.yml`.
- **`@ConfigurationProperties(prefix = "app")`**: Map nhóm thuộc tính từ YML vào Java Object.

### 📌 Ví dụ thực tế:
```java
@Service
public class OrderService {

    @Value("${app.shipping.default-fee:30000}") // Đọc giá trị từ YML, mặc định 30000
    private double shippingFee;

    private final PaymentService paymentService;

    // Inject đích danh PaypalService nếu có nhiều class implement PaymentService
    public OrderService(@Qualifier("paypalPaymentService") PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
```

---

## 🌐 4. Web & Request Mapping Annotations (Spring MVC)

- **HTTP Mappings:** `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`, `@PatchMapping`.
- **`@PathVariable`**: Lấy biến từ đường dẫn URL (`/users/{id}`).
- **`@RequestParam`**: Lấy query params (`/users?page=1`).
- **`@RequestBody`**: Map dữ liệu JSON body từ Request thành Java Object.
- **`@ResponseStatus`**: Đặt HTTP Status code trả về.

### 📌 Ví dụ thực tế:
```java
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // GET /api/v1/users/10?lang=vi
    @GetMapping("/{id}")
    public UserDTO getUserById(
            @PathVariable("id") Long userId,
            @RequestParam(defaultValue = "en") String lang) {
        return userService.findById(userId);
    }

    // POST /api/v1/users (Body: {"fullName": "Nguyen A", ...})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Trả về HTTP 201 Created
    public UserDTO createUser(@Valid @RequestBody CreateUserRequestDTO request) {
        return userService.createUser(request);
    }
}
```

---

## 💾 5. Data Access & Transactions

- **`@Transactional`**: Quản lý giao dịch DB tự động.
- **`@Entity`**, **`@Table`**, **`@Id`**, **`@GeneratedValue`**: Mapping Java Class với Bảng Database (JPA).

### 📌 Ví dụ thực tế:
```java
@Entity
@Table(name = "products")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name", nullable = false)
    private String name;
}

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional // Nếu ném Exception, thao tác lưu DB sẽ tự động rollback
    public void updateStock(Long productId, int newQuantity) {
        ProductEntity product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        
        product.setQuantity(newQuantity);
        productRepository.save(product);
    }
}
```

---

## 💡 Tips & Tricks Thực Tế Khi Sử Dụng Annotations

1. **Tránh Field Injection (`@Autowired` trên Field):**
   - ❌ **Không nên:** `@Autowired private UserService userService;` (gây khó khăn cho Unit Test, bị IDE cảnh báo Code Smell).
   - ✅ **Nên dùng:** Constructor Injection kết hợp Lombok `@RequiredArgsConstructor`.

2. **Khai báo Giá trị Mặc định cho `@Value`:**
   - Thêm dấu `:` đằng sau tên thuộc tính: `@Value("${app.jwt.secret:default_secret_key_123}")`.
   - Nếu trong file `application.yml` quên không khai báo key, ứng dụng vẫn chạy bình thường với giá trị mặc định đằng sau mà không bị sập app.

3. **Cạm bẫy với `@Transactional` (Self-Invocation Trap):**
   - `@Transactional` chỉ có hiệu lực khi được gọi **từ ngoài class vào** (thông qua Spring AOP Proxy).
   - ❌ Nếu trong cùng 1 Class, `methodA()` (không có `@Transactional`) gọi trực tiếp `methodB()` (có `@Transactional`), thì `@Transactional` của `methodB` **hoàn toàn KHÔNG có tác dụng**!
   - `@Transactional` bắt buộc phải đặt trên phương thức `public`.

4. **Tránh nhầm lẫn giữa `@PathVariable` và `@RequestParam`:**
   - Dùng `@PathVariable` cho **nhận dạng tài nguyên cố định**: `/api/users/123`.
   - Dùng `@RequestParam` cho **lọc, tìm kiếm, phân trang**: `/api/users?page=1&limit=10&search=java`.

5. **Đừng quên `@Valid` khi nhận `@RequestBody`:**
   - Muốn các annotation kiểm tra như `@NotNull`, `@Email`, `@Size` trong DTO có hiệu lực, bạn bắt buộc phải thêm chữ `@Valid` đằng trước `@RequestBody`:
     ```java
     public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserDTO request)
     ```
