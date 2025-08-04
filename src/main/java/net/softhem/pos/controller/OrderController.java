package net.softhem.pos.controller;

import net.softhem.pos.dto.CreateOrderRequest;
import net.softhem.pos.dto.OrderDTO;
import net.softhem.pos.dto.OrderItemRequest;
import net.softhem.pos.dto.UpdateOrderRequest;
import net.softhem.pos.model.Order;
import net.softhem.pos.model.OrderProduct;
import net.softhem.pos.model.Product;
import net.softhem.pos.repository.OrderProductRepository;
import net.softhem.pos.repository.OrderRepository;
import net.softhem.pos.repository.ProductRepository;
import net.softhem.pos.service.OrderProductService;
import net.softhem.pos.service.OrderService;
import net.softhem.pos.service.OrderUpdateService;
import net.softhem.pos.service.ProductService;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderProductService orderProductService;
    private final ProductService productService;
    private final OrderUpdateService orderUpdateService;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);


    public OrderController(OrderService orderService, OrderProductService orderProductService, ProductService productService, OrderUpdateService orderUpdateService, ProductRepository productRepository, OrderRepository orderRepository, OrderProductRepository orderProductRepository) {
        this.orderService = orderService;
        this.orderProductService = orderProductService;
        this.productService = productService;
        this.orderUpdateService = orderUpdateService;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderProductRepository = orderProductRepository;
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<OrderDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        OrderDTO order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody CreateOrderRequest request) {
        OrderDTO order = orderService.createOrder(request);
        order.setCommand("list");
        orderUpdateService.sendOrderUpdate(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
    // Add product to order
    @PostMapping("/{orderId}/items")
    public ResponseEntity<OrderDTO> addItem(
            @PathVariable Long orderId,
            @RequestBody OrderItemRequest request) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        // Check if product already exists in order
        Optional<OrderProduct> existingItem = order.getOrderProducts().stream()
                .filter(item -> item.getProduct().getId().equals(request.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            // Update quantity
            existingItem.get().setQuantity(existingItem.get().getQuantity() + request.getQuantity());
        } else {
            // Add new item
            OrderProduct item = new OrderProduct();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(request.getQuantity());
            order.getOrderProducts().add(item);
        }
        Order savedOrder = orderRepository.save(order);
        return ResponseEntity.ok(orderService.convertToDTO(savedOrder));
    }

    // Remove item from order
    @DeleteMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<OrderDTO> removeItem(
            @PathVariable Long orderId,
            @PathVariable Long itemId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        OrderProduct item = orderProductRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));

        if (!item.getOrder().getId().equals(orderId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item doesn't belong to this order");
        }

        order.getOrderProducts().remove(item);
        orderProductRepository.delete(item);

        Order savedOrder = orderRepository.save(order);
        return ResponseEntity.ok(orderService.convertToDTO(savedOrder));
    }

    // Get order details
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        return ResponseEntity.ok(order);
    }







    @PutMapping("/{id}")
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable Long id, @RequestBody UpdateOrderRequest request) {
        List<OrderProduct> orderProducts = orderProductService.getByOrderId(id);
        logger.info("###################: " + orderProducts.size());
        orderProductService.restoreQuantities(orderProducts);
        logger.info("################### restored: " + orderProducts.size());

        orderProductService.deleteByOrderId(id);
        orderProducts = orderProductService.getByOrderId(id);
        logger.info("################### after del: " + orderProducts.size());


        logger.info("################### items new: " + request.getItems().size());
        OrderDTO order = orderService.updateOrder(id, request);
        orderUpdateService.sendOrderUpdate(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        OrderDTO order = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(order);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}