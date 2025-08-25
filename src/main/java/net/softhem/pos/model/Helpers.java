package net.softhem.pos.model;

import net.softhem.pos.dto.OrderDTO;
import net.softhem.pos.dto.OrderProductDTO;
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

    public static Page<OrderDTO> pageOrderDTO(Page<Order> orderPage) {
        List<OrderDTO> dtoList = orderPage.getContent()
                .stream()
                .map(Helpers::orderToDto)
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, orderPage.getPageable(), orderPage.getTotalElements());
    }

    public static OrderDTO orderToDto(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus());
        dto.setType(order.getType());
        dto.setDiscount(order.getDiscount());
        dto.setSubTotal(order.getSubTotal());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setNotes(order.getNotes());

        List<OrderProductDTO> orderProductDTOs = order.getOrderProducts().stream()
                .map(op -> {
                    OrderProductDTO opDto = new OrderProductDTO();
                    opDto.setId(op.getId());
                    opDto.setOrderId(op.getOrder().getId());
                    opDto.setProductId(op.getProduct().getId());
                    opDto.setProductName(op.getProduct().getName());
                    opDto.setQuantity(op.getQuantity());
                    opDto.setPriceAtPurchase(op.getPriceAtPurchase());
                    return opDto;
                })
                .toList();

        dto.setOrderProducts(orderProductDTOs);
        return dto;
    }
}
