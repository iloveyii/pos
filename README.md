# POS

- Point of sale API

## Install

- Via spring site

## Run

- Compile and run `mvn spring-boot:run`
- Compile `mvn clean package`
- Run `cd target && java -jar pos-0.0.1-SNAPSHOT.jar`

## SQL

```SQL
SELECT 
    op.total_order_quantity, 
    p.total_in_stock,
    op.total_order_quantity + p.total_in_stock AS combined_total
FROM 
    (SELECT SUM(quantity) AS total_order_quantity FROM ORDERS_PRODUCTS) op,
    (SELECT SUM(in_stock) AS total_in_stock FROM products) p;
```
