package net.softhem.pos.controller;


import net.softhem.pos.dto.OrderProductDTO;
import net.softhem.pos.dto.ProductDTO;
import net.softhem.pos.model.OrderProduct;
import net.softhem.pos.service.OrderProductService;
import net.softhem.pos.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-products")
public class OrderProductController {
    private final OrderProductService orderProductService;

    public OrderProductController(OrderProductService orderProductService) {
        this.orderProductService = orderProductService;
    }

    @GetMapping
    public List<OrderProductDTO> getAll() {
        return orderProductService.getAll();
    }

    @GetMapping("/{orderId}")
    public List<OrderProduct> getByOrderId(@PathVariable Long orderId) {
        return orderProductService.getByOrderId(orderId);
    }

    @DeleteMapping("/order/{orderId}")
    public void deleteByOrderId(@PathVariable Long orderId) {
        orderProductService.deleteByOrderId(orderId);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        orderProductService.deleteById(id);
    }


}