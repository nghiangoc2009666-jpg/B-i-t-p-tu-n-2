-- Chạy 1 lần trong SSMS. Bảng sẽ do Hibernate tự tạo khi chạy ứng dụng (hbm2ddl.auto=update).
IF DB_ID('ShoppingServiceJPA') IS NULL
    CREATE DATABASE ShoppingServiceJPA;
GO
