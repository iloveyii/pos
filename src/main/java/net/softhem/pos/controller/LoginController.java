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
public class LoginController {
    @GetMapping("/login")
    public String index(Model model) {
        model.addAttribute("message", "Hello Thymeleaf!");
        return "login";
    }
}
