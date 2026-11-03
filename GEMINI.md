# Quy Tắc Soạn Thảo Tài Liệu & Sơ Đồ Kỹ Thuật (Documentation & Diagram Rules)

Tài liệu trong kho lưu trữ này phục vụ học tập, tra cứu kiến trúc Backend và hệ thống phân tán. Mọi đóng góp, chỉnh sửa hoặc tạo mới tài liệu Markdown phải tuân thủ nghiêm ngặt các quy tắc sau:

---

## 1. Nguyên Tắc Sơ Đồ Đôi (ASCII Box Art + Mermaid)
Đối với các nội dung về **Kiến trúc hệ thống, Luồng xác thực (Authentication Flows), Vòng đời Request, và Cấu trúc dữ liệu**:
- **Bắt buộc có sơ đồ ASCII Box Art:** Đặt trong khối code block ```` ``` ```` thuần để người đọc mở file code ở bất kỳ trình soạn thảo/terminal/thiết bị nào cũng xem và hiểu được ngay lập tức mà không cần cài extension.
- **Đi kèm sơ đồ Mermaid chuẩn:** Để tạo trải nghiệm trực quan, chuyên nghiệp khi mở cửa sổ Markdown Preview.

---

## 2. Tiêu Chuẩn Viết Mã Mermaid An Toàn (Bulletproof Mermaid)
Để đảm bảo tất cả biểu đồ render mượt mà 100% trên VS Code (`bierner.markdown-mermaid`), GitHub, GitLab và các công cụ preview khác:

### A. Quy tắc đặt nhãn (Labels & Quotes)
1. **Luôn bọc nhãn trong dấu ngoặc kép:** Bất cứ khi nào nhãn chứa dấu ngoặc đơn `()`, dấu gạch chéo `/`, dấu cách, số, icon emoji hoặc ký tự đặc biệt, **bắt buộc** phải dùng cú pháp:
   - ✅ Đúng: `ISP1["Nhà Mạng Viettel / VNPT (Local ISP)"]`
   - ❌ Sai: `ISP1[Nhà Mạng Viettel / VNPT (Local ISP)]` (Dễ gây lỗi Parse error).
2. **Nhãn trên đường nối (Edge Labels):** Khi nhãn trên mũi tên chứa ngoặc đơn `()` hoặc ký hiệu:
   - ✅ Đúng: `Kernel -->|"🚨 CHẶN ĐỨNG (Access Denied)"| Block`
   - ❌ Sai: `Kernel -->|🚨 CHẶN ĐỨNG (Access Denied)| Block`

### B. Quy tắc cho Sequence Diagram
1. **Tuyệt đối không dùng dấu chấm phẩy `;` trong nội dung thông điệp:**
   - Trong Mermaid sequence diagram, dấu `;` được coi là ký tự ngắt câu lệnh.
   - ✅ Đúng: `Header: Set-Cookie: JSESSIONID=sess_999, HttpOnly, Secure`
   - ❌ Sai: `Header: Set-Cookie: JSESSIONID=sess_999; HttpOnly; Secure`
   - Với câu lệnh SQL: Bỏ dấu `;` ở cuối (VD: `SELECT * FROM users LIMIT 100`).
2. **Tuyệt đối không dùng dấu thăng `#` trong nội dung thông điệp:**
   - Trong Mermaid, ký tự `#` bị coi là bắt đầu của comment (chú thích). Viết `#105` hay `#1` sẽ làm Mermaid nuốt chửng toàn bộ nội dung phía sau hoặc gây vỡ cú pháp.
   - ✅ Đúng: `GET /products/105` hoặc `Xem sản phẩm ID 105`
   - ❌ Sai: `Xem sản phẩm #105`
3. **Không bọc dấu ngoặc kép `"` quanh tên hiển thị của Actor / Participant:**
   - Khác với Flowchart, cú pháp `actor User as "📱 Người dùng"` trong Sequence Diagram sẽ in nguyên cả 2 dấu ngoặc kép ra màn hình hiển thị.
   - ✅ Đúng: `actor User as 📱 Người dùng`
   - ❌ Sai: `actor User as "📱 Người dùng"`
4. **Thứ tự của `Note over A,B`:**
   - Thứ tự hai Actor bắt buộc phải theo chiều từ Trái sang Phải tương ứng với thứ tự khai báo:
   - ✅ Đúng: `Note over Browser,Server: Thông điệp`
   - ❌ Sai: `Note over Server,Browser: Thông điệp` (Gây đảo lộn layout hoặc lỗi render).
5. **Hạn chế dùng khối `alt / else / end` phân nhánh phức tạp:**
   - Khối `alt / else / end` lồng ghép thường xuyên bị lỗi crash renderer / không hiển thị trên VS Code Preview.
   - ✅ Khuyên dùng: Dùng `Note over A,B: TRƯỜNG HỢP 1: ...` và `Note over A,B: TRƯỜNG HỢP 2: ...` để thể hiện luồng tuần tự rành mạch, hiển thị 100% trên mọi preview.

### C. Quy tắc cho Flowchart & Subgraph
1. **Không dùng `direction TB` hoặc `direction LR` bên trong `subgraph`:**
   - Lệnh `direction` bên trong subgraph gây lỗi tương thích trên nhiều phiên bản Mermaid markdown-it của IDE. Hãy để hướng luồng phụ thuộc vào khai báo gốc `graph TD` hoặc `flowchart LR`.
2. **Không nối mũi tên trực tiếp từ ID của Subgraph:**
   - ✅ Đúng: Nối từ một node cụ thể bên trong subgraph hoặc định nghĩa node đại diện.
   - ❌ Sai: `subgraph_id --> node`

---

## 3. Phong Cách Trình Bày (Styling & Aesthetics)
- Đặt tên node rõ ràng, có emoji đại diện (VD: `👑 Master`, `👥 Replica`, `🛡️ Sentinel`, `⚙️ Server`).
- Sử dụng bảng màu `classDef` hài hòa, độ tương phản cao, hiện đại (dark slate, emerald green, indigo blue, ruby red).
