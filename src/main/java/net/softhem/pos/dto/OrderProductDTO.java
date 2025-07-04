package net.softhem.pos.dto;


import lombok.Data;

@Data
public class OrderProductDTO {
    private Long id;
    private Long orderId;
    private Long productId;
    private String productName;
    private Integer quantity;
    private Float priceAtPurchase;
}