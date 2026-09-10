# 17. Database Transactions & ACID Properties

Transaction (Giao dịch) là một đơn vị công việc không thể chia nhỏ (Unit of Work). Nếu một bước trong giao dịch thất bại, toàn bộ giao dịch sẽ thất bại và được khôi phục về trạng thái ban đầu (Rollback).

---

## 💎 4 Tính Chất ACID

1. **Atomicity (Tính Nguyên tử):** Nguyên tắc "Tất cả hoặc Không có gì" (All or Nothing). Tất cả các câu SQL cùng thành công hoặc tất cả cùng bị hủy bỏ.
2. **Consistency (Tính Nhất quán):** Đảm bảo dữ liệu trước và sau giao dịch luôn thỏa mãn các quy tắc và ràng buộc tính hợp lệ của Database.
3. **Isolation (Tính Cô lập):** Đảm bảo các giao dịch chạy song song không ảnh hưởng hoặc đọc dữ liệu dở dang của nhau.
4. **Durability (Tính Bền vững):** Một khi giao dịch đã được `COMMIT`, dữ liệu được lưu vĩnh viễn vào đĩa cứng và không bị mất kể cả khi sập nguồn điện.

---

## ⚙️ Các Cấp Độ Cô Lập (Isolation Levels) Trong Transaction

Spring Security / Hibernate hỗ trợ 4 cấp độ cô lập chống các hiện tượng nhiễu dữ liệu:

- **`READ_UNCOMMITTED`:** Thấp nhất, có thể gặp hiện tượng **Dirty Read** (đọc dữ liệu chưa commit của giao dịch khác).
- **`READ_COMMITTED`:** Chỉ đọc dữ liệu đã commit (Chống Dirty Read).
- **`REPEATABLE_READ`:** Đảm bảo đọc cùng 1 dòng dữ liệu n lần trong 1 TX thì kết quả luôn giống nhau (Chống Non-repeatable Read).
- **`SERIALIZABLE`:** Cao nhất, xếp hàng các TX chạy tuần tự (Chống Phantom Read), nhưng hiệu năng chậm nhất.

---

## 🛠️ Sử Dụng `@Transactional` Trong Spring Boot

```java
@Service
public class TransferService {

    private final AccountRepository accountRepository;

    public TransferService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    // Cấu hình Transaction với quy tắc Rollback khi gặp RuntimeException
    @Transactional(
        isolation = Isolation.READ_COMMITTED,
        propagation = Propagation.REQUIRED,
        rollbackFor = Exception.class
    )
    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        accountRepository.deduct(fromId, amount);
        
        // Giả sử có lỗi xảy ra ở đây
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số tiền chuyển không hợp lệ");
        }

        accountRepository.deposit(toId, amount);
    }
}
```

---

## 💡 Tips & Tricks & Cạm Bẫy Với `@Transactional`

1. **Mặc định `@Transactional` CHỈ Rollback với `RuntimeException`:**
   Nếu phương thức ném ra `Checked Exception` (như `IOException`, `SQLException`), Spring **MẶC ĐỊNH SẼ KHÔNG ROLLBACK**!
   - **Giải pháp:** Luôn thêm thuộc tính: `@Transactional(rollbackFor = Exception.class)`.

2. **Cạm bẫy Self-Invocation (Gọi hàm nội bộ):**
   Nếu `methodA()` (không có `@Transactional`) gọi `methodB()` (có `@Transactional`) trong cùng 1 Class ➔ `@Transactional` của `methodB` **sẽ không có tác dụng** vì cuộc gọi `this.methodB()` bị BỎ QUA (bypass) Spring AOP Proxy.

   ```java
   // ❌ LỖI: @Transactional ở saveOrder() bị mất tác dụng vì gọi qua this.saveOrder()
   @Service
   public class OrderService {
       public void processOrder(Long id) {
           this.saveOrder(id); // Bỏ qua Spring AOP Proxy!
       }

       @Transactional
       public void saveOrder(Long id) { ... }
   }

   // ✅ SỬA: Tách saveOrder() sang một Service riêng (PaymentTransactionService) rồi inject vào!
   @Service
   @RequiredArgsConstructor
   public class PaymentTransactionService {
       private final AccountRepository accountRepository;

       @Transactional(rollbackFor = Exception.class) // Đặt @Transactional tại đây
       public void executePayment(Long fromAcc, Long toAcc, BigDecimal amount) {
           accountRepository.deduct(fromAcc, amount);
           if (amount.compareTo(new BigDecimal("100000000")) > 0) {
               throw new RuntimeException("Vượt hạn mức!");
           }
           accountRepository.deposit(toAcc, amount);
       }
   }

   @Service
   @RequiredArgsConstructor
   public class OrderProcessingService {
       private final PaymentTransactionService paymentTransactionService; // Inject Service riêng

       public void processOrder(Long fromAcc, Long toAcc, BigDecimal amount) {
           // Gọi qua Proxy của PaymentTransactionService -> @Transactional & Rollback hoạt động 100%!
           paymentTransactionService.executePayment(fromAcc, toAcc, amount);
       }
   }
   ```


