package net.softhem.pos.service;

import net.softhem.pos.dto.ProductDTO;
import net.softhem.pos.exception.ResourceNotFoundException;
import net.softhem.pos.model.Category;
import net.softhem.pos.model.Helpers;
import net.softhem.pos.model.Product;
import net.softhem.pos.repository.CategoryRepository;
import net.softhem.pos.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, FileStorageService fileStorageService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findAll(pageable);
        return Helpers.pageProductDTO(productPage);
    }

    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return convertToDTO(product);
    }

    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) throws IOException {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setStatus(productDTO.isStatus());
        product.setPrice(productDTO.getPrice());
        product.setInStock(productDTO.getInStock());
        product.setDescription(productDTO.getDescription());
        product.setUpdatedAt(LocalDateTime.now());
        // Handle category
        if (productDTO.getCategoryId() != null) {
            categoryRepository.findById(productDTO.getCategoryId())
                    .ifPresent(product::setCategory);
        }
        // Handle base64 image
        if (productDTO.getImage() != null && !productDTO.getImage().isEmpty()) {
            String filename = fileStorageService.storeBase64Image(productDTO.getImage());
            product.setImage(filename);
        }
        Product savedProduct = productRepository.save(product);
        return convertToDTO(savedProduct);
    }

    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) throws IOException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        product.setName(productDTO.getName());
        product.setStatus(productDTO.isStatus());
        product.setPrice(productDTO.getPrice());
        product.setInStock(productDTO.getInStock());
        product.setDescription(productDTO.getDescription());
        product.setUpdatedAt(LocalDateTime.now());
        // Handle category
        if (productDTO.getCategoryId() != null) {
            categoryRepository.findById(productDTO.getCategoryId())
                    .ifPresent(product::setCategory);
        }
        // Handle base64 image
        // Check for either protocol - no need to update url
        if (productDTO.getImage().contains("http://") || productDTO.getImage().contains("https://")) {
            System.out.println("Contains HTTP or HTTPS protocol");
        } else {
            if (productDTO.getImage() != null && !productDTO.getImage().isEmpty()) {
                String filename = fileStorageService.storeBase64Image(productDTO.getImage());
                product.setImage(filename);
            }
        }

        Product updatedProduct = productRepository.save(product);
        return convertToDTO(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            // throw new ResourceNotFoundException("Product not found with id: " + id);
        } else {
            productRepository.deleteById(id);
        }
    }

    private ProductDTO convertToDTO(Product product) {
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
}