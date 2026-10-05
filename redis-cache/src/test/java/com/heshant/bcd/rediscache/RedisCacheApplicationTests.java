package com.heshant.bcd.rediscache;

import com.heshant.bcd.rediscache.dto.ProductDto;
import com.heshant.bcd.rediscache.entity.Product;
import com.heshant.bcd.rediscache.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.http.MediaType;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RedisCacheApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CacheManager cacheManager;

    @MockitoSpyBean
    private ProductRepository productRepositorySpy;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();

        Cache cache = cacheManager.getCache("PRODUCT_CACHE");

        if (cache != null) {
            cache.clear();
        }

        Mockito.clearInvocations(productRepositorySpy);
    }

    @Test
    void createProduct_shouldSaveToDatabaseAndCache() throws Exception {

        ProductDto productDto = new ProductDto(null, "Laptop", BigDecimal.valueOf(1200));

        // Create product
        MvcResult result = mockMvc.perform(post("/api/v1/products").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(1200))
                .andReturn();

        ProductDto createdProduct =
                objectMapper.readValue(result.getResponse().getContentAsString(), ProductDto.class);

        Long productId = createdProduct.id();

        // Verify database
        assertTrue(productRepository.findById(productId).isPresent());

        // Verify Redis cache
        Cache cache = cacheManager.getCache("PRODUCT_CACHE");

        assertNotNull(cache);

        ProductDto cachedProduct = cache.get(productId, ProductDto.class);

        assertNotNull(cachedProduct);
        assertEquals(productId, cachedProduct.id());
        assertEquals("Laptop", cachedProduct.name());
        assertEquals(BigDecimal.valueOf(1200), cachedProduct.price());
    }

    @Test
    void findById_shouldUseCacheAfterFirstRequest() throws Exception {

        // Save directly to database
        Product product = new Product();
        product.setName("Phone");
        product.setPrice(BigDecimal.valueOf(800));

        product = productRepository.save(product);

        Long productId = product.getId();

        // First request
        mockMvc.perform(get("/api/v1/products/" + productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Phone"));

        // Database should have been called once
        Mockito.verify(productRepositorySpy, Mockito.times(1)).findById(productId);

        // Clear Mockito invocation history
        Mockito.clearInvocations(productRepositorySpy);

        // Second request
        mockMvc.perform(get("/api/v1/products/" + productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Phone"));

        // Database should NOT be called
        Mockito.verify(productRepositorySpy, Mockito.never()).findById(productId);
    }

    @Test
    void updateProduct_shouldUpdateDatabaseAndCache() throws Exception {

        // Create product
        Product product = new Product();
        product.setName("Tablet");
        product.setPrice(BigDecimal.valueOf(500));

        product = productRepository.save(product);

        Long productId = product.getId();

        // Put original product into cache first
        mockMvc.perform(get("/api/v1/products/" + productId)).andExpect(status().isOk());

        ProductDto updatedProduct = new ProductDto(productId, "Updated Tablet", BigDecimal.valueOf(550));

        // Update product
        mockMvc.perform(put("/api/v1/products/" + productId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Updated Tablet"))
                .andExpect(jsonPath("$.price").value(550));

        // Check Redis
        Cache cache =
                cacheManager.getCache("PRODUCT_CACHE");

        assertNotNull(cache);

        ProductDto cachedProduct = cache.get(productId, ProductDto.class);

        assertNotNull(cachedProduct);

        assertEquals("Updated Tablet", cachedProduct.name());

        assertEquals(BigDecimal.valueOf(550), cachedProduct.price());
    }

    @Test
    void deleteProduct_shouldDeleteFromDatabaseAndCache() throws Exception {

        // Create product
        Product product = new Product();
        product.setName("Smartwatch");
        product.setPrice(BigDecimal.valueOf(250));

        product = productRepository.save(product);

        Long productId = product.getId();

        // First load it so that it exists in Redis
        mockMvc.perform(get("/api/v1/products/" + productId)).andExpect(status().isOk());

        // Verify it exists in cache
        Cache cache = cacheManager.getCache("PRODUCT_CACHE");

        assertNotNull(cache);
        assertNotNull(cache.get(productId, ProductDto.class));

        // Delete
        mockMvc.perform(delete("/api/v1/products/" + productId)).andExpect(status().isNoContent());

        // Verify database
        assertFalse(productRepository.findById(productId).isPresent());

        // Verify cache was evicted
        assertNull(cache.get(productId));
    }
}