# 26. Spring Boot Testing: Unit Testing & Integration Testing

Spring Framework và Spring Boot cung cấp một hệ sinh thái kiểm thử (Testing) vô cùng phong phú và mạnh mẽ thông qua thư viện `spring-boot-starter-test`. 

Nó cho phép lập trình viên kiểm thử từng tầng riêng biệt (Slicing Test) hoặc kiểm thử toàn bộ ứng dụng (Integration Test) một cách nhanh chóng, tin cậy.

> **Định nghĩa chuẩn về Spring Testing:**
> Spring provides a set of testing utilities that make it easy to test the various components of a Spring application, including controllers, services, repositories, and other components. It has a rich set of testing annotations, utility classes and other features to aid in unit testing, integration testing and more.

---

## 🔺 1. Kim Tự Tháp Kiểm Thử Trong Spring Boot (Testing Pyramid)

```text
              / \
             /   \         End-to-End Test (E2E)
            /  ▲  \        Chạy toàn bộ app + DB thật + Selenium/Cypress (Rất chậm, chi phí cao)
           /───────\
          /         \      Integration Test (@SpringBootTest)
         /     ▲     \     Load toàn bộ Spring ApplicationContext, kết nối các tầng (Vừa phải)
        /─────────────\
       /               \   Slice Test (@WebMvcTest, @DataJpaTest)
      /        ▲        \  Chỉ nạp duy nhất 1 tầng cần test vào bộ nhớ (Nhanh)
     /───────────────────\
    /                     \ Unit Test (JUnit 5 + Mockito)
   /           ▲           \Test từng class riêng biệt, mock 100% dependency (Siêu nhanh, chạy dưới 1s)
  /─────────────────────────\
```

---

## 🧰 2. Các Thư Viện Tích Hợp Trong `spring-boot-starter-test`

Khi thêm dependency này vào `pom.xml`, bạn sở hữu trọn bộ công cụ test hàng đầu thế giới mà không cần cài đặt thêm gì:

- **JUnit 5 (Jupiter):** Framework viết test chuẩn hiện đại của Java.
- **Mockito:** Thư viện tạo các đối tượng giả lập (Mocking objects) và kiểm tra tương tác.
- **AssertJ:** Thư viện viết câu khẳng định kiểm thử (Fluent assertions: `assertThat(result).isEqualTo(...)`).
- **MockMvc:** Giả lập gửi HTTP Request tới Controller mà không cần bật Web Server Tomcat thật.
- **JSONPath:** Phân tích và kiểm tra các trường trong chuỗi dữ liệu JSON trả về.

---

## 🎯 3. Chiến Lược Test 3 Tầng: Controller - Service - Repository

### Tầng 1: Test Controller (Web Layer) với `@WebMvcTest` & `MockMvc`
> **Mục tiêu:** Kiểm tra xem Controller có nhận đúng HTTP Method, parse đúng `@PathVariable` / JSON body và trả về đúng Status Code hay không. Không cần bật database!

```java
@WebMvcTest(UserController.class) // Chỉ nạp UserController vào ngữ cảnh web
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc; // Công cụ giả lập bắn HTTP Request

    @MockBean
    private UserService userService; // Giả lập Service bên dưới

    @Test
    void testGetUserById_Success() throws Exception {
        // 1. Given (Chuẩn bị dữ liệu giả)
        UserDTO mockUser = new UserDTO(1L, "Thịnh", "thinh@gmail.com");
        Mockito.when(userService.getUserById(1L)).thenReturn(mockUser);

        // 2. When & Then (Bắn HTTP GET và kiểm tra kết quả)
        mockMvc.perform(get("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Kỳ vọng trả về 200 OK
                .andExpect(jsonPath("$.id").value(1)) // Kiểm tra trường id trong JSON
                .andExpect(jsonPath("$.name").value("Thịnh"));
    }
}
```

---

### Tầng 2: Test Service (Business Layer) với Unit Test Thuần (JUnit 5 + Mockito)
> **Mục tiêu:** Kiểm tra các logic tính toán, rẽ nhánh nghiệp vụ. Không nạp Spring Container để tốc độ test đạt mức mili-giây!

```java
@ExtendWith(MockitoExtension.class) // Không load Spring context, chạy siêu nhẹ
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository; // Giả lập Repository

    @Mock
    private PaymentClient paymentClient;     // Giả lập Feign Client

    @InjectMocks
    private OrderServiceImpl orderService;   // Tự động tiêm các mock ở trên vào Service

    @Test
    void testCreateOrder_Success() {
        // Given
        Order mockOrder = new Order(100L, 500.0, "PENDING");
        Mockito.when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);
        Mockito.when(paymentClient.processPayment(100L, 500.0)).thenReturn("SUCCESS");

        // When
        String result = orderService.createOrder(100L, 500.0);

        // Then
        assertThat(result).contains("SUCCESS");
        Mockito.verify(orderRepository, times(2)).save(any(Order.class)); // Xác minh hàm save được gọi 2 lần
    }
}
```

---

