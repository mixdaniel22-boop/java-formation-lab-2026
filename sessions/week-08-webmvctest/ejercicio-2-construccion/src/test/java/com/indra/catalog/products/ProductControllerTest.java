package com.indra.catalog.products;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.indra.catalog.products.application.CreateProductCommand;
import com.indra.catalog.products.application.ProductService;
import com.indra.catalog.products.domain.exception.ProductNotFoundException;
import com.indra.catalog.products.domain.model.Product;
import com.indra.catalog.products.web.ProductController;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    private static final Product LAPTOP = new Product("PROD-0001", "Laptop", new BigDecimal("100.00"), 3);

    @Test
    @WithMockUser(roles = { "EDITOR", "ADMIN" })
    @DisplayName("test Get Product by ID - Success")
    void testGetProductById() throws Exception {
        when(productService.findById(anyString())).thenReturn(LAPTOP);

        mockMvc.perform(get("/api/products/PROD-001")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("PROD-0001"))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(100.00))
                .andExpect(jsonPath("$.stock").value(3));
    }

    @Test
    @WithMockUser(roles = "EDITOR")
    @DisplayName("test Get Product by ID - Not Found")
    void testGetProductByIdNotFound() throws Exception {
        when(productService.findById("PROD-999")).thenThrow(new ProductNotFoundException("PROD-999"));

        mockMvc.perform(get("/api/products/PROD-999")
                .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PRODUCT_NOT_FOUND"));
    }

    @Test
    @WithMockUser(roles = { "EDITOR", "ADMIN" })
    @DisplayName("test  Created Product - Success")
    void testCreateProduct() throws Exception {
        when(productService.create(any(CreateProductCommand.class))).thenReturn(LAPTOP);

        mockMvc.perform(post("/api/products")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name": "Laptop",
                            "price": 100.00,
                            "stock": 3
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/products/PROD-0001"))
                .andExpect(jsonPath("$.id").value("PROD-0001"))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(100.00))
                .andExpect(jsonPath("$.stock").value(3));
    }

    @Test
    @WithMockUser(roles = { "EDITOR" })
    @DisplayName("test Create Product - Validation Error Name Field")
    void testCreateProductValidationErrorName() throws Exception {
        mockMvc.perform(post("/api/products")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name": "",
                            "price": 100.00,
                            "stock": 3
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors[0]").value(containsString("name")));
    }

    @Test
    @WithMockUser(roles = { "EDITOR" })
    @DisplayName("test Create Product - Validation Error")
    void testCreateProductValidationErrorPrice() throws Exception {
        mockMvc.perform(post("/api/products")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name": "PROD-0001",
                            "price": "",
                            "stock": 3
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors[0]").value(containsString("price")));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    @DisplayName("test  DELETE Product - Success")
    void testDeleteProductOk() throws Exception {
        mockMvc.perform(delete("/api/products/PROD-0001")
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("test  DELETE Product - Success")
    void testDeleteProductOk401() throws Exception {
        mockMvc.perform(delete("/api/products/PROD-0001")
                .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    @DisplayName("test  DELETE Product - Error")
    void testDeleteProductError() throws Exception {

        doThrow(new ProductNotFoundException("PROD-0001")).when(productService).delete("PROD-0001");

        mockMvc.perform(delete("/api/products/PROD-0001")
                .with(csrf()))
                .andExpect(status().isNotFound());
    }
}
