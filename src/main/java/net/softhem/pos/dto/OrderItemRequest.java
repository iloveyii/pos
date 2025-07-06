package net.softhem.pos.dto;

import lombok.Data;
import net.softhem.pos.model.OrderProduct;

@Data
public class OrderItemRequest {
    private Long productId;
    private Integer quantity;
}