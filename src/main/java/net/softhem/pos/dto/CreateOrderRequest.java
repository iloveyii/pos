package net.softhem.pos.dto;


import lombok.Data;
import java.util.List;

@Data
public class CreateOrderRequest {
    private List<OrderItemRequest> items;
}

