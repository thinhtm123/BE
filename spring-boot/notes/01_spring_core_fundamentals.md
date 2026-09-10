# 01. Spring Core Fundamentals

## 💡 Terminology & Main Concepts

Spring Core là nền tảng của Spring Framework, cung cấp mô hình cấu hình cho các ứng dụng Java.

---

### 1. Beans & IoC (Inversion of Control) / DI (Dependency Injection)

- **Bean:** Java Object được quản lý bởi Spring Container (khởi tạo, cấu hình và quản lý vòng đời).
- **Inversion of Control (IoC):** Đảo ngược quyền điều khiển. Thay vì lập trình viên tự `new` đối tượng (`new UserService()`), Spring Container (`ApplicationContext`) sẽ đứng ra khởi tạo và quản lý chúng.
- **Dependency Injection (DI):** Spring tự động tiêm (inject) các phụ thuộc (Bean A cần Bean B) vào cho bạn.

```java
// Khai báo Bean bằng Annotation
@Service
public class EmailService implements MessageService {
    public String getMessage() {
        return "Email Sent!";
    }
}

// Dependency Injection qua Constructor (Best Practice)
@RestController
public class UserController {
    private final MessageService messageService;

    // Spring tự tìm Bean phù hợp và inject vào đây
    public UserController(MessageService messageService) {
        this.messageService = messageService;
    }
}
```

> 📌 **Best Practice:** Ưu tiên **Constructor Injection** thay vì `@Autowired` trên field để đảm bảo tính Immutability (`final`) và dễ Unit Test.

---

### 2. Aspect-Oriented Programming (AOP)

Tách biệt các **Cross-cutting Concerns** (tác vụ lặp đi lặp lại ở nhiều nơi như Logging, Security Check, Performance Monitoring, Transaction) ra khỏi Business Logic chính.

- **Aspect:** Module chứa code xử lý chung.
- **Pointcut:** Quy tắc xác định hàm nào sẽ được áp dụng Aspect.
- **Advice:** Thời điểm thực thi (`@Before`, `@After`, `@Around`).

```java
@Aspect
@Component
public class LoggingAspect {

    @Around("execution(* com.example.service.*.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed(); // Chạy hàm gốc

        long executionTime = System.currentTimeMillis() - start;
        System.out.println(joinPoint.getSignature() + " chạy trong: " + executionTime + "ms");
        return result;
    }
}
```

---

### 3. Spring Event Model (Decoupled Communication)

Cho phép các component giao tiếp theo cơ chế **Publish-Subscribe** giúp giảm sự phụ thuộc trực tiếp.

```java
// 1. Event Data
public record UserRegisteredEvent(String username, String email) {}

// 2. Publisher (Bắn Event)
@Service
public class UserService {
    private final ApplicationEventPublisher eventPublisher;

    public UserService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void registerUser(String username, String email) {
        // Business logic...
        eventPublisher.publishEvent(new UserRegisteredEvent(username, email));
    }
}

// 3. Listener (Lắng nghe & Xử lý)
@Component
public class NotificationListener {
    @EventListener
    public void handleUserRegistered(UserRegisteredEvent event) {
        System.out.println("Gửi email chào mừng tới: " + event.email());
    }
}
```

---

### 4. Data Access & Transaction Management (`@Transactional`)

Spring quản lý Transaction tự động. Nếu có ngoại lệ (Exception) xảy ra trong phương thức `@Transactional`, toàn bộ thao tác Database trong đó sẽ được **Rollback** tự động.

```java
@Service
public class BankService {
    private final AccountRepository accountRepository;

    public BankService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional // Quản lý giao dịch DB
    public void transferMoney(Long fromId, Long toId, BigDecimal amount) {
        accountRepository.deduct(fromId, amount);
        // Nếu lỗi xảy ra ở đây, thao tác deduct ở trên sẽ được rollback tự động
        accountRepository.deposit(toId, amount);
    }
}
```

---

### 5. Task Execution & Scheduling

Lập lịch (`@Scheduled`) và thực thi bất đồng bộ (`@Async`).

```java
@EnableScheduling
@EnableAsync
@Configuration
public class AppConfig {}

@Component
public class ScheduledTasks {

    // Chạy định kỳ mỗi 5 giây
    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {
        System.out.println("Fixed rate task: " + LocalTime.now());
    }

    @Async
    public CompletableFuture<String> processAsyncTask() {
        return CompletableFuture.completedFuture("Task Done");
    }
}
```
