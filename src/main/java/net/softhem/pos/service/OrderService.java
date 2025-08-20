package net.softhem.pos.service;

import net.softhem.pos.dto.*;
import net.softhem.pos.exception.InsufficientStockException;
import net.softhem.pos.exception.ResourceNotFoundException;
import net.softhem.pos.model.*;
import net.softhem.pos.repository.OrderRepository;
import net.softhem.pos.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderProductService orderProductService;
    List<OrderProduct> orderProducts = new ArrayList<>();

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
        Pageable pageable = PageRequest.of(0, 30);
        Page<Order> orders = orderRepository.findAll(pageable);
        return orders.getContent().stream().map(Helpers::orderToDto).toList();
    }

    @Transactional(readOnly = true)
    public Page<OrderDTO> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orderPage = orderRepository.findAll(pageable);
        return Helpers.pageOrderDTO(orderPage);
    }

    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return Helpers.orderToDto(order);
    }

    @Transactional
    public OrderDTO createOrder(CreateOrderRequest request) {
        Order order = new Order();
        Order savedOrder = orderRepository.save(order);
        return Helpers.orderToDto(savedOrder);
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
        return Helpers.orderToDto(updatedOrder);
    }

    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        orderRepository.delete(order);
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
        oldOrder.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(oldOrder);
        return Helpers.orderToDto(savedOrder);
    }
}