# 🏛️ High Level Database Concepts

**High-level database concepts** là các nguyên lý nền tảng để thiết kế, xây dựng và vận hành hệ thống cơ sở dữ liệu **robust (bền vững)**, **efficient (hiệu quả)** và **scalable (có thể mở rộng)**.

---

## 1. Các Khái Niệm Cốt Lõi

### 🔷 ACID — Bộ 4 Tính Chất Giao Dịch
Đảm bảo mọi giao dịch cơ sở dữ liệu được xử lý **an toàn và đáng tin cậy**:

| Tính Chất | Ý Nghĩa |
| :--- | :--- |
| **Atomicity** | Giao dịch thành công trọn vẹn hoặc rollback hoàn toàn — không có trạng thái nửa vời. |
| **Consistency** | Dữ liệu trước và sau giao dịch luôn thỏa mãn tất cả các ràng buộc đã định nghĩa. |
| **Isolation** | Các giao dịch song song không ảnh hưởng lẫn nhau — mỗi giao dịch như chạy một mình. |
| **Durability** | Sau khi `COMMIT`, dữ liệu được lưu vĩnh viễn dù server sập điện ngay sau đó. |

---

### 🔷 Normalization (Chuẩn Hóa Dữ Liệu)
Quy trình tổ chức lại schema để **loại bỏ dữ liệu trùng lặp** và đảm bảo tính toàn vẹn:

- **1NF:** Mỗi ô trong bảng chứa đúng **một giá trị nguyên tử** (không chứa mảng, danh sách lồng nhau).
- **2NF:** Không có cột nào chỉ phụ thuộc vào **một phần** của khóa chính tổ hợp.
- **3NF:** Không có cột nào phụ thuộc vào **cột không phải khóa** khác (loại bỏ phụ thuộc bắc cầu).

> 💡 **Thực tế:** Hầu hết hệ thống Backend cần đạt **3NF** là đủ. Đôi khi chủ động **de-normalize** (giữ lại một số trùng lặp có kiểm soát) để tối ưu hiệu năng đọc dữ liệu.

---

### 🔷 Indexes (Chỉ Mục)
Cấu trúc dữ liệu phụ giúp tăng tốc tìm kiếm, tương tự như **mục lục cuốn sách**:
- Không có Index → Database phải quét **toàn bộ bảng (Full Table Scan)** — O(n).
- Có Index (B-Tree) → Tìm kiếm theo dạng cây nhị phân — O(log n).
- **Đánh đổi:** Index tăng tốc đọc (`SELECT`) nhưng làm chậm ghi (`INSERT/UPDATE/DELETE`) vì phải cập nhật cả index.

---

### 🔷 Transactions (Giao Dịch)
Một **đơn vị công việc logic** gồm nhiều câu lệnh SQL thực thi như một khối thống nhất:

```sql
BEGIN;
  UPDATE accounts SET balance = balance - 500 WHERE id = 1; -- Trừ tiền người gửi
  UPDATE accounts SET balance = balance + 500 WHERE id = 2; -- Cộng tiền người nhận
COMMIT; -- Hoặc ROLLBACK nếu có lỗi
```
> Nếu câu lệnh thứ 2 lỗi → `ROLLBACK` → câu lệnh thứ 1 cũng bị hoàn tác → Tiền không bao giờ "mất".

---

### 🔷 Concurrency Control (Kiểm Soát Truy Cập Đồng Thời)
Quản lý nhiều người dùng đọc/ghi dữ liệu **cùng một lúc** mà không xung đột:
- **PostgreSQL dùng MVCC:** Mỗi giao dịch thấy một *snapshot* nhất quán của dữ liệu tại thời điểm bắt đầu → Đọc không chặn Ghi.
- **Isolation Levels:** `READ COMMITTED` (mặc định), `REPEATABLE READ`, `SERIALIZABLE` (nghiêm ngặt nhất).

---

### 🔷 Database Design (Thiết Kế CSDL)
Quy trình từ yêu cầu nghiệp vụ đến schema thực tế:

```text
Yêu Cầu Nghiệp Vụ
        ↓
Conceptual Model  → ERD (Entity-Relationship Diagram): Vẽ sơ đồ thực thể - quan hệ
        ↓
Logical Model     → Xác định bảng, cột, kiểu dữ liệu, ràng buộc
        ↓
Physical Model    → Tối ưu theo engine cụ thể: thêm index, partition, ...
```

---

## 2. Tóm Tắt

| Khái Niệm | Vai Trò |
| :--- | :--- |
| **ACID** | Đảm bảo giao dịch an toàn, không mất dữ liệu. |
| **Normalization** | Loại bỏ trùng lặp, đảm bảo nhất quán khi cập nhật. |
| **Indexes** | Tăng tốc truy vấn, đánh đổi bằng chi phí ghi. |
| **Transactions** | Bao bọc nhiều lệnh SQL thành một khối nguyên tử. |
| **Concurrency Control** | Cho phép nhiều người dùng đồng thời mà không xung đột. |
| **Database Design** | Quy trình ERD → Logical → Physical schema. |
