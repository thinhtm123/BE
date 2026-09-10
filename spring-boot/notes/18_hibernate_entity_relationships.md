# 18. Hibernate Entity Relationships & Cascading

Trong Hibernate, Relationships biểu diễn mối quan hệ khóa ngoại (Foreign Key) giữa các bảng Database thành các tham chiếu Object trong Java, giúp tự động hóa thao tác truy vấn và lan truyền dữ liệu (Cascading).

---

## 📊 4 Loại Mối Quan Hệ Entity

### 1. `@OneToOne` (1 - 1)
```java
@Entity
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id") // Bên giữ khóa ngoại
    private UserProfileEntity profile;
}
```

### 2. `@ManyToOne` (N - 1) & `@OneToMany` (1 - N)
Mối quan hệ 2 chiều phổ biến nhất giữa Cha và Con:

```java
// Phía N (Chứa khóa ngoại): ManyToOne
@Entity
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;
}

// Phía 1 (Bên bị sở hữu): OneToMany
@Entity
public class UserEntity {
    @Id
    private Long id;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderEntity> orders = new ArrayList<>();
}
```

### 3. `@ManyToMany` (N - N)
Yêu cầu một bảng trung gian (`JoinTable`):

```java
@Entity
public class StudentEntity {
    @Id
    private Long id;

    @ManyToMany
    @JoinTable(
        name = "student_courses",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private List<CourseEntity> courses = new ArrayList<>();
}
```

---

## ⚙️ Cụ Thể Các Loại `CascadeType`

- **`CascadeType.PERSIST`:** Khi `save()` entity Cha, tự động `save()` entity Con.
- **`CascadeType.MERGE`:** Khi `update()` entity Cha, tự động `update()` entity Con.
- **`CascadeType.REMOVE`:** Khi `delete()` entity Cha, tự động `delete()` tất cả entity Con liên quan trong DB.
- **`CascadeType.ALL`:** Bao gồm tất cả các quyền ở trên.
- **`orphanRemoval = true`:** Nếu xóa 1 entity Con ra khỏi danh sách `List` của Cha ➔ Hibernate tự động bắn lệnh `DELETE` entity Con đó khỏi DB.

---

## 💡 Fetching Strategy: `LAZY` vs `EAGER`

- **`FetchType.LAZY` (Khuyên dùng 99%):** Chỉ load dữ liệu con từ DB lên khi gọi hàm `getOrders()`. Giúp ứng dụng chạy nhanh và tiết kiệm bộ nhớ.
- **`FetchType.EAGER`:** Luôn luôn tự động `JOIN` lấy dữ liệu con lên ngay lập tức. Dễ gây ra lỗi **N+1 Query Problem** khiến ứng dụng bị giật lag nghiêm trọng.

---

## 🧭 BỘ CẨM NANG CHI TIẾT TỪNG BƯỚC (ULTIMATE GUIDE 3 DẠNG BÀI TOÁN)

### 🗂️ DẠNG 1: QUAN HỆ 1 - N (Ví dụ: Danh Mục `Category` & Sản Phẩm `Product`)
1. **Nghiệp vụ:** 1 `Category` chứa N `Product`, 1 `Product` thuộc 1 `Category` ➔ **Quy về 1-N**.
2. **ProductEntity (Bên N):** Biến đơn `category` ➔ `@ManyToOne` + `@JoinColumn(name = "category_id")` *(Tạo cột khóa ngoại `category_id`)*.
3. **CategoryEntity (Bên 1):** Biến LIST `products` ➔ `@OneToMany(mappedBy = "category")` *(Nối về biến `category`)*.

### 🗂️ DẠNG 2: QUAN HỆ 1 - 1 (Ví dụ: Người Dùng `User` & Hồ Sơ `UserProfile`)
1. **Nghiệp vụ:** 1 `User` có 1 `UserProfile`, 1 `UserProfile` thuộc 1 `User` ➔ **Quy về 1-1**.
2. **UserEntity (Bên giữ FK):** Biến đơn `profile` ➔ `@OneToOne` + `@JoinColumn(name = "profile_id")` *(Tạo cột khóa ngoại `profile_id`)*.
3. **UserProfileEntity (Bên phụ):** Biến đơn `user` ➔ `@OneToOne(mappedBy = "profile")` *(Nối về biến `profile`)*.

### 🗂️ DẠNG 3: QUAN HỆ N - N (Ví dụ: Sinh Viên `Student` & Môn Học `Course`)
1. **Nghiệp vụ:** 1 `Student` học N `Course`, 1 `Course` có N `Student` ➔ **Quy về N-N (Cần Bảng Trung Gian)**.
2. **StudentEntity (Bên làm chủ):** Biến LIST `courses` ➔ `@ManyToMany` + `@JoinTable(name = "student_courses", ...)` *(Tạo bảng trung gian)*.
3. **CourseEntity (Bên phụ):** Biến LIST `students` ➔ `@ManyToMany(mappedBy = "courses")` *(Nối về biến `courses`)*.




