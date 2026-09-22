# 🔑 Object Privileges in PostgreSQL (Quyền Hạn Trên Đối Tượng CSDL)

**Object Privileges (Quyền đối tượng)** là hệ thống kiểm soát quyền hạn chi tiết trong PostgreSQL, quy định một User / Role cụ thể được phép **làm gì** (Đọc, Thêm, Sửa, Xóa, Thực thi) trên **đối tượng nào** (Bảng, View, Sequence, Hàm, Schema).

Đây là nền tảng của nguyên tắc bảo mật nổi tiếng: **Principle of Least Privilege (Nguyên tắc đặc quyền tối thiểu)** — Chỉ cấp vừa đủ quyền để làm việc, không cấp dư thừa!

---

## 🧭 1. Ma Trận Quyền Hạn Theo Từng Loại Đối Tượng

Mỗi loại đối tượng trong CSDL sẽ hỗ trợ các loại quyền khác nhau:

| Đối tượng (Object) | Các quyền áp dụng được (`Privileges`) | Ý nghĩa thực tế |
| :--- | :--- | :--- |
| **TABLE (Bảng)** | `SELECT`, `INSERT`, `UPDATE`, `DELETE`, `TRUNCATE`, `REFERENCES` | Quyền đọc, thêm, sửa, xóa dữ liệu và tạo khóa ngoại tham chiếu. |
| **VIEW (Bảng ảo)** | `SELECT`, `INSERT`, `UPDATE`, `DELETE` | Giống Table (nếu là Updatable View). |
| **SEQUENCE (Tự tăng)**| `USAGE`, `SELECT`, `UPDATE` | Bắt buộc phải có `USAGE` thì cột `SERIAL / BIGSERIAL` mới tự nhảy số được khi INSERT. |
| **FUNCTION (Hàm/Proc)**| `EXECUTE` | Quyền được phép gọi và thực thi hàm. |
| **SCHEMA (Thư mục)** | `USAGE`, `CREATE` | `USAGE`: Cho phép truy cập vào bên trong schema; `CREATE`: Cho phép tạo bảng mới trong schema đó. |
| **DATABASE** | `CONNECT`, `CREATE`, `TEMPORARY` | Cho phép đăng nhập vào DB, tạo schema mới. |

---

## ⚡ 2. Cú Pháp Hai Lệnh Cốt Lõi: `GRANT` (Cấp) & `REVOKE` (Thu hồi)

### 1️⃣ Cấp quyền (`GRANT`)
```sql
-- Cú pháp chung:
-- GRANT [danh_sach_quyen] ON [loai_doi_tuong] [ten_doi_tuong] TO [ten_role];

-- Ví dụ 1: Chỉ cho phép 'sale_user' được ĐỌC bảng products
GRANT SELECT ON TABLE products TO sale_user;

-- Ví dụ 2: Cấp quyền Đọc và Sửa, nhưng CẤM xóa bảng orders
GRANT SELECT, INSERT, UPDATE ON TABLE orders TO app_user;

-- Ví dụ 3: Cấp toàn bộ quyền trên TẤT CẢ các bảng trong schema public
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO app_admin;

-- Ví dụ 4: Cấp quyền gọi hàm tính thuế
GRANT EXECUTE ON FUNCTION calculate_tax(NUMERIC) TO billing_user;
```

### 2️⃣ Thu hồi quyền (`REVOKE`)
Khi nhân viên chuyển dự án hoặc muốn tước bỏ quyền nguy hiểm:
```sql
-- Thu hồi quyền DELETE trên bảng orders
REVOKE DELETE ON TABLE orders FROM app_user;

-- Thu hồi toàn bộ quyền
REVOKE ALL PRIVILEGES ON TABLE products FROM sale_user;
```

---

## 🚀 3. "Cái Bẫy" Kinh Điển Khi Cấp Quyền Cho Bảng Tự Tăng (`SERIAL`)

Rất nhiều bạn mới làm Backend gặp lỗi này:
> *"Em đã chạy `GRANT INSERT ON users TO app_user;` rồi nhưng khi code Backend chạy lệnh INSERT vẫn báo lỗi: `permission denied for sequence users_id_seq`!"*

👉 **Nguyên nhân:** Khi tạo bảng có cột `id SERIAL`, Postgres tự động sinh ra một **Sequence** ngầm phía sau để nhảy số `1, 2, 3...`. Khi INSERT, user bắt buộc phải có thêm quyền `USAGE` trên sequence đó!

```sql
-- Cách cấp quyền chuẩn xác 100% cho mọi bảng tự tăng ID:
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO app_user;
```

---

## 🔮 4. Tự Động Cấp Quyền Cho Bảng Mới Tạo Trong Tương Lai (`ALTER DEFAULT PRIVILEGES`)

Nếu bạn cấp quyền bằng lệnh `GRANT ALL TABLES`, nó **chỉ áp dụng cho các bảng đang có sẵn**. Ngày mai bạn tạo bảng mới thì user kia lại không có quyền!

Để giải quyết triệt để, PostgreSQL cung cấp lệnh **Default Privileges**:
```sql
-- Bất kỳ bảng nào được tạo ra trong tương lai, tự động cấp quyền SELECT cho 'read_only_user'
ALTER DEFAULT PRIVILEGES IN SCHEMA public 
GRANT SELECT ON TABLES TO read_only_user;
```

---

## 🔍 5. Cách Kiểm Tra Quyền Hạn Trong `psql` / DBeaver

- Trong `psql`, gõ:
  - `\dp <ten_bang>` hoặc `\z <ten_bang>`: Xem bảng phân quyền chi tiết.
- Giải mã ký hiệu quyền viết tắt của PostgreSQL:
  - `r` = `SELECT` (Read)
  - `a` = `INSERT` (Append)
  - `w` = `UPDATE` (Write)
  - `d` = `DELETE`
  - `D` = `TRUNCATE`
  - `x` = `REFERENCES`
  - `X` = `EXECUTE`
