package net.softhem.pos.dto;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private boolean status;
    private String description;
    private String image;
    private Float price;
    private Integer inStock;
    private LocalDateTime updatedAt;
    private Long categoryId;
}