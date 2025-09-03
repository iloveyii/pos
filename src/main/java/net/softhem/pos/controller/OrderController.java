package net.softhem.pos.controller;

import net.softhem.pos.dto.*;
import net.softhem.pos.model.Helpers;
import net.softhem.pos.model.Order;
import net.softhem.pos.model.OrderProduct;
import net.softhem.pos.model.Product;
import net.softhem.pos.repository.OrderProductRepository;
import net.softhem.pos.repository.OrderRepository;
import net.softhem.pos.repository.ProductRepository;
import net.softhem.pos.service.OrderProductService;
import net.softhem.pos.service.OrderService;
import net.softhem.pos.service.OrderUpdateService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderProductService orderProductService;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final OrderUpdateService orderUpdateService;

    public OrderController(OrderService orderService,
                           OrderProductService orderProductService,
                           ProductRepository productRepository,
                           OrderRepository orderRepository,
                           OrderProductRepository orderProductRepository,
                           OrderUpdateService orderUpdateService) {
        this.orderService = orderService;
        this.orderProductService = orderProductService;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderProductRepository = orderProductRepository;
        this.orderUpdateService = orderUpdateService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<OrderDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{page}/{size}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Page<OrderDTO>> getAllOrders(@PathVariable int page,
                                                         @PathVariable int size) {
        return ResponseEntity.ok(orderService.getAllOrders(page,size));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        OrderDTO order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody CreateOrderRequest request) throws InterruptedException {
        OrderDTO order = orderService.createOrder(request);
        order.setCommand("list");
        orderUpdateService.sendOrderUpdate(order);
        Thread.sleep(500);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    // Change order's status and/or type
    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<OrderDTO> changeStatusAndType(
            @PathVariable Long orderId,
            @RequestBody OrderStatusAndTypeRequest request) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        if(request.getStatus() != null && !request.getStatus().isEmpty())
            order.setStatus(request.getStatus());
        if(request.getType() != null && !request.getType().isEmpty())
            order.setType(Helpers.addOrRemoveFromCsvString(order.getType(), request.getType()));
        if(request.getPaymentMethod() != null && !request.getPaymentMethod().isEmpty())
            order.setPaymentMethod(Helpers.addOrRemoveFromCsvString(order.getPaymentMethod(), request.getPaymentMethod()));

        orderRepository.save(order);
        orderUpdateService.sendOrderUpdate(Helpers.orderToDto(order));
        return ResponseEntity.ok(Helpers.orderToDto(order));
    }

    // Add product to order
    @PostMapping("/{orderId}/items")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
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
            // Update inStock
            existingItem.get().getProduct().setInStock(
                    existingItem.get().getProduct().getInStock() +
                    Helpers.getUpdatedInStock(
                        existingItem.get().getQuantity(),
                        request.getQuantity()
                    )
            );
            // Update quantity
            existingItem.get().setQuantity(request.getQuantity());
            orderProductRepository.save(existingItem.get());
        } else {
            // Add new item
            OrderProduct item = new OrderProduct();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(request.getQuantity());
            item.getProduct().setInStock(item.getProduct().getInStock() - 1);
            item.setPriceAtPurchase(product.getPrice());
            orderProductRepository.save(item);
        }
        // Get order with new item added/saved
        order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        OrderDTO orderDto = Helpers.orderToDto(order);
        orderUpdateService.sendOrderUpdate(orderDto);
        return ResponseEntity.ok(orderDto);
    }

    // Remove item from order
    @DeleteMapping("/{orderId}/items/{itemId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<OrderDTO> removeItem(
            @PathVariable Long orderId,
            @PathVariable Long itemId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        OrderProduct item = orderProductRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));

        if (!item.getOrder().getId().equals(orderId)) {
            // New Order and dont need to remove item
            // throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item doesn't belong to this order");
        }

        // find product in Order's orderProducts list by product id
        for(OrderProduct orderProduct: order.getOrderProducts()){
            if(Objects.equals(orderProduct.getProduct().getId(), itemId)) {
                // Return inStock
                orderProduct.getProduct().setInStock(orderProduct.getProduct().getInStock() + orderProduct.getQuantity());
                productRepository.save(orderProduct.getProduct());
                order.getOrderProducts().remove(orderProduct);
                orderProductRepository.delete(orderProduct);
                // Order savedOrder = orderRepository.save(order);
                return ResponseEntity.ok(Helpers.orderToDto(order));
            }
        }

        OrderDTO orderDto = Helpers.orderToDto(order);
        orderUpdateService.sendOrderUpdate(orderDto);
        return ResponseEntity.ok(orderDto);
    }

    // Get order details
    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Order> getOrder(@PathVariable Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable Long id, @RequestBody UpdateOrderRequest request) {
        List<OrderProduct> orderProducts = orderProductService.getByOrderId(id);
        orderProductService.restoreQuantities(orderProducts);
        orderProductService.deleteByOrderId(id);
        OrderDTO order = orderService.updateOrder(id, request);
        orderUpdateService.sendOrderUpdate(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        OrderDTO order = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(order);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}