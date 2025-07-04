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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        //OrderDTO order = orderService.updateOrder(id, request);
        orderProductService.deleteByOrderId(id);
        Optional<Order> order = orderRepository.findById(id);

        OrderProduct orderProduct1 = new OrderProduct();
        orderProduct1.setQuantity(5);
        Optional<Product> product1 = productRepository.findById(1L);
        orderProductService.createOrderProduct(orderProduct1, order, product1);

        OrderProduct orderProduct2 = new OrderProduct();
        orderProduct2.setQuantity(10);
        Optional<Product> product2 = productRepository.findById(2L);
        orderProductService.createOrderProduct(orderProduct2,order, product2);

        return ResponseEntity.status(HttpStatus.CREATED).body(null);
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