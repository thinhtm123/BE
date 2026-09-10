# 05. Spring MVC Architecture & REST APIs

Spring MVC là một web framework mạnh mẽ nằm trong hệ sinh thái **Spring Framework**, được thiết kế nhằm giúp việc xây dựng các ứng dụng web trở nên dễ dàng và chuẩn hóa dựa trên mẫu thiết kế **MVC (Model-View-Controller)** và kiến trúc **Front Controller**.

> **Định nghĩa chuẩn (Spring MVC Architecture):**
> The Spring MVC (Model-View-Controller) is a web application framework that is part of the Spring Framework. It is designed to make it easy to build web applications using the MVC design pattern.
>
> Ứng dụng được phân tách thành các thành phần chuyên trách: **Model** (Dữ liệu & Logic nghiệp vụ), **View** (Hiển thị giao diện HTML hoặc dữ liệu JSON), và **Controller** (Nhạc trưởng tiếp nhận HTTP Request, điều phối xử lý và sinh ra phản hồi thích hợp).

---

## 🏛️ 1. Ba Thành Phần Trong Mô Hình MVC

| Thành phần | Đại diện cho cái gì? | Nhiệm vụ cụ thể trong Spring |
| :--- | :--- | :--- |
| **Model** | Dữ liệu & Nghiệp vụ | Chứa dữ liệu cần hiển thị (Java Object, DTO, Entity) hoặc đối tượng `org.springframework.ui.Model`. |
| **View** | Giao diện hiển thị | - **Web truyền thống (SSR):** File HTML/JSP/Thymeleaf nhận Model để render giao diện.<br>- **Web hiện đại (REST API):** Chuỗi dữ liệu thô dạng **JSON/XML** gửi về cho Frontend/Mobile. |
| **Controller** | "Nhạc trưởng" điều phối | Tiếp nhận HTTP Request từ trình duyệt qua DispatcherServlet, gọi Service xử lý nghiệp vụ, đẩy dữ liệu vào Model và chọn View tương ứng. |

---

## 🧩 2. Kiến Trúc Chi Tiết Của Spring MVC (6 Thành Phần Cốt Lõi)

Kiến trúc Spring MVC hoạt động như một dây chuyền lắp ráp gồm 6 linh kiện chuyên biệt xoay quanh **Front Controller (`DispatcherServlet`)**:

```text
                               ┌─────────────────────────────────────────────────────────────┐
                               │                    DispatcherServlet                        │
                               │                   (Front Controller)                        │
                               └──────┬──────────────┬──────────────┬──────────────┬─────────┘
                                      │              │              │              │
                       (1) Tìm Controller    (2) Gọi hàm    (3) Xử lý giao diện (4) Xử lý lỗi
                                      │              │              │              │
                                      ▼              ▼              ▼              ▼
                              ┌──────────────┐┌──────────────┐┌──────────────┐┌──────────────┐
                              │HandlerMapping││HandlerAdapter││ ViewResolver ││   Handler    │
                              │              ││              ││ (hoặc Message││  Exception   │
                              │              ││              ││  Converter)  ││   Resolver   │
                              └──────────────┘└──────────────┘└──────────────┘└──────────────┘
```

1. **`DispatcherServlet` (Front Controller):**
   - Tâm điểm đón nhận mọi HTTP Request gửi tới ứng dụng tại cổng duy nhất (`/`).
   - Quản lý vòng đời của **`WebApplicationContext`** (IoC container chứa các bean tầng web).
2. **`HandlerMapping` (Bộ định tuyến):**
   - Phân tích URL, HTTP Method và Header của request để tìm xem Controller và Method nào phụ trách xử lý (thông qua `@RequestMapping`, `@GetMapping`...).
3. **`HandlerAdapter` (Cầu nối thực thi):**
   - Giúp `DispatcherServlet` thực thi hàm Controller. Nó đảm nhận việc bóc tách dữ liệu từ request (`@PathVariable`, `@RequestParam`, `@RequestBody`) và ép kiểu (type casting) truyền vào tham số của hàm.
4. **`Controller` / `Handler` (Bộ xử lý):**
   - Nơi lập trình viên viết logic tiếp nhận, gọi tầng Service/Repository và trả về kết quả.
5. **`ViewResolver` & `HttpMessageConverter` (Tầng trả dữ liệu):**
   - Với Web truyền thống: `ViewResolver` (Thymeleaf, JSP) tìm file template tương ứng để ghép dữ liệu (render).
   - Với REST API: `HttpMessageConverter` (mặc định dùng **Jackson**) tự động serialize Java Object thành chuỗi **JSON**.
6. **`HandlerExceptionResolver` (Bộ cứu hộ lỗi):**
   - Hứng và chuyển đổi các Exception phát sinh trong Controller thành HTTP Response chuẩn đẹp (kích hoạt qua `@RestControllerAdvice` + `@ExceptionHandler`).

---

## 🛠️ 3. Các Thành Phần Hậu Cần & Bổ Trợ (Supporting Components)

