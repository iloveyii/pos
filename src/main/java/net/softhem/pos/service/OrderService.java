package net.softhem.pos.service;


import lombok.RequiredArgsConstructor;
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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class OrderService {

    private OrderRepository orderRepository;
    private ProductRepository productRepository;
    private OrderProductService orderProductService;
    List<OrderProduct> orderProducts = new ArrayList<>();

    public OrderService() {
    }

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        ProductService productService,
                        OrderProductService orderProductService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.orderProductService = orderProductService;
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(this::convertToDTO).toList();
    }

    public PaginatedResponse<Order> searchOrders(String status, int page, int size) {
        // Create pageable with sorting
        Pageable pageable = PageRequest.of(page, size);

        // Convert criteria to repository query
        Page<Order> propertyPage = orderRepository.findByCriteria(
                status,
                pageable
        );

        return PaginatedResponse.<Order>builder()
                .content(propertyPage.getContent())
                .currentPage(propertyPage.getNumber())
                .totalPages(propertyPage.getTotalPages())
                .totalItems(propertyPage.getTotalElements())
                .itemsPerPage(propertyPage.getSize())
                .build();
    }

    private Sort createSort(String sortBy, String sortDirection) {
        if (sortBy == null || sortBy.isEmpty()) {
            return Sort.unsorted();
        }

        Sort.Direction direction = Sort.Direction.ASC;
        if (sortDirection != null && sortDirection.equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }

        switch (sortBy.toLowerCase()) {
            case "status":
                return Sort.by(direction, "status");
            case "order_date":
                return Sort.by(direction, "order_date");
            case "total_amount":
                return Sort.by(direction, "total_amount");
            default:
                return Sort.by(direction, "order_date");
        }
    }


    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return convertToDTO(order);
    }


    // create order
    // add/update product to order
    // remove order from order
    //

    @Transactional
    public OrderDTO createOrder(CreateOrderRequest request) {
        Order order = new Order();
        // order.setOrderDate(LocalDateTime.now());
        // order.setStatus("PENDING");
        // order.setTotalAmount(0.0f);

//        List<OrderProduct> orderProducts = new ArrayList<>();
//        float totalAmount = 0.0f;
//
//        for (OrderItemRequest item : request.getItems()) {
//            Product product = productRepository.findById(item.getProductId())
//                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + item.getProductId()));
//
//            if (product.getInStock() < item.getQuantity()) {
//                throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
//            }
//
//            OrderProduct orderProduct = new OrderProduct();
//            orderProduct.setOrder(order);
//            orderProduct.setProduct(product);
//            orderProduct.setQuantity(item.getQuantity());
//            orderProduct.setPriceAtPurchase(product.getPrice());
//            orderProducts.add(orderProduct);
//
//            totalAmount += product.getPrice() * item.getQuantity();
//
//            // Update product stock
//            product.setInStock(product.getInStock() - item.getQuantity());
//            productRepository.save(product);
//        }

        // order.setOrderProducts(orderProducts);
        // order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);
        return convertToDTO(savedOrder);
    }

    private void addOrderProduct(Order order, Product product, Integer quantity, boolean save) {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setOrder(order);
        orderProduct.setProduct(product);
        orderProduct.setQuantity(quantity);
        orderProduct.setPriceAtPurchase(product.getPrice());
        orderProducts.add(orderProduct);
        if(save)
            orderProductService.createOrderProduct(orderProduct, order, product);
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

    public OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus());
        dto.setDiscount(order.getDiscount());
        dto.setSubTotal(order.getSubTotal());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setNotes(order.getNotes());

        List<OrderProductDTO> orderProductDTOs = order.getOrderProducts().stream()
                .map(op -> {
                    OrderProductDTO opDto = new OrderProductDTO();
                    opDto.setId(op.getId());
                    opDto.setOrderId(op.getOrder().getId());
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
        // Remove previous orderProducts
        // orderProductService.deleteByOrderId(id); // @todo release stock

        float totalAmount = 0.0f;

        for (OrderItemRequest item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + item.getProductId()));

            if (product.getInStock() < item.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
            }

            addOrderProduct(oldOrder, product, item.getQuantity(), true);
            totalAmount += product.getPrice() * item.getQuantity();

            // Update product stock
            product.setInStock(product.getInStock() - item.getQuantity());
            productRepository.save(product);
        }
        oldOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        //oldOrder.getOrderProducts().clear();
        // oldOrder.setOrderProducts(orderProducts);
        oldOrder.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(oldOrder);
        return convertToDTO(savedOrder);
    }
}