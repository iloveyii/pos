package net.softhem.pos.controller;

import net.softhem.pos.dto.ProductDTO;
import net.softhem.pos.model.Helpers;
import net.softhem.pos.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class PosViewController {
    private final ProductService productService;

    public PosViewController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/pos")
    public String home(Model model) {
        model.addAttribute("message", "Hello Thymeleaf!");
        return "index";
    }

    @GetMapping("/products")
    public String showProducts(Model model,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size) {
        Page<ProductDTO> productDTOPage = productService.getAllProducts(page,size);
        List<ProductDTO> products = Helpers.listProductDto(productDTOPage);
        model.addAttribute("products", products);
        return "products";
    }
}
