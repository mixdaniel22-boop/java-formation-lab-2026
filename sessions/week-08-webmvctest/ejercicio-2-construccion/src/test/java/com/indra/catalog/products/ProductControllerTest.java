package com.indra.catalog.products;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
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
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.indra.catalog.products.application.ProductService;
import com.indra.catalog.products.domain.exception.ProductNotFoundException;
import com.indra.catalog.products.domain.model.Product;
import com.indra.catalog.products.web.ProductController;
import com.indra.catalog.products.web.ProductWebMapper;

@WebMvcTest(ProductController.class)
@Import(ProductWebMapper.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    @WithMockUser
    @DisplayName("test Get Product by ID - Success")
    void testGetProductById() throws Exception {
        Product product = new Product();
        product.setId("PROD-001");
        product.setName("producto 1");
        product.setPrice(new BigDecimal("200.00"));
        product.setStock(3);
        when(productService.findById("PROD-001")).thenReturn(product);

        mockMvc.perform(get("/api/products/PROD-001")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("PROD-001"))
                .andExpect(jsonPath("$.name").value("producto 1"))
                .andExpect(jsonPath("$.price").value(200.00))
                .andExpect(jsonPath("$.stock").value(3));
    }

    @Test
    @WithMockUser
    @DisplayName("test Get Product by ID - Not Found")
    void testGetProductByIdNotFound() throws Exception {
        when(productService.findById("PROD-999")).thenThrow(new ProductNotFoundException("PROD-999"));

        mockMvc.perform(get("/api/products/PROD-999")
                .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PRODUCT_NOT_FOUND"));
    }

    @Test
    @WithMockUser
    @DisplayName("test  Created Product - Success")
    void testCreateProduct() throws Exception {
        when(productService.create(any())).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            product.setId("PROD-001");
            product.setName("producto x");
            product.setPrice(new BigDecimal("100.00"));
            product.setStock(3);
            return product;
        });

        mockMvc.perform(post("/api/products")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name": "producto x",
                            "price": 100.00,
                            "stock": 3
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/products/PROD-001"))
                .andExpect(jsonPath("$.id").value("PROD-001"))
                .andExpect(jsonPath("$.name").value("producto x"))
                .andExpect(jsonPath("$.price").value(100.00))
                .andExpect(jsonPath("$.stock").value(3));
    }

    @Test
    @WithMockUser
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
    @WithMockUser
    @DisplayName("test Create Product - Validation Error")
    void testCreateProductValidationErrorPrice() throws Exception {
        mockMvc.perform(post("/api/products")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name": "PROD-001",
                            "price": "",
                            "stock": 3
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors[0]").value(containsString("price")));
    }

    @Test
    @WithMockUser
    @DisplayName("test  DELETE Product - Success")
    void testDeleteProductOk() throws Exception {
        mockMvc.perform(delete("/api/products/PROD-001")
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    @DisplayName("test  DELETE Product - Error")
    void testDeleteProductError() throws Exception {

        doThrow(new ProductNotFoundException("PROD-001")).when(productService).delete("PROD-001");

        mockMvc.perform(delete("/api/products/PROD-001")
                .with(csrf()))
                .andExpect(status().isNotFound());
    }
}
