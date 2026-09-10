# 07. Spring Bean Scopes

Scope (Phạm vi) quy định số lượng instance được tạo ra và vòng đời (lifecycle) của một Spring Bean trong IoC Container.

---

## 📊 6 Loại Scope trong Spring Framework

1. **Singleton (Mặc định):** Chỉ tạo **1 instance duy nhất** cho toàn bộ ứng dụng. Tất cả các nơi inject đều dùng chung instance này.
2. **Prototype:** Tạo **1 instance MỚI** mỗi khi Bean được yêu cầu/inject.
3. **Request (Web):** Tạo 1 instance mới cho **mỗi HTTP Request**. Tự hủy khi request kết thúc.
4. **Session (Web):** Tạo 1 instance mới cho **mỗi HTTP Session** (dùng cho Giỏ hàng, User Session).
5. **Application (Web):** Tạo 1 instance duy nhất cho toàn bộ `ServletContext`.
6. **WebSocket (Web):** Gắn liền với vòng đời của 1 kết nối WebSocket.

---

## 📌 Ví dụ thực tế cho từng Scope

### 1. Singleton Scope (Mặc định)
```java
@Service // Mặc định là Singleton Scope
public class UserService {
    // Phải đảm bảo Stateless (không chứa thông tin biến riêng của 1 user cụ thể)
}
```

### 2. Prototype Scope
```java
@Component
@Scope("prototype")
public class ExportExcelTask {
    // Mỗi khi export excel, Spring sẽ tạo 1 instance mới hoàn toàn
}
```

### 3. Session Scope (Giỏ hàng người dùng)
```java
@Component
@SessionScope // Tạo riêng cho từng phiên làm việc của mỗi người dùng
public class UserCart {
    private List<String> cartItems = new ArrayList<>();
}
```

---

## 💡 Tips & Tricks & Bẫy Phỏng Vấn về Bean Scope

1. **Quy tắc thiết kế Singleton Bean:**
   - Hầu hết các `@Service`, `@Repository`, `@RestController` đều nên là **Singleton** để tối ưu bộ nhớ RAM.
   - **CẢNH BÁO:** Vì Singleton được dùng chung bởi nhiều Thread (người dùng) cùng lúc, **KHÔNG ĐƯỢC lưu trạng thái cá nhân (Stateful Data)** vào biến instance của Singleton Bean để tránh rò rỉ dữ liệu giữa các User (Thread safety issue).

2. **Giải quyết bẫy Inject Request/Session Bean vào Singleton Bean:**
   - Singleton Bean tạo ngay khi bật App, trong khi Request Bean chỉ tạo khi có HTTP Request gửi đến.
   - **Giải pháp:** Sử dụng `proxyMode = ScopedProxyMode.TARGET_CLASS` (Đã được tích hợp sẵn trong `@RequestScope` và `@SessionScope`). Spring sẽ tiêm một Proxy đại lý đứng ra làm trung gian điều phối.
