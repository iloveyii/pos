package net.softhem.pos.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ORDERS")
@Getter
@Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_date", nullable = true)
    private LocalDateTime orderDate = LocalDateTime.now();

    @Column(nullable = true)
    private String status = "PENDING";

    @Column(name = "sub_total", nullable = true)
    private Float subTotal;

    @Column(name = "discount", nullable = true)
    private Float discount;

    @Column(name = "total_amount", nullable = true)
    private Float totalAmount;

    @Column(name = "payment_method", nullable = true)
    private String paymentMethod;

    @Column(name = "notes", nullable = true)
    private String notes;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderProduct> orderProducts = new ArrayList<>();
}