package com.indra.catalog.products.web.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateProductRequest(String id,
        @NotBlank(message = "obligatorio, no vacío, máximo 120 caracteres") @Size(max = 120) String name,
        @NotNull @DecimalMin("0.0") BigDecimal price,
        @Min(0) int stock) {
}
