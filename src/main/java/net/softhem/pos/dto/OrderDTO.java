package net.softhem.pos.dto;


import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private LocalDateTime orderDate;
    private String status;
    private Float subTotal;
    private Float discount;
    private Float totalAmount;
    private String paymentMethod;
    private String notes;
    private List<OrderProductDTO> orderProducts;
    private String command;
}