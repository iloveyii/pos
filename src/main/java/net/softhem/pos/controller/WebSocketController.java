package net.softhem.pos.controller;

import net.softhem.pos.dto.CommandDTO;
import net.softhem.pos.dto.OrderDTO;
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
        orderUpdateService.sendOrderUpdate(orderDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderDTO);
    }
}
