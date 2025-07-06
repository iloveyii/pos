package net.softhem.pos.controller;

import net.softhem.pos.dto.CreateOrderRequest;
import net.softhem.pos.dto.OrderDTO;
import net.softhem.pos.dto.UpdateOrderRequest;
import net.softhem.pos.model.Order;
import net.softhem.pos.model.OrderProduct;
import net.softhem.pos.model.Product;
import net.softhem.pos.repository.OrderRepository;
import net.softhem.pos.repository.ProductRepository;
import net.softhem.pos.service.OrderProductService;
import net.softhem.pos.service.OrderService;
import net.softhem.pos.service.ProductService;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.LoggerFactory;


import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderProductService orderProductService;
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);


    public OrderController(OrderService orderService, OrderProductService orderProductService, ProductService productService, ProductRepository productRepository, OrderRepository orderRepository) {
        this.orderService = orderService;
        this.orderProductService = orderProductService;
        this.productService = productService;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
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
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
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