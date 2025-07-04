package net.softhem.pos.service;

import net.softhem.pos.dto.OrderProductDTO;
import net.softhem.pos.dto.ProductDTO;
import net.softhem.pos.exception.ResourceNotFoundException;
import net.softhem.pos.model.Order;
import net.softhem.pos.model.OrderProduct;
import net.softhem.pos.model.Product;
import net.softhem.pos.repository.OrderProductRepository;
import net.softhem.pos.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderProductService {
    private final OrderProductRepository orderProductRepository;

    public OrderProductService(OrderProductRepository orderProductRepository) {
        this.orderProductRepository = orderProductRepository;
    }

    public List<OrderProductDTO> getAll() {
        return orderProductRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<OrderProduct> getByOrderId(Long orderId) {
        return orderProductRepository.findByOrderId(orderId);
    }

    public OrderProduct createOrderProduct(OrderProduct orderProduct, Optional<Order> order, Optional<Product> product) {
        orderProduct.setOrder(order.get());
        orderProduct.setProduct(product.get());
        orderProduct.setPriceAtPurchase(product.get().getPrice());
        return orderProductRepository.save(orderProduct);
    }

    @Transactional
    public void deleteByOrderId(Long orderId) {
        orderProductRepository.deleteByOrderId(orderId);
    }

    public void deleteById(Long id) {
        orderProductRepository.deleteById(id);
    }

    private OrderProductDTO convertToDTO(OrderProduct orderProduct) {
        OrderProductDTO dto = new OrderProductDTO();
        dto.setId(orderProduct.getId());
        dto.setOrderId(orderProduct.getOrder().getId());
        dto.setProductId(orderProduct.getProduct().getId());
        dto.setProductName(orderProduct.getProduct().getName());
        dto.setQuantity(orderProduct.getQuantity());
        dto.setPriceAtPurchase(orderProduct.getPriceAtPurchase());
        return dto;
    }
}