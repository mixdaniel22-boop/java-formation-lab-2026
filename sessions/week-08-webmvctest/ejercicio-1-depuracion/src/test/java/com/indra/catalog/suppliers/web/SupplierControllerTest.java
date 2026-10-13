package com.indra.catalog.suppliers.web;

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

import com.indra.catalog.suppliers.application.SupplierServiceImpl;
import com.indra.catalog.suppliers.domain.Supplier;
import com.indra.catalog.suppliers.domain.SupplierNotFoundException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SupplierController.class)
@Import(SupplierWebMapper.class)
class SupplierControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SupplierServiceImpl supplierService;

    @Test
    @DisplayName("test Get Supplier by ID - Success")
    void testGetSupplierById() throws Exception {
        Supplier supplier = new Supplier();
        supplier.setId("SUP-001");
        supplier.setName("ACME LTDA");
        supplier.setTaxId("900123456-7");
        supplier.setEmail("compras@acme.co");
        when(supplierService.findById("SUP-001")).thenReturn(supplier);

        mockMvc.perform(get("/api/suppliers/SUP-001"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "{\"id\":\"SUP-001\",\"name\":\"ACME LTDA\",\"taxId\":\"900123456-7\",\"email\":\"compras@acme.co\"}"));
    }

    @Test
    @DisplayName("test Get Supplier by ID - Not Found")
    void testGetSupplierByIdNotFound() throws Exception {
        when(supplierService.findById("SUP-999")).thenThrow(new SupplierNotFoundException("SUP-999"));

        mockMvc.perform(get("/api/suppliers/SUP-999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("SUPPLIER_NOT_FOUND"));
    }

    @Test
    @DisplayName("test  Created Supplier - Success")
    void testCreateSupplier() throws Exception {
        when(supplierService.create(any())).thenAnswer(invocation -> {
            Supplier supplier = invocation.getArgument(0);
            supplier.setId("SUP-001");
            return supplier;
        });

        mockMvc.perform(post("/api/suppliers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name": "  acme ltda ",
                            "taxId": "900.123.456-7",
                            "email": "compras@acme.co"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/suppliers/SUP-001"))
                .andExpect(jsonPath("$.name").value("ACME LTDA"))
                .andExpect(jsonPath("$.taxId").value("900123456-7"));
    }

    @Test
    @DisplayName("test Create Supplier - Validation Error")
    void testCreateSupplierValidationError() throws Exception {
        mockMvc.perform(post("/api/suppliers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name": "",
                            "taxId": "900.123.456-7",
                            "email": "compras@acme.co"
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors[0]").value(containsString("name")));
    }

    @Test
    @DisplayName("test  DELETE Supplier - Success")
    void testDeleteSupplierOk() throws Exception {
        mockMvc.perform(delete("/api/suppliers/SUP-001"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("test  DELETE Supplier - Error")
    void testDeleteSupplierError() throws Exception {

        doThrow(new SupplierNotFoundException("SUP-001")).when(supplierService).delete("SUP-001");

        mockMvc.perform(delete("/api/suppliers/SUP-001"))
                .andExpect(status().isNotFound());
    }
}
