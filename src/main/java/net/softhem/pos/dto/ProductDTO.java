package net.softhem.pos.dto;


import lombok.Data;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private String image;
    private Float price;
    private Integer inStock;
}