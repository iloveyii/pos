package net.softhem.pos.controller;

import net.softhem.pos.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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

    @GetMapping("/kassa")
    public String showProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "index";
    }
}
