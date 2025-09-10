package net.softhem.pos.model;

import net.softhem.pos.dto.OrderDTO;
import net.softhem.pos.dto.OrderProductDTO;
import net.softhem.pos.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.parameters.P;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Helpers {
    private static String devDataPath = "./src/main/resources/static";

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

    public static String addWordToCsvStream(String csvString, String newWord) {
        if (csvString == null || csvString.isEmpty()) {
            return newWord;
        }

        if (newWord == null || newWord.trim().isEmpty()) {
            return csvString;
        }

        String trimmedNewWord = newWord.trim();

        // Check if word exists
        boolean wordExists = Arrays.stream(csvString.split(","))
                .map(String::trim)
                .anyMatch(word -> word.equals(trimmedNewWord));

        if (wordExists) {
            return csvString;
        }

        // Add new word
        return Stream.concat(
                Arrays.stream(csvString.split(",")).map(String::trim),
                Stream.of(trimmedNewWord)
        ).collect(Collectors.joining(","));
    }

    public static String removeWordFromCsvString(String csvString, String wordToRemove) {
        String[] parts = (csvString + "").split("\\s*,\\s*"); // split by comma and optional spaces
        return Arrays.stream(parts)
                .filter(word -> !word.equals(wordToRemove)) // remove target word
                .collect(Collectors.joining(","));
    }

    public static String addWordToCsvString(String csvString, String wordToAdd) {
        return Helpers.removeWordFromCsvString(csvString, wordToAdd) + "," + wordToAdd;
    }

    public static String addOrRemoveFromCsvString(String csvString, String word) {
        if(word.startsWith("-")) {
            String wordWithoutDash = word.replace("-", "");
            return Helpers.removeWordFromCsvString(csvString, wordWithoutDash);
        } else {
            return Helpers.addWordToCsvString(csvString, word);
        }
    }

    public static int getUpdatedInStock(int existing, int requested) {
        return existing - requested;
    }

    public static String getDirectoryPath(String dirname) {
        String dataPath = String.format("/data/%s", dirname);

        if(!Files.exists(Path.of(dataPath))) {
            dataPath = String.format("%s/%s", devDataPath, dirname);
        }

        return  dataPath;
    }

    public static String getDirectoryPath(String dirname, boolean create) throws IOException {
        String dirPath = getDirectoryPath(dirname);
        if(!Files.exists(Path.of(dirPath))) {
            Files.createDirectories(Path.of(dirPath));
        }
        return dirPath;
    }

    public static boolean fileExists(String filename) throws IOException {
        return Files.exists(Path.of(getDirectoryPath(filename)));
    }
}
