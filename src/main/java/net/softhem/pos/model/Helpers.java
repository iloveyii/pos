package net.softhem.pos.model;

import net.softhem.pos.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Helpers {

    public static ProductDTO productToDto(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setImage(String.format("/%s/%s", "images/products", product.getImage()));
        dto.setPrice(product.getPrice());
        dto.setInStock(product.getInStock());
        dto.setStatus(product.isStatus());
        dto.setUpdatedAt(product.getUpdatedAt());
        dto.setCategoryId(product.getCategory().getId());
        return dto;
    }

    // Convert Page<Product> to Page<ProductDTO>
    public static Page<ProductDTO> pageProductDTO(Page<Product> productPage) {
        List<ProductDTO> dtoList = productPage.getContent()
                .stream()
                .map(Helpers::productToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, productPage.getPageable(), productPage.getTotalElements());
    }

    public static List<ProductDTO> listProductDto(Page<ProductDTO> pageProductDto) {
        return new ArrayList<>(pageProductDto.getContent());
    }
}
