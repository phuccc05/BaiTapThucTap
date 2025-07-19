CREATE DATABASE task_manager2
Go
Use task_manager2
Go

CREATE TABLE users (
                       id INT IDENTITY(1,1) PRIMARY KEY,
                       username NVARCHAR(100) NOT NULL UNIQUE,
                       email VARCHAR(150) NOT NULL UNIQUE,
                       mat_khau VARCHAR(255) NOT NULL,
                       gioi_tinh BIT,
                       ngay_tao DATETIME DEFAULT GETDATE(),
                       ngay_sua DATETIME NULL
);

CREATE TABLE tasks (
                       id INT IDENTITY(1,1) PRIMARY KEY,
                       title NVARCHAR(200) NOT NULL,
                       mo_ta NVARCHAR(500) NOT NULL,
                       trang_thai bit DEFAULT 1,
                       id_user int NOT NULL,
                       ngay_tao DATETIME DEFAULT GETDATE(),
                       ngay_sua DATETIME NULL
);

INSERT INTO users (username, email, mat_khau, gioi_tinh)
VALUES
    ('admin', 'admin@example.com', '$2a$10$k8JSkV4U4m70ME9ShAof4uW1frpn1A1cyoHpBrZqx0b9t5dKQXK96',0), -- 123456
    ('john_doe', 'john@example.com', '$2a$10$FDEGbFw6axabKgoijbBrKefRA9CpyglLaMxVUZacZYxyKi3JqKqsy', 0), -- password123
    ('jane_smith', 'jane@example.com', '$2a$10$P1Me34vlY8Mxh7xoj/N0u.pRu6LIG0F1WDl4vnYm9VnNLYeA94qIq', 1), -- jane@pass
    ('hungtran', 'hung@example.com', '$2a$10$msApKL1U7aNV8vxzZ6f2fOJZSv9cOYPbkeAxpQe7P7B7ZwXjTj8tW', 0), -- hung123
    ('linhvu', 'linh@example.com', '$2a$10$0u/UY0cEMKDvfrmyqaOxGuEzMdzZpeZRoNW4bVmsfNmL9lmraGeF2', 1); -- linh@123


INSERT INTO tasks (title, mo_ta, trang_thai, id_user)
VALUES
    (N'Tạo giao diện đăng nhập', N'Thiết kế giao diện đăng nhập cho hệ thống.', 0, 1),
    (N'Tạo API đăng ký', N'Xây dựng API /register để người dùng tạo tài khoản.', 1, 2),
    (N'Hoàn thiện xác thực JWT', N'Tích hợp JWT để xác thực và bảo vệ API.', 1, 5),
    (N'Viết tài liệu hướng dẫn sử dụng', N'Tài liệu dành cho người dùng cuối.', 0, 3),
    (N'Sửa lỗi hiển thị trên mobile', N'Sửa lỗi responsive khi xem trên điện thoại.', 1, 4);

select * from users
select * from tasks