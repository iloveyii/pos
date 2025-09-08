# POS

- Point of sale API

## Install

- Via spring site

## Run

- Compile and run `mvn spring-boot:run`
- Compile `mvn clean package`
- Run `cd target && java -jar pos-0.0.1-SNAPSHOT.jar`

## SQL

- Open browser: <http://localhost:8080/h2-console/>

```SQL
SELECT 
    op.total_order_quantity, 
    p.total_in_stock,
    op.total_order_quantity + p.total_in_stock AS combined_total
FROM 
    (SELECT SUM(quantity) AS total_order_quantity FROM ORDERS_PRODUCTS) op,
    (SELECT SUM(in_stock) AS total_in_stock FROM products) p;
```
## Debug

- <https://chat.deepseek.com/a/chat/s/0095ab7b-a28e-4263-b2d7-ff066d764506>

## Pagination

- <http://localhost:8080/products?page=0&size=3>

## Arbets 

- Rusta och matcha
- Jöbbsprängt
- Nyttstart jobb
- Internship
- Utbildining 12 månader

## Run docker cli 

- `docker exec -i springlatex xelatex -output-directory=/data/pdf/${order.service_number} -jobname=${order.service_number} /data/pdf_files/${order.service_number}/generated.tex`; 
- `docker exec -i springlatex xelatex -output-directory=/data/pdf/9 -jobname=9 /data/tex/9.tex`; 

## Generate PDF

- It generates for now by click QR button
- Browse using <https://pos.softhem.net/pdf/9>
- Served by PdfController



