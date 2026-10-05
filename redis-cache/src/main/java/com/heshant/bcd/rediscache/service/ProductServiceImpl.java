package com.heshant.bcd.rediscache.service;

import com.heshant.bcd.rediscache.dto.ProductDto;
import com.heshant.bcd.rediscache.entity.Product;
import com.heshant.bcd.rediscache.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    @Cacheable(value = "PRODUCT_CACHE", key = "#id")
    public ProductDto findById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        return new ProductDto(product.getId(), product.getName(), product.getPrice());
    }

    @Override
    public List<ProductDto> findAll() {
        List<Product> all = productRepository.findAll();
//        return all.stream().map(product -> new ProductDto(product.getId(), product.getName(), product.getPrice())).collect(Collectors.toList());

        List<ProductDto> productDtos = new ArrayList<>();
        for (Product product : all) {
            ProductDto productDto = new ProductDto(product.getId(), product.getName(), product.getPrice());
            productDtos.add(productDto);
        }

        return productDtos;
    }

    @Override
    @CachePut(value = "PRODUCT_CACHE", key = "#result.id()")
    public ProductDto createProduct(ProductDto productDto) {
        Product product = new Product();
        product.setName(productDto.name());
        product.setPrice(productDto.price());

        Product save = productRepository.save(product);

        return new ProductDto(save.getId(), save.getName(), save.getPrice());
    }

    @Override
    @CachePut(value = "PRODUCT_CACHE", key = "#result.id()")
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));

        product.setName(productDto.name());
        product.setPrice(productDto.price());

        Product save = productRepository.save(product);

        return new ProductDto(save.getId(), save.getName(), save.getPrice());
    }

    @Override
    @CacheEvict(value = "PRODUCT_CACHE", key = "#id")
    public void delete(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        productRepository.delete(product);
    }
}
