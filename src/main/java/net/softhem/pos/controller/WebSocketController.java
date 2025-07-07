package net.softhem.pos.controller;

import net.softhem.pos.dto.OrderDTO;
import net.softhem.pos.dto.ProductDTO;
import net.softhem.pos.model.Product;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebSocketController {

    @MessageMapping("/order") // Receives messages sent to /app/order
    @SendTo("/topic/orders") // Sends return value to /topic/orders
    public OrderDTO addOrder(OrderDTO orderDTO) {
        // Process the book (save to database, etc.)
        return orderDTO;
    }

    @GetMapping("/websocket")
    public String home(Model model) {
        return "websocket";
    }
}
