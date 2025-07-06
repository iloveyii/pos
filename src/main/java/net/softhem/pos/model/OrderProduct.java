package net.softhem.pos.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "ORDERS_PRODUCTS")
@Getter
@Setter
public class OrderProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "price_at_purchase", nullable = false)
    private Float priceAtPurchase;

    @Override
    public boolean equals(Object o) {
        if(this == o) return  true;
        if(o == null) return false;
        if(this.getClass() != o.getClass() ) return  false;

        OrderProduct orderProduct = (OrderProduct) o;
        return Objects.equals(order.getId(), orderProduct.getOrder().getId())
                && Objects.equals(product.getId(), orderProduct.getProduct().getId())
                && Objects.equals(quantity, orderProduct.getQuantity())
                && Objects.equals(priceAtPurchase, orderProduct.getPriceAtPurchase());
    }
    @Override
    public int hashCode() {
        return Objects.hash(order.getId(), product.getId(), quantity, priceAtPurchase);
    }
}