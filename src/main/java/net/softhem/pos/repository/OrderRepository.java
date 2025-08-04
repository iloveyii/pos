package net.softhem.pos.repository;

import net.softhem.pos.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Basic pagination for all orders
    Page<Order> findAll(Pageable pageable);
    // Filter by exact status
    Page<Order> findByStatus(String status, Pageable pageable);
}