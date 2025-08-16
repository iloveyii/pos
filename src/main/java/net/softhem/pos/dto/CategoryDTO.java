package net.softhem.pos.dto;

import lombok.Data;
import java.util.List;

@Data
public class CategoryDTO {
    private Long id;
    private String name;
    private String description;
    private boolean status;
    // private List<ProductDTO> products;  // Optional: for full category details
}