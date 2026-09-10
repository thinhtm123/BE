# 19. Hibernate Entity Lifecycle & Dirty Checking

Entity Lifecycle định nghĩa 4 trạng thái vòng đời của một đối tượng Entity và mức độ kết nối của nó với Hibernate Persistence Context (Session).

---

## 🔄 4 Trạng Thái Vòng Đời Entity

1. **Transient (Tạm thời):** Đối tượng vừa được khởi tạo bằng `new`, chưa có ID, chưa được quản lý bởi Hibernate Session.
2. **Persistent (Đang được quản lý):** Đối tượng đang nằm trong Hibernate Session, đã có ID dưới DB. Tích hợp tính năng **Dirty Checking** (Tự động phát hiện thay đổi và `UPDATE` khi Commit Transaction).
3. **Detached (Ngắt kết nối):** Đối tượng từng nằm trong Session nhưng Session đã đóng/kết thúc Transaction. Thay đổi thuộc tính lúc này không tự động `UPDATE` xuống DB.
4. **Removed (Đánh dấu xóa):** Đối tượng bị gọi lệnh `delete()` và chờ lệnh `DELETE` bắn xuống DB khi Commit.

---

## ⚡ Cơ Chế Dirty Checking (Automatic Dirty Checking)

Dirty Checking là tính năng quan trọng nhất của trạng thái **Persistent**.

```java
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void updateEmail(Long userId, String newEmail) {
        // Step 1: UserEntity chuyển sang trạng thái PERSISTENT
        UserEntity user = userRepository.findById(userId).orElseThrow();

        // Step 2: Sửa đổi thuộc tính
        user.setEmail(newEmail);

        // KHÔNG CẦN userRepository.save(user)!
        // Khi Transaction kết thúc, Hibernate tự so sánh trạng thái ban đầu và trạng thái mới (Dirty Checking)
        // và tự động bắn lệnh: UPDATE users SET email = ? WHERE id = ?
    }
}
```

---

## 💡 Các Hàm Chuyển Đổi Trạng Thái Trong EntityManager

- `persist(entity)`: Transient ➔ Persistent
- `find(Entity.class, id)`: Lấy từ DB ➔ Persistent
- `detach(entity)` / `clear()`: Persistent ➔ Detached
- `merge(entity)`: Detached ➔ Persistent
- `remove(entity)`: Persistent ➔ Removed
