package net.softhem.pos.dto;


import lombok.Data;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private LocalDateTime orderDate;
    private String orderDateString;
    private String status;
    private String type;
    private Float subTotal;
    private Float discount;
    private Float totalAmount;
    private String paymentMethod;
    private String notes;
    private List<OrderProductDTO> orderProducts;
    private String command;

    public String getOrderDateString() {
        // Desired format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        // Format the date and time
        return orderDate.format(formatter);
    }

}