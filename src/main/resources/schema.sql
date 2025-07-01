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
    total_amount FLOAT NOT NULL
);

CREATE TABLE IF NOT EXISTS ORDERS_PRODUCTS (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    price_at_purchase FLOAT NOT NULL,
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
INSERT INTO ORDERS (status, total_amount) VALUES
('COMPLETED', 1248.00),
('SHIPPED', 2998.00),
('PENDING', 678.00),
('PROCESSING', 599.00);

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
(4, 5, 1, 599.00);

