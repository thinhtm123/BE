# 20. Spring Data & Spring Data JPA

Spring Data là tập hợp các dự án trừu tượng hóa tầng truy xuất dữ liệu (Data Access Layer), cung cấp giao diện Repository nhất quán cho cả Relational DB (JPA/MySQL) và NoSQL DB (MongoDB, Redis).

---

## 🌳 Cây Kế Thừa Repository Interface

```text
Repository (Marker Interface)
 └── CrudRepository (Các hàm CRUD cơ bản: save, findById, delete)
      └── PagingAndSortingRepository (Hỗ trợ Pageable & Sort)
           └── JpaRepository (Hỗ trợ flush, saveAndFlush, deleteInBatch)
```

---

## ⚡ 1. Derived Query Methods (Tự động sinh SQL từ tên hàm)

Spring Data JPA tự động phân tích (parse) tên phương thức trong interface để tạo truy vấn SQL:

- `findByEmail(String email)` ➔ `WHERE email = ?`
- `findByAgeGreaterThan(int age)` ➔ `WHERE age > ?`
- `findByFullNameContaining(String keyword)` ➔ `WHERE full_name LIKE %?%`
- `findByStatusOrderByNameAsc(String status)` ➔ `WHERE status = ? ORDER BY name ASC`
- `existsByEmail(String email)` ➔ `SELECT COUNT(*) > 0 WHERE email = ?`
- `deleteByEmail(String email)` ➔ `DELETE FROM users WHERE email = ?`

---

## 📝 2. Các Cách Viết `@Query` Trong Spring Data JPA

### Cách A: JPQL (Java Persistence Query Language - Theo Entity)
```java
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("SELECT u FROM UserEntity u WHERE u.email = :email AND u.status = :status")
    Optional<UserEntity> findCustomUser(@Param("email") String email, @Param("status") String status);
}
```

### Cách B: Native Query (SQL Thô theo Bảng Database)
```java
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query(value = "SELECT * FROM users WHERE email = :email AND status = :status", nativeQuery = true)
    Optional<UserEntity> findNativeUser(@Param("email") String email, @Param("status") String status);
}
```

---

## 📑 3. Phân Trang (Pagination) & Sắp Xếp (Sorting)

```java
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    // Trả về đối tượng Page chứa data + metadata phân trang
    Page<UserEntity> findByStatus(String status, Pageable pageable);
}
```

**Sử dụng ở Service:**
```java
// Trang 0, 10 phần tử/trang, sort giảm dần theo id
Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
Page<UserEntity> result = userRepository.findByStatus("ACTIVE", pageable);

List<UserEntity> users = result.getContent(); // Danh sách user
int totalPages = result.getTotalPages();      // Tổng số trang
long totalElements = result.getTotalElements(); // Tổng số bản ghi
```

---

## 💡 Tips & Tricks

1. **Ưu tiên dùng `@Modifying` khi viết Query UPDATE/DELETE:**
   Khi viết `@Query` làm thao tác `UPDATE` hoặc `DELETE`, bắt buộc phải thêm annotation `@Modifying`:
   ```java
   @Modifying
   @Transactional
   @Query("UPDATE UserEntity u SET u.status = :status WHERE u.id = :id")
   int updateStatus(@Param("id") Long id, @Param("status") String status);
   ```

2. **Dùng Projection khi chỉ cần lấy 1 vài trường (Tránh SELECT *):**
   Tạo Interface Projection chứa các hàm `getter` của trường cần lấy để tối ưu tốc độ truy vấn.
