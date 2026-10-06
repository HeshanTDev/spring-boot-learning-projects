package com.heshant.bcd.applicationlogging.service.impl;

import com.heshant.bcd.applicationlogging.dto.ProductRequestDto;
import com.heshant.bcd.applicationlogging.dto.ProductResponseDto;
import com.heshant.bcd.applicationlogging.entity.Product;
import com.heshant.bcd.applicationlogging.exception.ResourceNotFoundException;
import com.heshant.bcd.applicationlogging.repository.ProductRepository;
import com.heshant.bcd.applicationlogging.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public ProductResponseDto createProduct(ProductRequestDto productRequestDto) {
        log.info("Creating product: {}", productRequestDto.name());

        Product product = new Product();
        product.setName(productRequestDto.name());
        product.setPrice(productRequestDto.price());

        Product savedProduct = productRepository.save(product);
        log.info("Product created with id: {}", savedProduct.getId());

        return toResponse(savedProduct);
    }

    @Override
    public List<ProductResponseDto> getAllProducts() {
        log.info("Fetching all products");
        return productRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public ProductResponseDto getProductById(Long id) {
        log.info("Fetching product with id: {}", id);
        return toResponse(findProduct(id));
    }

    @Override
    public ProductResponseDto updateProduct(Long id, ProductRequestDto productRequestDto) {
        log.info("Updating product with id: {}", id);

        Product product = findProduct(id);
        product.setName(productRequestDto.name());
        product.setPrice(productRequestDto.price());

        Product savedProduct = productRepository.save(product);
        log.info("Product updated with id: {}", savedProduct.getId());

        return toResponse(savedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        log.info("Deleting product with id: {}", id);

        Product product = findProduct(id);
        productRepository.delete(product);

        log.info("Product deleted with id: {}", id);
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found with id: {}", id);
                    return new ResourceNotFoundException("Product not found with id: " + id);
                });
    }

    private ProductResponseDto toResponse(Product product) {
        return new ProductResponseDto(product.getId(), product.getName(), product.getPrice());
    }
}