Ngoài các linh kiện trực tiếp tham gia luồng xử lý request, Spring MVC còn sở hữu các thành phần "hậu phương" đóng vai trò tối quan trọng:

### 1. Spring IoC Container (`WebApplicationContext`)
- **Quản lý vòng đời (Lifecycle Management):** Khởi tạo, cấu hình các bean (Controller, Service, Repository, Component), nạp các dependency qua DI và hủy bean khi server tắt.
- Cho phép các Controller ở tầng Web dễ dàng gọi và inject tầng Business Logic bên dưới một cách lỏng lẻo (Loosely Coupled).

### 2. Interceptor (`HandlerInterceptor`) - Bổ trợ tính năng xuyên suốt
Interceptor đóng vai trò như các "trạm kiểm soát" chặn trước và sau khi Controller xử lý:
- **`preHandle()`**: Chạy TRƯỚC khi Controller thực thi. Trả về `true` để cho đi tiếp, `false` để chặn lại.
- **`postHandle()`**: Chạy SAU khi Controller hoàn thành nhưng TRƯỚC khi render View.
- **`afterCompletion()`**: Chạy SAU KHI request đã hoàn tất hoàn toàn (dùng để dọn dẹp tài nguyên).

**Các ứng dụng thực tế phổ biến của Interceptor:**
- **Security & Authorization:** Kiểm tra Session hoặc Custom Token trước khi cho phép request vào Controller.
- **Caching (Bộ nhớ đệm):** Kiểm tra ETag, tự động thêm các HTTP Cache headers (`Cache-Control`, `Expires`).
- **Logging & Đo thời gian thực thi (Performance Timing):** Bấm giờ xử lý của request để phát hiện các Controller chạy chậm.
- **Đa ngôn ngữ (i18n):** `LocaleChangeInterceptor` tự động bắt tham số `?lang=en` trên URL để đổi ngôn ngữ hiển thị.

### 3. `MultipartResolver` (Xử lý Upload File)
- Chịu trách nhiệm phân giải gói tin HTTP dạng `multipart/form-data`, chuyển đổi file tải lên thành đối tượng `MultipartFile` để Controller xử lý lưu trữ.

---

## 🔄 4. Vòng Đời Chi Tiết Của Một Request (Request Lifecycle)

```text
Browser / Client (HTTP Request)
      │
      │ 1. Gửi HTTP Request (vd: GET /api/users/1)
      ▼
┌────────────────────────────────────────────────────────────────────────┐
│                        DispatcherServlet                               │
│                                                                        │
│  2. URL này map với hàm nào? ──> [ HandlerMapping ]                    │
│                                           │ Trả về UserController.java │
│                                           ▼                            │
│  3. Thực thi hàm ───────────> [ HandlerAdapter ]                       │
│                                           │ Gọi getUserById(1)         │
│                                           ▼                            │
│  4. Nhận kết quả từ Controller:                                        │
│     ├── Trường hợp A (HTML): Trả về ("user-page", Model)               │
│     │      │                                                           │
│     │      └──> [ ViewResolver ] ──> Render ra mã HTML hoàn chỉnh      │
│     │                                                                  │
│     └── Trường hợp B (REST API): Có gắn @ResponseBody / @RestController│
│            │                                                           │
│            └──> [ HttpMessageConverter (Jackson) ] ──> Convert sang JSON
└────────────────────────────────────────────────────────────────────────┘
      │
      │ 5. Trả về HTTP Response (HTML hoặc JSON)
      ▼
Browser / Client
```

---

## 🌳 5. Cây Phả Hệ Của `DispatcherServlet` & Vai Trò Apache Tomcat

Bản chất `DispatcherServlet` vẫn kế thừa từ chuẩn **Servlet của Java EE / Jakarta EE**:

```text
javax.servlet.Servlet (Interface gốc của Java Servlet)
  └── GenericServlet
        └── HttpServlet (Cung cấp các hàm doGet, doPost, doPut, doDelete...)
              └── HttpServletBean (Biến Servlet thành một Spring Bean)
                    └── FrameworkServlet (Khởi tạo & tích hợp WebApplicationContext)
                          └── DispatcherServlet (Trái tim điều phối của Spring MVC)
```

- **Apache Tomcat (Web Server nhúng):** Đóng vai trò mở cổng TCP mạng (ví dụ port 8080), lắng nghe các kết nối HTTP từ client, chuyển đổi chuỗi text thô thành 2 đối tượng Java: `HttpServletRequest` và `HttpServletResponse`, sau đó ném cho `DispatcherServlet`.
- `DispatcherServlet` tiếp nhận và dùng toàn bộ sức mạnh của Spring IoC để xử lý.

---

## ⚔️ 6. So Sánh: `@Controller` vs `@RestController`

