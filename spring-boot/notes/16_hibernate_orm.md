# 16. Hibernate & ORM Framework

Hibernate là một framework ORM (Object-Relational Mapping) hàng đầu cho Java, cung cấp cơ chế ánh xạ giữa các lớp Java (Classes) và các bảng trong Cơ sở dữ liệu quan hệ (Tables).

---

## ❓ So Sánh JPA vs Hibernate

- **JPA (Jakarta Persistence API):** Là bộ **Specification (Quy chuẩn / Interface)** định nghĩa các chuẩn ORM trong Java.
- **Hibernate:** Là **Implementation (Bộ cài đặt thực tế)** phổ biến nhất thực thi các chuẩn của JPA.

---

## 🛠️ Các Annotation Ánh Xạ Cơ Bản

- `@Entity`: Khai báo Class đại diện cho một bảng Database.
- `@Table(name = "table_name")`: Chỉ định tên bảng trong DB.
- `@Id`: Đánh dấu khóa chính (Primary Key).
- `@GeneratedValue(strategy = GenerationType.IDENTITY)`: Tự động tăng khóa chính (Auto Increment).
- `@Column`: Cấu hình tên cột, tính duy nhất (`unique`), độ dài (`length`), chấp nhận null (`nullable`).
- `@Transient`: Đánh dấu thuộc tính KHÔNG lưu vào Database.

---

## 🔗 Quản Lý Mối Quan Hệ (Relationships)

1. **`@OneToOne`:** Mối quan hệ 1 - 1 (Ví dụ: `User` - `UserProfile`).
2. **`@ManyToOne`:** Mối quan hệ N - 1 (Ví dụ: Nhiều `Order` thuộc về 1 `User`).
3. **`@OneToMany`:** Mối quan hệ 1 - N (Ví dụ: 1 `User` có nhiều `Order`).
4. **`@ManyToMany`:** Mối quan hệ N - N (Ví dụ: `Student` - `Course`).

```java
@Entity
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id") // Tạo cột Khóa ngoại user_id trong bảng orders
    private UserEntity user;
}
```

---

## 💡 Cấu Hình Hibernate Trong `application.yml`

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update # Tự động tạo/sửa bảng trong DB dựa trên Entity (validate, create, update, none)
    show-sql: true     # In các câu lệnh SQL thực thi ra màn hình console
    properties:
      hibernate:
        format_sql: true # Định dạng câu SQL in ra cho dễ đọc
```

---

## 💡 Tips & Tricks & Cạm Bẫy N+1 Query Problem

1. **Cạm bẫy N+1 Query trong Hibernate:**
   Khi dùng `@OneToMany` hoặc `@ManyToOne` với `FetchType.EAGER`, Hibernate sẽ bắn 1 câu query lấy danh sách danh mục + N câu query con để lấy sản phẩm của từng danh mục ➔ Gây sập Database!
   - **Giải pháp:** Luôn dùng `FetchType.LAZY` và viết câu lệnh `JOIN FETCH` khi cần lấy dữ liệu liên quan.
