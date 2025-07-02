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
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final OrderProductService orderProductService;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        ProductService productService,
                        OrderProductService orderProductService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.productService = productService;
        this.orderProductService = orderProductService;
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

    private Optional<OrderProduct> getOldOrderProductById(Order oldOrder, Long id) {
        Optional<OrderProduct> orderProductOpt = oldOrder.getOrderProducts().stream()
                .filter(p -> p.getId() == id)
                .findFirst();

        if (orderProductOpt.isPresent()) {
            Product product = orderProductOpt.get().getProduct();
            System.out.println("Found: " + product.getName());
            return orderProductOpt;
        } else {
            System.out.println("Product not found");
            return  null;
        }
    }

    @Transactional
    public OrderDTO updateOrder(Long id, UpdateOrderRequest request) {
        Order oldOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        List<OrderProduct> orderProducts = oldOrder.getOrderProducts();
        // Remove previous orderProducts
        orderProductService.deleteOrderProduct(oldOrder.getId());
        float totalAmount = 0.0f;

        for (OrderItemRequest item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + item.getProductId()));

            totalAmount += product.getPrice() * item.getQuantity();

            Optional<OrderProduct> olderOrderProduct = getOldOrderProductById(oldOrder, item.getProductId());
            if(olderOrderProduct.isPresent()) {
                // Case 1 old product in cart/request.getItems
                // return the old stock to product inStock
                product.setInStock(product.getInStock() + olderOrderProduct.get().getQuantity());
                // Update product stock
                product.setInStock(product.getInStock() - item.getQuantity());
                productRepository.save(product);
            } else {
                // Case 2 new product in cart/request.getItems
                if (product.getInStock() < item.getQuantity()) {
                    throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
                }
                // Update product stock
                product.setInStock(product.getInStock() - item.getQuantity());
                productRepository.save(product);
            }
            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setOrder(oldOrder);
            orderProduct.setProduct(product);
            orderProduct.setQuantity(item.getQuantity());
            orderProduct.setPriceAtPurchase(product.getPrice());
            orderProducts.add(orderProduct);
        }
        //oldOrder.setOrderProducts(orderProducts);
        oldOrder.setTotalAmount(totalAmount);
        // Order savedOrder = orderRepository.save(oldOrder);
        return convertToDTO(oldOrder);
    }
}