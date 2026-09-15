# 🐳 PostgreSQL Với Docker (Using Docker)

Chạy PostgreSQL bằng **Docker** là tiêu chuẩn thực tế tại hầu hết các công ty công nghệ hiện nay. Nó giúp lập trình viên Backend cô lập hoàn toàn môi trường, không lo xung đột thư viện với hệ điều hành và thiết lập database mới chỉ trong vài giây.

---

## 🚀 1. Khởi Chạy Nhanh Bằng Lệnh `docker run`

### Câu lệnh một dòng chuẩn nhất:
```bash
docker run --name postgres-dev \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=app_db \
  -p 5432:5432 \
  -v postgres_data:/var/lib/postgresql/data \
  -d postgres:17-alpine
```

### Giải thích các tham số cốt lõi:
- `--name postgres-dev`: Đặt tên dễ nhớ cho container.
- `-e POSTGRES_USER=postgres`: Tài khoản quản trị khởi tạo.
- `-e POSTGRES_PASSWORD=postgres`: Mật khẩu cho user.
- `-e POSTGRES_DB=app_db`: Tên database mặc định được tạo ngay khi khởi động.
- `-p 5432:5432`: Ánh xạ cổng (Port mapping) `Port máy thật : Port trong container`.
- `-v postgres_data:/var/lib/postgresql/data`: **Volume lưu trữ dữ liệu bền vững (Persistence)**. Nếu không có cờ này, khi xóa container thì toàn bộ dữ liệu trong database sẽ mất vĩnh viễn!
- `-d postgres:17-alpine`: Chạy ngầm (`detach`) với image bản Alpine siêu nhẹ (~40MB).

---

## 📦 2. Cấu Hình Chuẩn Bằng `docker-compose.yml` (Khuyên Dùng Cho Dự Án)

Trong dự án thực tế (Spring Boot, NestJS, Go...), bạn nên đặt file `docker-compose.yml` ở root của project:

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:17-alpine
    container_name: backend_postgres
    restart: always
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: mysecretpassword
      POSTGRES_DB: backend_db
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data
      # Tùy chọn: Tự động chạy file SQL khởi tạo ban đầu
      # - ./init.sql:/docker-entrypoint-initdb.d/init.sql:ro
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 5s
      timeout: 5s
      retries: 5

volumes:
  pgdata:
    driver: local
```

### Các lệnh quản lý thông dụng với Compose:
```bash
# Khởi động ở chế độ chạy nền
docker compose up -d

# Xem log database thời gian thực
docker compose logs -f postgres

# Dừng container (vẫn giữ nguyên dữ liệu)
docker compose down

# Xóa container VÀ xóa sạch volume dữ liệu (làm mới từ đầu)
docker compose down -v
```

---

## 🔌 3. Kết Nối Vào PostgreSQL Trong Docker

### Cách 1: Dùng lệnh `psql` từ máy thật (Máy đã có psql)
Vì port `5432` đã được ánh xạ ra máy thật, bạn kết nối bình thường:
```bash
psql -h localhost -p 5432 -U postgres -d backend_db
```

### Cách 2: Nhảy thẳng vào container và mở `psql`
Cách này áp dụng khi máy bạn hoàn toàn **không cài bất kỳ công cụ PostgreSQL nào**:
```bash
# Cú pháp: docker exec -it <tên_container> psql -U <user> -d <db>
docker exec -it backend_postgres psql -U postgres -d backend_db
```

---

## ⚠️ 4. Các Lưu Ý Sống Còn Khi Dùng Docker Với Database

1. **Luôn dùng Docker Volume:** Tuyệt đối không quên `-v` nếu không muốn mất toàn bộ dữ liệu sau khi restart máy hoặc build lại container.
2. **Khởi tạo dữ liệu mẫu tự động:** Bất kỳ file `.sql` hoặc `.sh` nào đặt trong thư mục `/docker-entrypoint-initdb.d/` bên trong container sẽ được chạy tự động **lần đầu tiên** database khởi tạo.
3. **Đổi Port nếu trùng:** Nếu máy bạn đã cài PostgreSQL chạy nền ở port 5432, hãy map sang port khác để tránh lỗi xung đột cổng (`Bind for 0.0.0.0:5432 failed: port is already allocated`):
   ```bash
   -p 5433:5432   # Kết nối từ code sẽ dùng port 5433
   ```
