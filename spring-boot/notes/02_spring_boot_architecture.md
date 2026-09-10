# 02. Spring Boot Architecture (Kiến trúc phân tầng)

Spring Boot áp dụng **Layered Architecture** dựa trên nguyên lý **Separation of Concerns** (Phân tách mối quan tâm). Mỗi tầng chỉ giao tiếp với tầng ngay bên dưới hoặc bên trên nó.

---

## 🏛️ 4 Tầng trong Spring Boot

```
[ Client / Browser / Mobile App ]
              │
              ▼
1. Presentation Layer (@RestController)   --> Handles HTTP, Validation, DTO mapping
              │
              ▼
2. Business Layer (@Service)              --> Business Logic, Authorization, @Transactional
              │
              ▼
3. Persistence Layer (@Repository)        --> Database storage logic, Entities, Queries
              │
              ▼
4. Database Layer (MySQL / PostgreSQL)    --> Actual physical data storage
```

### Chi tiết 4 tầng:

1. **Presentation Layer:** Nhận HTTP Request, validate tham số (`@Valid`), xác thực (Authentication) và trả về HTTP Response + Status Code.
2. **Business Layer:** Chứa toàn bộ logic nghiệp vụ (Business Rules), phân quyền (Authorization), điều phối giao dịch (`@Transactional`).
3. **Persistence Layer:** Chứa logic truy xuất dữ liệu, biến đổi đối tượng từ/thành các dòng trong database (Spring Data JPA / ORM).
4. **Database Layer:** Nơi lưu trữ vật lý (thao tác CRUD).

---

## ❓ Tại sao lại dùng Interface `UserService` và Class `UserServiceImpl`?

### Phân biệt:
- **`UserService` (Interface):** Là Hợp đồng (Contract) định nghĩa danh sách hàm.
- **`UserServiceImpl` (Class):** Cài đặt chi tiết thực tế của Hợp đồng đó.

### Cơ chế Spring IoC:
- Tầng Controller khai báo tham số kiểu Interface: `public UserController(UserService userService)`.
- Khi ứng dụng khởi chạy, Spring tự động tìm Bean `@Service` triển khai interface đó (`UserServiceImpl`) và inject vào Controller.

### Lợi ích:
- **Loose Coupling:** Controller không bị phụ thuộc vào chi tiết cài đặt của Service.
- **Unit Testing (Mocking):** Dễ dàng tạo Mock Object cho Interface khi test Controller.
- **Multiple Implementations:** Dễ dàng thay đổi hoặc thêm cài đặt mới mà không cần sửa code ở Controller.

---

## 💡 Tips & Tricks cho Layered Architecture

1. **Dùng Lombok `@RequiredArgsConstructor`:**
   Giảm bớt boilerplate code khi inject dependencies.
   ```java
   @Service
   @RequiredArgsConstructor
   public class UserServiceImpl implements UserService {
       private final UserRepository userRepository;
       private final PasswordEncoder passwordEncoder;
   }
   ```

2. **Dùng Java `record` làm DTO:**
   Tuyệt đối **KHÔNG trả Entity ra ngoài Controller**. Dùng `record` để tạo DTO immutable và ngắn gọn.
   ```java
   public record CreateUserRequestDTO(
       @NotBlank String fullName,
       @Email String email,
       @Size(min = 6) String password
   ) {}

   public record UserResponseDTO(Long id, String fullName, String email) {}
   ```

3. **Phân chia Validation hợp lý:**
   - **Controller Layer:** Validate định dạng (`@Valid`, `@NotNull`, `@Email`).
   - **Service Layer:** Validate nghiệp vụ (Check email đã trùng chưa, check số dư tài khoản).
