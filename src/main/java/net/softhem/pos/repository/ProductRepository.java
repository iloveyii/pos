package net.softhem.pos.repository;


import net.softhem.pos.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Basic pagination - get all products with pagination
    Page<Product> findAll(Pageable pageable);
}