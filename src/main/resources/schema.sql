drop table if exists orders_products;
drop table if exists products;
drop table if exists orders;

CREATE TABLE IF NOT EXISTS PRODUCTS (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    description VARCHAR(512),
    image VARCHAR(64),
    price FLOAT,
    in_stock INTEGER
);

CREATE TABLE IF NOT EXISTS ORDERS (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'PENDING',
    sub_total FLOAT DEFAULT 0.0,
    discount FLOAT DEFAULT 0.0,
    total_amount FLOAT DEFAULT 0.0,
    payment_method VARCHAR(50) DEFAULT 'CARD',
    notes VARCHAR(200) DEFAULT 'http://localhost:8080/pdf/invoice-sample2.pdf'
);

CREATE TABLE IF NOT EXISTS ORDERS_PRODUCTS (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    price_at_purchase FLOAT DEFAULT 0.0,
    -- PRIMARY KEY (order_id, product_id),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT
);

-- Insert sample products
INSERT INTO PRODUCTS (name, description, image, price, in_stock) VALUES
('Wireless Headphones', 'Noise cancelling Bluetooth', 'wireless-headphones.avif', 99.00, 100),
('Smart Watch', 'Fitness tracker & notifications', 'smart-watch.avif', 149.99, 50),
('Bluetooth Speaker', 'Portable waterproof speaker', 'bluetooth-phones.avif', 59.99, 200),
('USB-C Cable', 'Fast charging 3ft cable', 'ear-buds.avif', 12.99, 75),

('Wireless Mouse', 'Ergonomic design', 'wireless-mouse.avif', 24.99, 60),
('Laptop Backpack', 'Water resistant with USB port', 'laptop-bag.avif', 39.99, 30),
('Power Bank', '10000mAh dual USB', 'power-bank.avif', 29.99, 50),
('Screen Protector', 'Tempered glass for smartphones', 'screen-protector.avif', 8.99, 100);

-- Insert sample orders
INSERT INTO ORDERS (status, discount, sub_total, total_amount, notes) VALUES
('COMPLETED', 0.0, 1248.0, 1248.00, 'http://localhost:8080/pdf/JM004426.pdf'),
('SHIPPED', 10.0, 2998.00, 2698.2, 'http://localhost:8080/pdf/invoice-sample2.pdf'),
('PENDING', 20.0, 328.00, 319.0, 'http://localhost:8080/pdf/invoice-sample.pdf'),
('COMPLETED', 50.0, 678.00, 339.0, 'http://localhost:8080/pdf/invoice-sample2.pdf'),
('PENDING', 30.0, 628.00, 539.0, 'http://localhost:8080/pdf/invoice-sample2.pdf'),
('PROCESSING', 30.0, 628.00, 539.0, 'http://localhost:8080/pdf/invoice-sample2.pdf'),
('CANCELLED', 10.0, 599.00, 539.1, 'http://localhost:8080/pdf/invoice-sample2.pdf'),
('COMPLETED', 10.0, 599.00, 539.1, 'http://localhost:8080/pdf/invoice-sample2.pdf');

-- Insert order items (products in each order)
INSERT INTO ORDERS_PRODUCTS (order_id, product_id, quantity, price_at_purchase) VALUES
-- Order 1: John's order (iPhone + AirPods)
(1, 1, 1, 999.00),
(1, 3, 1, 249.00),

-- Order 2: Emily's order (MacBook Pro + Apple Watch)
(2, 2, 1, 1999.00),
(2, 4, 1, 429.00),
(2, 3, 2, 249.00),  -- Two AirPods

-- Order 3: Michael's order (AirPods)
(3, 3, 1, 249.00),
(3, 5, 1, 429.00),  -- Watch at different price

-- Order 4: Sarah's order (iPad)
(4, 5, 1, 599.00),
(4, 6, 2, 599.00),

-- Order 5: Sarah's order (iPad)
(5, 3, 1, 599.00),
(5, 4, 2, 599.00),

-- Order 6: Sarah's order (iPad)
(6, 1, 1, 599.00),
(6, 6, 2, 599.00),

-- Order 7:
(7, 1, 1, 599.00),
(7, 6, 2, 599.00),

-- Order 8:
(8, 1, 1, 599.00),
(8, 6, 2, 599.00);


