package net.softhem.pos.repository;

import net.softhem.pos.model.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
    List<OrderProduct> findByOrderId(Long orderId);
    void deleteByOrderId(Long orderId);
}