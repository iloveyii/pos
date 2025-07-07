package net.softhem.pos.service;


import net.softhem.pos.dto.OrderDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
        import org.springframework.stereotype.Service;

@Service
public class OrderUpdateService {

    private final SimpMessagingTemplate messagingTemplate;

    public OrderUpdateService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendOrderUpdate(OrderDTO orderDTO) {
        // Send to all subscribers of "/topic/orders"
        messagingTemplate.convertAndSend("/topic/orders", orderDTO);

        // To send to a specific user:
        // messagingTemplate.convertAndSendToUser(username, "/queue/orders", orderDto);
    }
}