# 📜 OpenAPI Specification (OAS) & Swagger

Trong bài so sánh triển khai API trước, ta đã biết: **REST không bắt buộc contract**. 
Hậu quả trong thực tế là:
- Backend code một đằng, tài liệu viết trên Confluence/Notion một nẻo (và thường bị bỏ quên không cập nhật).
- Frontend gọi API bị lỗi vì backend âm thầm đổi tên trường dữ liệu hoặc kiểu dữ liệu (`int` thành `string`).
- Muốn test API lại phải lọ mọ gõ từng endpoint vào Postman.

👉 **OpenAPI Specification (OAS)** ra đời để chấm dứt tình trạng "vô kỷ luật" đó của REST!

---

## 💡 1. OpenAPI Là Gì? (Ẩn Dụ Thực Tế)

> **OpenAPI chính là "Bản vẽ thiết kế kỹ thuật" (Blueprint) hoặc "Bản hợp đồng pháp lý" chuẩn quốc tế cho REST API.**

Thay vì giải thích bằng lời, OpenAPI mô tả toàn bộ hệ thống API của bạn dưới dạng **một file chuẩn hóa (`YAML` hoặc `JSON`)**:
- Có những URL nào? (`/users`, `/orders`)
- Nhận method nào? (`GET`, `POST`, `PUT`, `DELETE`)
- Cần truyền tham số gì? (Header, Query param, Path param, Body)
- Dữ liệu trả về hình thù ra sao? (Kiểu dữ liệu, format, status code 200, 400, 404, 500)
- Xác thực bằng cách nào? (Bearer Token, API Key, OAuth2)

---

## ⚖️ 2. Phân Biệt: OpenAPI vs. Swagger (Rất Dễ Nhầm!)

Rất nhiều lập trình viên gọi lẫn lộn hai khái niệm này. Thực chất:

```
┌────────────────────────────────────────────────────────┐
│             OpenAPI = TIÊU CHUẨN (SPEC)                │
│    (Tương tự như Hiến pháp / Luật giao thông)          │
└────────────────────────────────────────────────────────┘
                           ▲
                           │ Được hỗ trợ & hiện thực bởi
                           ▼
┌────────────────────────────────────────────────────────┐
│             Swagger = BỘ CÔNG CỤ (TOOLS)               │
│   (Gồm Swagger UI, Swagger Editor, Swagger Codegen)    │
└────────────────────────────────────────────────────────┘
```

* **Lịch sử ngắn**: Ban đầu (2010), công ty SmartBear tạo ra công cụ và đặt tên chuẩn là **Swagger**. Đến năm 2015, họ hiến tặng đặc tả này cho quỹ Linux Foundation và đổi tên chuẩn chính thức thành **OpenAPI Specification (OAS)**.
* **Tóm lại**: 
  - File định nghĩa định dạng gọi là **OpenAPI Spec** (`openapi.yaml` hoặc `openapi.json`).
  - Giao diện web xanh xanh đẹp mắt bạn hay mở lên để bấm "Try it out" gọi là **Swagger UI**.

---

## 🧬 3. Giải Phẫu Một File OpenAPI (YAML)

Một file OpenAPI 3.0 chuẩn thường có các phần cốt lõi sau:

```yaml
openapi: 3.0.3
info:
  title: User Management API
  version: 1.0.0
  description: API quản lý người dùng hệ sinh thái E-commerce

servers:
  - url: https://api.example.com/v1
    description: Production Server
  - url: http://localhost:8080/v1
    description: Local Dev

paths:
  /users/{id}:
    get:
      summary: Lấy thông tin user theo ID
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: Thành công
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/UserResponse'
        '404':
          description: Không tìm thấy user

components:
  schemas:
    UserResponse:
      type: object
      properties:
        id:
          type: integer
          example: 123
        fullName:
          type: string
          example: "Nguyen Van A"
        email:
          type: string
          format: email
          example: "a.nguyen@example.com"
      required:
        - id
        - fullName
        - email

  securitySchemes:
    BearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT
```

---

## 🔄 4. Hai Trường Phái Triển Khai Trong Dự Án Thực Tế

Trong các công ty phần mềm, có 2 cách áp dụng OpenAPI:

### Cách 1: Code-First (Phổ biến ở dự án vừa & nhỏ)
1. Backend developer nhảy vào viết code controller (Java Spring Boot, Go, NestJS,...).
2. Gắn các annotations / decorators vào code (vd: `@Operation`, `@ApiResponse`, `@Schema`).
3. Dùng thư viện (như `springdoc-openapi`) tự động quét code và render ra Swagger UI tại đường dẫn `/swagger-ui/index.html`.
* **Ưu điểm**: Nhanh, tiện, code tới đâu sinh docs tới đó.
* **Nhược điểm**: Backend code xong thì Frontend mới có docs để làm theo; nếu Backend sửa code mà quên sửa annotation thì docs bị sai.

### Cách 2: Design-First / Contract-First (Chuẩn quốc tế & Microservices lớn)
1. Team Lead / Solution Architect viết file `openapi.yaml` trước.
2. Mở họp bàn với Frontend, Mobile, QA để chốt contract (Data types, Endpoints, Mã lỗi).
3. Sau khi chốt file YAML:
   - **Frontend**: Dùng Mock Server (như *Prism*) chạy từ file YAML để code giao diện ngay lập tức mà không cần chờ Backend!
   - **Backend**: Dùng công cụ `openapi-generator` tự sinh ra khung Controller / Interface, chỉ việc điền logic xử lý DB.
* **Ưu điểm**: Tính nhất quán cực cao, Frontend và Backend chạy song song (tiết kiệm 50% thời gian chờ đợi).
* **Nhược điểm**: Cần kỹ năng viết file YAML chuẩn và văn hóa kỷ luật nhóm cao.

---

## 🚀 5. Hệ Sinh Thái Cực Mạnh Của OpenAPI

Khi bạn có một file `openapi.yaml`, cả một "kho vũ khí" tự động hóa mở ra:

1. **Tự động sinh tài liệu tương tác (Swagger UI / Redoc)**:
   - Giao diện trực quan cho phép Tester / Developer bấm nút gọi API trực tiếp trên trình duyệt, xem mẫu request/response.
2. **Tự sinh Client SDK (OpenAPI Generator)**:
   - Một lệnh CLI sinh ra toàn bộ code gọi API bằng TypeScript (cho React/Vue), Dart (cho Flutter), Swift (iOS), Kotlin (Android). Frontend không cần viết thủ công `fetch()` hay `axios` từng endpoint!
3. **Mocking Server tự động (Prism / WireMock)**:
   - Giả lập một server trả về dữ liệu mẫu chỉ với 1 dòng lệnh: `prism mock openapi.yaml`.
4. **Contract Testing**:
   - Tự động kiểm tra xem Backend deploy lên có trả đúng định dạng đã cam kết trong file spec hay không.
