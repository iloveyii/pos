package net.softhem.pos.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "PRODUCTS")
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean status;

    @Column(nullable = false)
    private Float price;

    @Column(nullable = true)
    private String description;

    @Column(nullable = true)
    private String image;

    @Column(name = "in_stock", nullable = false)
    private Integer inStock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
}