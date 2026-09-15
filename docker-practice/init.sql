-- File init.sql này sẽ tự động chạy trong lần đầu tiên container khởi tạo
CREATE TABLE IF NOT EXISTS categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price NUMERIC(10, 2) NOT NULL,
    stock INT DEFAULT 0,
    category_id INT REFERENCES categories(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Nạp sẵn dữ liệu mẫu (Seed data)
INSERT INTO categories (name) VALUES ('Laptop'), ('Điện thoại'), ('Phụ kiện');

INSERT INTO products (name, price, stock, category_id) VALUES
('MacBook Pro M3', 1999.99, 10, 1),
('Dell XPS 15', 1499.00, 15, 1),
('iPhone 16 Pro', 1199.99, 30, 2),
('Bàn phím cơ Keychron', 89.00, 50, 3);
