# 🧼 SOAP API (Simple Object Access Protocol)

**SOAP (Giao thức truy cập đối tượng đơn giản)** là một giao thức chuẩn hóa chính thức (chuẩn W3C) ra đời từ cuối những năm 1990, được thiết kế để các ứng dụng phân tán có thể trao đổi dữ liệu có cấu trúc với tính an ninh và toàn vẹn giao dịch cao nhất.

Dù các ứng dụng web và startup hiện đại ưu tiên dùng REST và GraphQL vì sự gọn nhẹ, **SOAP vẫn là "trụ cột không thể thay thế"** trong các hệ thống doanh nghiệp cấp cao (Enterprise): Ngân hàng (Core Banking), Tài chính quốc tế, Bảo hiểm và Viễn thông.

---

## 🏛️ 1. Bản Chất Kỹ Thuật Của SOAP

- **Giao thức chuẩn (Protocol):** Khác với REST (chỉ là phong cách kiến trúc), SOAP là một **giao thức nghiêm ngặt với các quy tắc bắt buộc**.
- **100% Định dạng XML:** SOAP chỉ sử dụng XML để đóng gói thông điệp, không hỗ trợ JSON hay định dạng khác.
- **Độc lập giao thức truyền tải:** SOAP có thể chạy trên HTTP, HTTPS, nhưng cũng có thể chạy trên SMTP (Email), TCP hoặc JMS (Message Queues).

---

## ✉️ 2. Cấu Trúc Bức Thư SOAP (SOAP Envelope)

Mọi yêu cầu hoặc phản hồi trong SOAP đều được bọc trong một "phong bì" XML gồm 4 phần:

```xml
<?xml version="1.0"?>
<soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope">
    
    <!-- 1. Header (Tùy chọn): Chứa thông tin bảo mật, chữ ký số, routing -->
    <soap:Header>
        <wsse:Security xmlns:wsse="http://docs.oasis-open.org/wss/...">
            <wsse:UsernameToken>
                <wsse:Username>bank_api_user</wsse:Username>
                <wsse:Password>SecurePassword123</wsse:Password>
            </wsse:UsernameToken>
        </wsse:Security>
    </soap:Header>

    <!-- 2. Body (Bắt buộc): Dữ liệu nghiệp vụ thật (lệnh gọi hàm và tham số) -->
    <soap:Body>
        <m:GetAccountBalance xmlns:m="http://bank.com/balance">
            <m:AccountNumber>123456789</m:AccountNumber>
        </m:GetAccountBalance>
    </soap:Body>

    <!-- 3. Fault (Tùy chọn): Nằm trong Body, dùng để báo lỗi chi tiết nếu có sự cố -->

</soap:Envelope>
```

---

## 📜 3. WSDL (Web Services Description Language) — "Bản Hợp Đồng Điện Tử"

Điểm mạnh nhất của SOAP là **WSDL** (thường truy cập qua đường dẫn `http://server/service?wsdl`):
- WSDL là một file XML mô tả chính xác 100%: Service này có những hàm nào? Tham số truyền vào gồm kiểu dữ liệu gì (chuỗi, số, ngày tháng)? Giá trị trả về ra sao?
- **Tính năng tự sinh code (Code Generation):** Lập trình viên Backend (Java, C#) chỉ cần cắm link WSDL vào IDE, nó sẽ tự động sinh ra toàn bộ các class DTO và Client gọi API mà không cần tự tay gõ code!

---

## ⚖️ 4. Ưu Điểm & Nhược Điểm

### ✅ Ưu điểm:
1. **Bảo mật chuẩn cấp độ cao nhất (`WS-Security`):** Hỗ trợ mã hóa từng phần của tài liệu XML và ký chữ ký số chống chối bỏ.
2. **Hỗ trợ Giao dịch phân tán (ACID qua `WS-AtomicTransaction`):** Đảm bảo chuyển tiền giữa 2 ngân hàng độc lập tuân thủ quy tắc "hoặc cùng thành công, hoặc cùng rollback".
3. **Kiểm tra kiểu dữ liệu nghiêm ngặt:** WSDL đảm bảo không bao giờ có chuyện gửi nhầm trường hay sai kiểu.

### ❌ Nhược điểm:
1. **Nặng nề & Chậm chạp:** File XML dài dòng (verbose) làm tốn băng thông mạng và tốn nhiều CPU để phân tích cú pháp (parse XML).
2. **Khó học, cấu hình phức tạp:** Không thân thiện với ứng dụng Web / Mobile Frontend.

---

## 🎯 Tóm Tắt Khi Nào Backend Phải Dùng SOAP?

> *"Bạn sẽ phải dùng SOAP khi làm việc trong ngành **FinTech, Ngân hàng, Cổng thanh toán quốc tế** hoặc khi tích hợp với các hệ thống **Legacy Core Banking** đã được xây dựng từ 10 - 20 năm trước."*
