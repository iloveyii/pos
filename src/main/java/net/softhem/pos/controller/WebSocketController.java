package net.softhem.pos.controller;

import net.softhem.pos.dto.CommandDTO;
import net.softhem.pos.dto.OrderDTO;
import net.softhem.pos.service.FileStorageService;
import net.softhem.pos.service.OrderService;
import net.softhem.pos.service.OrderUpdateService;
import net.softhem.pos.service.PdfService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Controller
public class WebSocketController {
    private final OrderUpdateService orderUpdateService;
    private final OrderService orderService;
    private final PdfService pdfService;

    public WebSocketController(OrderUpdateService orderUpdateService, OrderService orderService, FileStorageService fileStorageService, PdfService pdfService) {
        this.orderUpdateService = orderUpdateService;
        this.orderService = orderService;
        this.pdfService = pdfService;
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
    public ResponseEntity<OrderDTO> commandEndPoint(@RequestBody CommandDTO commandDTO) throws Exception {
        OrderDTO orderDTO = orderService.getOrderById(commandDTO.getId());
        orderDTO.setCommand(commandDTO.getCommand());
        System.out.print("Command::" + commandDTO.getCommand());

        if(Objects.equals(commandDTO.getCommand(), "gen-receipt")) {
            System.out.print("Command is gen::" + commandDTO.getCommand());
            String url = pdfService.generatePdfReceipt(orderDTO);
            orderDTO.setUrl(url);
        }

        orderUpdateService.sendOrderUpdate(orderDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderDTO);
    }
}
