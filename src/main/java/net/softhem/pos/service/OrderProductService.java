package net.softhem.pos.service;

import net.softhem.pos.dto.OrderItemRequest;
import net.softhem.pos.dto.OrderProductDTO;
import net.softhem.pos.model.Order;
import net.softhem.pos.model.OrderProduct;
import net.softhem.pos.model.Product;
import net.softhem.pos.repository.OrderProductRepository;
import net.softhem.pos.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderProductService {
    private final OrderProductRepository orderProductRepository;
    private final ProductRepository productRepository;

    public OrderProductService(OrderProductRepository orderProductRepository, ProductRepository productRepository) {
        this.orderProductRepository = orderProductRepository;
        this.productRepository = productRepository;
    }

    public List<OrderProductDTO> getAll() {
        return orderProductRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<OrderProduct> getByOrderId(Long orderId) {
        return orderProductRepository.findByOrderId(orderId);
    }

    public OrderProduct createOrderProduct(OrderProduct orderProduct, Order order, Product product) {
        orderProduct.setOrder(order);
        orderProduct.setProduct(product);
        orderProduct.setPriceAtPurchase(product.getPrice());
        return orderProductRepository.save(orderProduct);
    }

    @Transactional
    public void deleteByOrderId(Long orderId) {
        orderProductRepository.deleteByOrderId(orderId);
    }

    public void deleteById(Long id) {
        orderProductRepository.deleteById(id);
    }

    @Transactional
    public void deleteOrderProductAndRestoreQuantities(Long orderId) {
        // 1. Find all products in the order
        List<OrderProduct> orderProducts = orderProductRepository.findByOrderId(orderId);
        System.out.print(String.format("Found %d products", orderProducts.size()));

        // 2. Restore quantities
        restoreProductQuantities(orderProducts);
    }

    private void restoreProductQuantities(List<OrderProduct> orderProducts) {
        // Group quantities by product for efficient updates
        Map<Product, Integer> quantityMap = orderProducts.stream()
                .collect(Collectors.groupingBy(
                        OrderProduct::getProduct,
                        Collectors.summingInt(OrderProduct::getQuantity)
                ));

        // Update each product
        quantityMap.forEach((product, quantity) -> {
            product.setInStock(product.getInStock() + quantity);
            productRepository.save(product);
        });
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

    public void restoreQuantities(List<OrderProduct> orderProducts) {
        restoreProductQuantities(orderProducts);
    }

    public void updateByItems(List<OrderProduct> orderProducts, List<OrderItemRequest> items) {
        for(OrderProduct orderProduct: orderProducts) {
            // if(orderProduct.getProduct().getId() == )
            boolean itemsHasProductId = items.stream()
                    .anyMatch(itm -> itm.getProductId().equals(orderProduct.getProduct().getId()));
            for(OrderItemRequest item: items){
                // 1. If order product exists in items
                if(Objects.equals(orderProduct.getProduct().getId(), item.getProductId())) {
                    // 2. Check if it has the same quantity
                    if(Objects.equals(orderProduct.getQuantity(), item.getQuantity())) {
                        System.out.println("Exact match and no further action required");
                    } else {
                        // 3. Else update quantity in order product
                        orderProduct.setQuantity(item.getQuantity());
                    }
                } else {
                    // 4. if product does not exist at all in items then it is not needed, delete it
                    orderProduct = null;

                }
            }
        }
    }
}