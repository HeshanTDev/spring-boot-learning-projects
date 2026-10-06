package com.heshant.bcd.applicationlogging.service;

import com.heshant.bcd.applicationlogging.dto.ProductRequestDto;
import com.heshant.bcd.applicationlogging.dto.ProductResponseDto;

import java.util.List;

public interface ProductService {

    ProductResponseDto createProduct(ProductRequestDto productRequestDto);

    List<ProductResponseDto> getAllProducts();

    ProductResponseDto getProductById(Long id);

    ProductResponseDto updateProduct(Long id, ProductRequestDto productRequestDto);

    void deleteProduct(Long id);
}
