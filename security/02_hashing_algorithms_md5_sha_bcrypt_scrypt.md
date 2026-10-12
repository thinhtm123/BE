# 🔐 Thuật Toán Băm (Hashing Algorithms): MD5, SHA, scrypt, bcrypt

> **Đúng vậy, cả 4 cái này đều là các Thuật toán Băm (Hashing Algorithms)** — chức năng của chúng là biến một chuỗi dữ liệu đầu vào bất kỳ (ví dụ mật khẩu, file tải về) thành một chuỗi ký tự có độ dài cố định, và mang tính chất **một chiều (One-way - không thể dịch ngược lại)**.

Tuy nhiên, trong kỹ nghệ Backend, 4 thuật toán này được chia làm **2 trường phái hoàn toàn đối nghịch nhau về mục đích sử dụng**:

---

## ⚡ 1. Bảng Phân Chia "Lãnh Địa" Thực Tế

```
                                  CÁC THUẬT TOÁN BĂM (HASHING)
                                                │
                 ┌──────────────────────────────┴──────────────────────────────┐
                 ▼                                                             ▼
     NHÓM 1: BĂM CỰC NHANH (Fast Hashing)                         NHÓM 2: CỐ TÌNH BĂM CHẬM (Slow Hashing)
             [ MD5, SHA-1, SHA-256 ]                                      [ bcrypt, scrypt, Argon2 ]
                 │                                                             │
     • Mục đích: Kiểm tra file, chữ ký số, Git                    • Mục đích: CHUYÊN DÙNG LƯU MẬT KHẨU
     • Tốc độ: Hàng triệu phép tính/giây                          • Tốc độ: Cố tình mất 0.1s - 0.3s / lần
     ❌ CẤM DÙNG ĐỂ BĂM MẬT KHẨU                                  ✅ TIÊU CHUẨN VÀNG CHO MẬT KHẨU
```

---

## 🏎️ 2. Nhóm 1: MD5 & SHA — "Vua Tốc Độ" (Dành Cho Toàn Vẹn Dữ Liệu)

### MD5 (Message Digest 5) & SHA (Secure Hash Algorithm)
- **Đặc điểm**: Được thiết kế để băm dữ liệu cực nhanh, tốn rất ít CPU.
- **Ứng dụng thực tế**:
  1. **Kiểm tra tính toàn vẹn (Checksum)**: Khi bạn tải một file cài đặt `.iso` 5GB về máy, bạn so sánh mã SHA-256 tải về xem file có bị lỗi hay bị chèn mã độc không.
  2. **Quản lý mã nguồn (Git)**: Mọi commit trong Git đều được định danh bằng một chuỗi băm (SHA-1).
  3. **Chữ ký số & Blockchain**: Bitcoin dùng thuật toán `SHA-256`.

### 🛑 Vì sao CẤM TUYỆT ĐỐI dùng MD5 & SHA để lưu mật khẩu?
1. **Quá nhanh = Quá nguy hiểm**: Một card đồ họa (GPU) ngày nay có thể tính toán **hàng chục tỷ mã MD5/SHA mỗi giây**. Nếu hacker lấy được database, chúng chỉ mất vài phút để vét cạn (Brute-force) toàn bộ mật khẩu người dùng!
2. **Dễ bị tấn công Rainbow Table**: Bảng tra cứu dịch ngược từ chuỗi băm ra mật khẩu phổ biến.
3. **MD5 và SHA-1 đã bị "bẻ gãy" (Collision Attack)**: Các nhà nghiên cứu đã tìm ra cách tạo ra 2 file hoàn toàn khác nhau nhưng lại có cùng mã băm MD5.

---

## 🐢 3. Nhóm 2: bcrypt & scrypt — "Cố Tình Làm Chậm" (Dành Riêng Cho Mật Khẩu)

Để bảo vệ mật khẩu, các chuyên gia mật mã học đã tạo ra thuật toán **Key Derivation Function (KDF)** với triết lý: **Phải làm cho nó chạy thật chậm và tốn tài nguyên!**

### bcrypt (Chuẩn mực phổ biến nhất trong Spring Boot / Node.js)
- **Tự động thêm Salt**: Mỗi lần băm, bcrypt tự sinh ra một chuỗi ngẫu nhiên (Salt) ghép vào mật khẩu. Dù hai user có cùng mật khẩu `123456`, chuỗi băm ra vẫn khác hoàn toàn nhau -> **Vô hiệu hóa 100% Rainbow Table**.
- **Điều chỉnh độ khó (Work Factor / Cost)**:
  - Bạn có thể cấu hình Cost từ `4` đến `31` (mặc định là `10` hoặc `12`).
  - Mỗi khi tăng thêm 1 bậc, thời gian tính toán tăng gấp đôi.
  - Server mất **0.2 giây** để verify mật khẩu khi user đăng nhập (user không nhận ra độ trễ).
  - Nhưng với hacker, để thử 1 tỷ mật khẩu, chúng sẽ mất:
    $$1.000.000.000 \times 0.2\text{ giây} \approx 6.3\text{ NĂM!}$$

---

### scrypt (Bậc thầy chống phần cứng chuyên dụng ASIC / GPU)
- **Tại sao cần scrypt khi đã có bcrypt?**
  Hacker chuyên nghiệp có thể chi tiền chế tạo ra các con chip phần cứng chuyên dụng (ASIC, FPGA) chỉ để băm bcrypt song song hàng triệu phép tính/giây.
- **Vũ khí của scrypt: Memory-Hard (Bắt tốn thật nhiều RAM)**:
  - Khi tính toán, scrypt bắt buộc phải cấp phát một vùng nhớ RAM lớn (ví dụ 16MB đến 64MB cho MỘI phép băm).
  - Chip phần cứng của hacker có thể có hàng triệu lõi tính toán, nhưng không thể nhét hàng triệu thanh RAM vào một con chip tí hon được -> Hacker bị "nghẽn RAM" và phá sản kế hoạch vét cạn!

---

## 🏆 4. Bảng So Sánh 4 Thuật Toán

| Thuật toán | Loại | Tốc độ | Chống Brute-force | Mục đích sử dụng chuẩn |
|---|---|---|---|---|
| **MD5** | Fast Hash | 🚀 Cực nhanh | ❌ Cực kém (Đã bị phá vỡ) | Chỉ dùng checksum kiểm tra file cũ. |
| **SHA (SHA-256)** | Fast Hash | 🚀 Rất nhanh | ❌ Kém (Quá nhanh cho pass) | **Checksum, Chữ ký số, Blockchain, HMAC**. |
| **bcrypt** | Slow Hash (CPU-hard) | 🐢 Chậm có kiểm soát | 🔒 Rất cao (Cost factor) | **Lưu mật khẩu người dùng** (Spring Security, Django). |
| **scrypt** | Slow Hash (Memory-hard) | 🐢 Chậm + Tốn RAM | 🔒 Cực cao (Chống ASIC/GPU) | **Lưu mật khẩu cao cấp, Ví tiền điện tử**. |

---

## 🎯 5. Lời Khuyên Cho Backend Developer

1. **Lưu mật khẩu người dùng**: Dùng **`bcrypt`** (với Cost Factor = 10 đến 12) hoặc chuẩn mới nhất của thế giới là **`Argon2`** (quán quân giải Password Hashing Competition).
2. **Kiểm tra tính toàn vẹn của dữ liệu / Token**: Dùng **`SHA-256`** hoặc **`SHA-512`** (kết hợp HMAC trong JWT).
3. **MD5 & SHA-1**: Xem như đã lỗi thời (deprecated), không dùng cho bất kỳ tính năng bảo mật mới nào.
