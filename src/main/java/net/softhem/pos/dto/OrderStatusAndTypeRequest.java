package net.softhem.pos.dto;

import lombok.Data;

@Data
public class OrderStatusAndTypeRequest {
    private String status;
    private String type;
    private String paymentMethod;
}