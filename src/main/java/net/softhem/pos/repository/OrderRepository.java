package net.softhem.pos.repository;

import net.softhem.pos.dto.OrderDTO;
import net.softhem.pos.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE " +
            "(:status IS NULL OR o.status LIKE %:status%) ")
    Page<Order> findByCriteria(
            String status,
            Pageable pageable);
}