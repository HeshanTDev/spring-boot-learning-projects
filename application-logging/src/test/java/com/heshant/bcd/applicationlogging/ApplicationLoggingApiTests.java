package com.heshant.bcd.applicationlogging;

import com.heshant.bcd.applicationlogging.dto.ProductRequestDto;
import com.heshant.bcd.applicationlogging.dto.ProductResponseDto;
import com.heshant.bcd.applicationlogging.dto.UserRequestDto;
import com.heshant.bcd.applicationlogging.dto.UserResponseDto;
import com.heshant.bcd.applicationlogging.repository.ProductRepository;
import com.heshant.bcd.applicationlogging.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end tests for the Product and User REST APIs.
 *
 * <p>These are {@link SpringBootTest} tests, so they need a reachable MySQL
 * instance configured in application.properties (or supplied via property
 * overrides). Data created by the tests is removed before each test method.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ApplicationLoggingApiTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanDatabase() {
        productRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("Product API /api/v1/products")
    class ProductApi {

        @Test
        void createProduct_returns201AndPersistedBody() throws Exception {
            ProductRequestDto request = new ProductRequestDto("Laptop", BigDecimal.valueOf(1200.00));

            mockMvc.perform(post("/api/v1/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").isNumber())
                    .andExpect(jsonPath("$.name").value("Laptop"))
                    .andExpect(jsonPath("$.price").value(1200.00));
        }

        @Test
        void getAllProducts_returns200AndArray() throws Exception {
            createProduct("Laptop", 1200.00);
            createProduct("Phone", 800.00);

            mockMvc.perform(get("/api/v1/products"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        void getProductById_returns200() throws Exception {
            long id = createProduct("Laptop", 1200.00);

            mockMvc.perform(get("/api/v1/products/{id}", id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id))
                    .andExpect(jsonPath("$.name").value("Laptop"));
        }

        @Test
        void getProductById_unknownId_returns404() throws Exception {
            mockMvc.perform(get("/api/v1/products/{id}", 999_999L))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").exists());
        }

        @Test
        void updateProduct_returns200AndUpdatedBody() throws Exception {
            long id = createProduct("Laptop", 1200.00);
            ProductRequestDto update = new ProductRequestDto("Updated Laptop", BigDecimal.valueOf(1350.00));

            mockMvc.perform(put("/api/v1/products/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(update)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id))
                    .andExpect(jsonPath("$.name").value("Updated Laptop"))
                    .andExpect(jsonPath("$.price").value(1350.00));
        }

        @Test
        void updateProduct_unknownId_returns404() throws Exception {
            ProductRequestDto update = new ProductRequestDto("Ghost", BigDecimal.valueOf(10.00));

            mockMvc.perform(put("/api/v1/products/{id}", 999_999L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(update)))
                    .andExpect(status().isNotFound());
        }

        @Test
        void deleteProduct_returns204AndRemovesRow() throws Exception {
            long id = createProduct("Laptop", 1200.00);

            mockMvc.perform(delete("/api/v1/products/{id}", id))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get("/api/v1/products/{id}", id))
                    .andExpect(status().isNotFound());
        }

        @Test
        void deleteProduct_unknownId_returns404() throws Exception {
            mockMvc.perform(delete("/api/v1/products/{id}", 999_999L))
                    .andExpect(status().isNotFound());
        }

        @Test
        void createProduct_blankName_returns400() throws Exception {
            mockMvc.perform(post("/api/v1/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"\",\"price\":10.00}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.name").exists());
        }

        @Test
        void createProduct_negativePrice_returns400() throws Exception {
            mockMvc.perform(post("/api/v1/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"Laptop\",\"price\":-5.00}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.price").exists());
        }

        @Test
        void createProduct_nullPrice_returns400() throws Exception {
            mockMvc.perform(post("/api/v1/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"Laptop\"}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.price").exists());
        }

        @Test
        void createProduct_malformedJson_returns400() throws Exception {
            mockMvc.perform(post("/api/v1/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{not-json"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").exists());
        }

        private long createProduct(String name, double price) throws Exception {
            ProductRequestDto request = new ProductRequestDto(name, BigDecimal.valueOf(price));
            String body = mockMvc.perform(post("/api/v1/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            return objectMapper.readValue(body, ProductResponseDto.class).id();
        }
    }

    @Nested
    @DisplayName("User API /api/v1/users")
    class UserApi {

        @Test
        void createUser_returns201AndPersistedBody() throws Exception {
            UserRequestDto request = new UserRequestDto("Heshan", "heshan@example.com");

            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").isNumber())
                    .andExpect(jsonPath("$.name").value("Heshan"))
                    .andExpect(jsonPath("$.email").value("heshan@example.com"));
        }

        @Test
        void getAllUsers_returns200AndArray() throws Exception {
            createUser("Heshan", "heshan@example.com");
            createUser("Alice", "alice@example.com");

            mockMvc.perform(get("/api/v1/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        void getUserById_returns200() throws Exception {
            long id = createUser("Heshan", "heshan@example.com");

            mockMvc.perform(get("/api/v1/users/{id}", id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id))
                    .andExpect(jsonPath("$.email").value("heshan@example.com"));
        }

        @Test
        void getUserById_unknownId_returns404() throws Exception {
            mockMvc.perform(get("/api/v1/users/{id}", 999_999L))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").exists());
        }

        @Test
        void updateUser_returns200AndUpdatedBody() throws Exception {
            long id = createUser("Heshan", "heshan@example.com");
            UserRequestDto update = new UserRequestDto("Heshan T", "heshan.updated@example.com");

            mockMvc.perform(put("/api/v1/users/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(update)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id))
                    .andExpect(jsonPath("$.name").value("Heshan T"))
                    .andExpect(jsonPath("$.email").value("heshan.updated@example.com"));
        }

        @Test
        void updateUser_unknownId_returns404() throws Exception {
            UserRequestDto update = new UserRequestDto("Ghost", "ghost@example.com");

            mockMvc.perform(put("/api/v1/users/{id}", 999_999L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(update)))
                    .andExpect(status().isNotFound());
        }

        @Test
        void deleteUser_returns204AndRemovesRow() throws Exception {
            long id = createUser("Heshan", "heshan@example.com");

            mockMvc.perform(delete("/api/v1/users/{id}", id))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get("/api/v1/users/{id}", id))
                    .andExpect(status().isNotFound());
        }

        @Test
        void deleteUser_unknownId_returns404() throws Exception {
            mockMvc.perform(delete("/api/v1/users/{id}", 999_999L))
                    .andExpect(status().isNotFound());
        }

        @Test
        void createUser_blankName_returns400() throws Exception {
            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"\",\"email\":\"heshan@example.com\"}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.name").exists());
        }

        @Test
        void createUser_invalidEmail_returns400() throws Exception {
            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"Heshan\",\"email\":\"not-an-email\"}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.email").exists());
        }

        @Test
        void createUser_blankEmail_returns400() throws Exception {
            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"Heshan\",\"email\":\"\"}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.email").exists());
        }

        private long createUser(String name, String email) throws Exception {
            UserRequestDto request = new UserRequestDto(name, email);
            String body = mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            return objectMapper.readValue(body, UserResponseDto.class).id();
        }
    }
}
