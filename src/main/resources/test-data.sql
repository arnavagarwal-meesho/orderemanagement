-- Insert test customer (password is 'password123' encoded with BCrypt)
INSERT INTO customers (name, email, address, password) 
VALUES ('Test Customer', 'test@example.com', '123 Test St', '$2a$10$8jf7TJa4g4G3DW8hD1.9Gu.xl19lSWRwZkYR.ERZXnk7cRlR9GIBi')
ON DUPLICATE KEY UPDATE id=id;

-- Insert test product
INSERT INTO products (name, description, price) 
VALUES ('Test Product', 'A test product', 99.99)
ON DUPLICATE KEY UPDATE id=id;

-- Insert inventory for the product
INSERT INTO inventories (product_id, stock_quantity) 
SELECT p.id, 10 FROM products p WHERE p.name = 'Test Product'
ON DUPLICATE KEY UPDATE stock_quantity = 10; 