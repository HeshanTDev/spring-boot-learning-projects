package com.heshant.bcd.rediscache.service;

import com.heshant.bcd.rediscache.dto.ProductDto;

import java.util.List;

public interface ProductService {
   ProductDto findById(Long id);
   List<ProductDto> findAll();
   ProductDto createProduct(ProductDto productDto);
   ProductDto updateProduct(Long id, ProductDto productDto);
   void delete(Long id);
}