### Tầng 3: Test Repository (Data Access Layer) với `@DataJpaTest`
> **Mục tiêu:** Kiểm tra các câu truy vấn `@Query`, JPA derived query. Tự động cấu hình In-Memory Database (H2) và **tự động Rollback dữ liệu** sau mỗi hàm test để không làm bẩn DB.

```java
@DataJpaTest // Chỉ cấu hình Spring Data JPA và Database H2
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByEmail() {
        // Given
        UserEntity user = new UserEntity("thinh@gmail.com", "Thịnh Nguyễn");
        userRepository.save(user);

        // When
        Optional<UserEntity> found = userRepository.findByEmail("thinh@gmail.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Thịnh Nguyễn");
        // Sau khi hàm test kết thúc, Spring tự ROLLBACK dữ liệu!
    }
}
```

---

## 🌐 4. Integration Test Toàn Diện với `@SpringBootTest`

Nếu bạn muốn kiểm tra từ A đến Z (Request đi từ Controller $\rightarrow$ Service $\rightarrow$ Repository $\rightarrow$ Database thật):

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class FullApplicationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testFullFlowCreateAndGetUser() throws Exception {
        // Bắn request POST tạo User vào DB thật
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Thịnh\", \"email\": \"test@gmail.com\"}"))
                .andExpect(status().isCreated());

        // Bắn tiếp request GET để kiểm tra xem đã lưu thành công chưa
        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@gmail.com"));
    }
}
```

---

## ⚔️ 5. Bẫy Phỏng Vấn: `@Mock` vs `@MockBean` & Sức Mạnh `@SpringBootTest`

### 1. Phân biệt `@Mock` (Mockito thuần) vs `@MockBean` (Spring Test):

| Tiêu chí | `@Mock` (org.mockito.Mock) | `@MockBean` (org.springframework...MockBean) |
| :--- | :--- | :--- |
| **Thuộc thư viện nào?** | Thư viện **Mockito thuần** | Thư viện **Spring Boot Test** |
| **Có nạp Spring Context?** | **KHÔNG**. Chạy siêu nhẹ với `@ExtendWith(MockitoExtension.class)`. | **CÓ**. Tích hợp trực tiếp vào Spring `ApplicationContext`. |
| **Cơ chế hoạt động** | Tạo 1 instance giả lập trong Java, phải tự inject bằng `@InjectMocks`. | **Thay thế hoàn toàn một Spring Bean thật** trong ApplicationContext bằng Bean giả lập. Bất kỳ bean nào khác `@Autowired` tới nó đều nhận được con mock này! |
| **Dùng khi nào?** | Viết **Unit Test** cho tầng Service/Logic thuần. | Viết **Web Slice Test** (`@WebMvcTest`) hoặc **Integration Test** khi cần mock 1 bean cụ thể (vd: cổng thanh toán VNPay). |

> [!TIP]
> **Cập nhật Spring Boot 3.4+:** Annotation `@MockBean` đã được thay thế bằng `@MockitoBean` trong Spring Framework 6.2 / Spring Boot 3.4 trở đi (cơ chế tương tự nhưng hỗ trợ mạnh hơn).

---

### 2. Các chế độ môi trường Web của `@SpringBootTest`:

Annotation `@SpringBootTest` cung cấp tham số `webEnvironment` để điều chỉnh cách khởi động Web Server:
- `MOCK` (Mặc định): Không bật server mạng thật, giả lập môi trường Servlet (dùng chung với `MockMvc`).
- `RANDOM_PORT`: Bật một server Tomcat nhúng **thật** trên một cổng ngẫu nhiên khả dụng (thích hợp test với `TestRestTemplate` hoặc `WebTestClient`).
- `NONE`: Không bật môi trường web nào (dùng để test các Batch Job hoặc Service nền).

---

## 💡 Best Practices Khi Viết Test Trong Spring Boot

1. **Tuân thủ mô hình BDD: Given - When - Then (hoặc AAA: Arrange - Act - Assert):**
   - **Given:** Chuẩn bị dữ liệu mẫu và thiết lập hành vi giả lập (`when(...).thenReturn(...)`).
   - **When:** Thực thi phương thức cần kiểm thử.
   - **Then:** Khẳng định kết quả mong đợi (`assertThat(...)`, `andExpect(...)`).
2. **Ưu tiên Unit Test hơn Integration Test:**
   - 70% số lượng bài test trong dự án nên là **Unit Test** với Mockito vì tốc độ chạy chỉ mất vài phần trăm giây.
   - Hạn chế lạm dụng `@SpringBootTest` ở mọi nơi vì nó phải khởi động toàn bộ Spring Context, khiến quá trình build CI/CD kéo dài hàng chục phút.
3. **Kiểm thử cả trường hợp lỗi (Edge Cases / Negative Testing):**
   - Không chỉ test case thành công (Happy path), luôn viết test cho các kịch bản ném Exception (`UserNotFoundException`, `InsufficientBalanceException`) để đảm bảo API trả về đúng mã lỗi 400, 404 thay vì 500 crash server.
