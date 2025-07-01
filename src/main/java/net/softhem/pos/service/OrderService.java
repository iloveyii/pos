package net.softhem.pos.service;


import net.softhem.pos.dto.*;
import net.softhem.pos.exception.InsufficientStockException;
import net.softhem.pos.exception.ResourceNotFoundException;
import net.softhem.pos.model.*;
import net.softhem.pos.repository.OrderRepository;
import net.softhem.pos.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        ProductService productService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.productService = productService;
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(this::convertToDTO).toList();
    }

    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return convertToDTO(order);
    }

    @Transactional
    public OrderDTO createOrder(CreateOrderRequest request) {
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");
        order.setTotalAmount(0.0f);

        List<OrderProduct> orderProducts = new ArrayList<>();
        float totalAmount = 0.0f;

        for (OrderItemRequest item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + item.getProductId()));

            if (product.getInStock() < item.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
            }

            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setOrder(order);
            orderProduct.setProduct(product);
            orderProduct.setQuantity(item.getQuantity());
            orderProduct.setPriceAtPurchase(product.getPrice());
            orderProducts.add(orderProduct);

            totalAmount += product.getPrice() * item.getQuantity();

            // Update product stock
            product.setInStock(product.getInStock() - item.getQuantity());
            productRepository.save(product);
        }

        order.setOrderProducts(orderProducts);
        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);
        return convertToDTO(savedOrder);
    }

    @Transactional
    public OrderDTO updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        return convertToDTO(updatedOrder);
    }

    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        orderRepository.delete(order);
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());

        List<OrderProductDTO> orderProductDTOs = order.getOrderProducts().stream()
                .map(op -> {
                    OrderProductDTO opDto = new OrderProductDTO();
                    opDto.setId(op.getId());
                    opDto.setProductId(op.getProduct().getId());
                    opDto.setProductName(op.getProduct().getName());
                    opDto.setQuantity(op.getQuantity());
                    opDto.setPriceAtPurchase(op.getPriceAtPurchase());
                    return opDto;
                })
                .toList();

        dto.setOrderProducts(orderProductDTOs);
        return dto;
    }
}