package net.softhem.pos.controller;

import net.softhem.pos.dto.CommandDTO;
import net.softhem.pos.dto.OrderDTO;
import net.softhem.pos.model.ReceiptFormat;
import net.softhem.pos.service.OrderService;
import net.softhem.pos.service.OrderUpdateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class WebSocketController {
    private final OrderUpdateService orderUpdateService;
    private final OrderService orderService;

    public WebSocketController(OrderUpdateService orderUpdateService, OrderService orderService) {
        this.orderUpdateService = orderUpdateService;
        this.orderService = orderService;
    }

    @MessageMapping("/order") // Receives messages sent to /app/order
    @SendTo("/topic/orders") // Sends return value to /topic/orders
    public OrderDTO addOrder(OrderDTO orderDTO) {
        // Process the book (save to database, etc.)
        return orderDTO;
    }

    @GetMapping("/pay")
    public String home(Model model) {
        return "pay";
    }

    @PostMapping("/command")
    public ResponseEntity<OrderDTO> commandEndPoint(@RequestBody CommandDTO commandDTO) {
        OrderDTO orderDTO = orderService.getOrderById(commandDTO.getId());
        orderDTO.setCommand(commandDTO.getCommand());
        genPdf();
        orderUpdateService.sendOrderUpdate(orderDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderDTO);
    }

    // Utility method to test the class
    public static void genPdf() {
        String jsonOrder = "{\n" +
                "    \"id\": 10,\n" +
                "    \"orderDate\": \"2025-09-06T19:31:27.491238\",\n" +
                "    \"orderDateString\": \"2025-09-06 19:31\",\n" +
                "    \"status\": \"PENDING\",\n" +
                "    \"type\": \"INVOICE\",\n" +
                "    \"subTotal\": 308.98,\n" +
                "    \"discount\": 0,\n" +
                "    \"totalAmount\": 333.69843,\n" +
                "    \"paymentMethod\": null,\n" +
                "    \"notes\": \"https://k.jojomobil.se/pdf_files/JM004426/JM004426.pdf\",\n" +
                "    \"orderProducts\": [\n" +
                "        {\n" +
                "            \"id\": 22,\n" +
                "            \"orderId\": 10,\n" +
                "            \"productId\": 3,\n" +
                "            \"productName\": \"Bluetooth Speaker\",\n" +
                "            \"quantity\": 1,\n" +
                "            \"priceAtPurchase\": 59.99\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 23,\n" +
                "            \"orderId\": 10,\n" +
                "            \"productId\": 2,\n" +
                "            \"productName\": \"Smart Watch\",\n" +
                "            \"quantity\": 1,\n" +
                "            \"priceAtPurchase\": 149.99\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 24,\n" +
                "            \"orderId\": 10,\n" +
                "            \"productId\": 1,\n" +
                "            \"productName\": \"Wireless Headphones\",\n" +
                "            \"quantity\": 1,\n" +
                "            \"priceAtPurchase\": 99\n" +
                "        }\n" +
                "    ],\n" +
                "    \"command\": null\n" +
                "}";

        try {
            String latexReceipt = ReceiptFormat.generateLatexReceipt(jsonOrder);
            System.out.println("=== RECEIPT LATEX ===\n");
            System.out.println(latexReceipt);

            System.out.println("\n=== PAYMENT QR LATEX ===\n");
            String latexPayment = ReceiptFormat.generatePaymentQRLatex(jsonOrder);
            System.out.println(latexPayment);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