| Đặc điểm | `@Controller` (Mô hình MVC gốc) | `@RestController` (Mô hình REST API) |
| :--- | :--- | :--- |
| **Mục đích** | Dành cho ứng dụng Server-Side Rendering (Web truyền thống) | Dành cho Backend viết API (Mobile, React, Angular gọi vào) |
| **Kiểu trả về** | Chuỗi `String` biểu thị **tên file HTML template** (Thymeleaf, JSP) | Trả về trực tiếp **Java Object / List / DTO** |
| **Bản chất** | Annotation đơn lẻ | Bản chất là `@Controller` + `@ResponseBody` gộp lại |
| **Cơ chế** | Đẩy kết quả qua `ViewResolver` | Đẩy kết quả qua `HttpMessageConverter` biến thành JSON |

### Code Minh Họa:

```java
// 1. Dành cho Web trả về giao diện HTML
@Controller
@RequestMapping("/products")
public class ProductWebController {

    @GetMapping
    public String viewProductList(Model model) {
        model.addAttribute("items", List.of("Laptop", "Mouse"));
        return "products"; // Sẽ tìm file /templates/products.html
    }
}

// 2. Dành cho REST API trả về JSON
@RestController
@RequestMapping("/api/products")
public class ProductApiController {

    @GetMapping
    public List<String> getProductsApi() {
        return List.of("Laptop", "Mouse"); // Jackson tự động trả về JSON: ["Laptop", "Mouse"]
    }
}
```

---

## 📌 7. Các Annotation Ràng Buộc Dữ Liệu Phổ Biến (Data Binding)

- **`@PathVariable`**: Trích xuất giá trị từ đường dẫn URL.
  ```java
  @GetMapping("/users/{id}")
  public UserDTO getUser(@PathVariable("id") Long id) { ... }
  ```
- **`@RequestParam`**: Lấy tham số truy vấn từ Query String (sau dấu `?`).
  ```java
  // GET /users?page=1&size=10
  @GetMapping("/users")
  public List<UserDTO> getUsers(@RequestParam(defaultValue = "1") int page, 
                                @RequestParam(defaultValue = "10") int size) { ... }
  ```
- **`@RequestBody`**: Đọc dữ liệu JSON từ Body của Request và parse thành Java Object (thường dùng cho POST/PUT).
  ```java
  @PostMapping("/users")
  public UserDTO createUser(@Valid @RequestBody CreateUserRequest request) { ... }
  ```
- **`@RequestHeader`**: Lấy giá trị từ HTTP Header (ví dụ Authorization token).
- **`@ResponseStatus`**: Đặt mã HTTP Status Code trả về (ví dụ `HttpStatus.CREATED` - 201).

---

## 💡 8. Best Practices & Bẫy Phỏng Vấn (Gotchas)

### 1. Bẫy Thread-Safety trong Controller (Đặc biệt nguy hiểm):
- Cả `DispatcherServlet` và các `@Controller` / `@RestController` mặc định đều là **Singleton** (chỉ có 1 instance duy nhất trong bộ nhớ RAM cho toàn app).
- Mỗi khi có HTTP Request gửi tới, Tomcat sẽ cấp **1 Thread riêng** chạy xuyên qua cùng 1 instance Controller đó.
- ❌ **CẤM:** Không được lưu trạng thái người dùng vào biến instance của Controller (Stateful variable):
  ```java
  @RestController
  public class OrderController {
      private Long currentUserId; // ❌ NGUY HIỂM! Request của User A sẽ ghi đè lên User B (Race Condition)
  }
  ```
-  Controller phải là **Stateless** (phi trạng thái), chỉ inject các Service hoặc Dependency bất biến (`private final ...`).

### 2. Phân biệt Servlet Filter vs Spring Interceptor:
- **Filter (Servlet Filter):** Đứng **TRƯỚC** `DispatcherServlet` (ở tầng Servlet Container / Tomcat). Thích hợp cho CORS, nén Gzip, Spring Security Filter Chain.
- **Interceptor (`HandlerInterceptor`):** Đứng **SAU** `DispatcherServlet`, bao quanh Controller. Có thể truy cập được thông tin về Controller Method (`HandlerMethod`).

### 3. Đừng try-catch thủ công trong Controller:
- Không bao giờ viết `try { ... } catch (Exception e)` nhằng nhịt trong Controller để trả về lỗi 500 hay 404.
- Hãy để Exception văng tự nhiên và hứng tập trung bằng **`@RestControllerAdvice`** + **`@ExceptionHandler`**.

### 4. Bẫy mô hình Thread-per-request (Blocking IO):
- Spring MVC hoạt động theo cơ chế **1 Request = 1 Thread Tomcat**.
- Nếu bạn thực hiện các tác vụ tốn thời gian (gọi API bên thứ 3 chậm, export file nặng) trực tiếp trong Controller, Thread của Tomcat sẽ bị giữ (block), làm cạn kiệt Thread Pool và làm đơ luôn toàn bộ ứng dụng. Giải pháp là dùng `@Async`, Task Executor hoặc chuyển sang Spring WebFlux.
