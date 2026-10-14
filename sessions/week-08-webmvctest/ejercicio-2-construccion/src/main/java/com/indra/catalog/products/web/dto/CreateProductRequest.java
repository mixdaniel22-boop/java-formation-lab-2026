package com.indra.catalog.products.web.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateProductRequest(String id,
                @NotBlank(message = "obligatorio, no vacío, máximo 120 caracteres") @Size(max = 120) String name,
                @NotBlank(message = "obligatorio, mayor que 0") @DecimalMin("0.0") BigDecimal price,
                @NotBlank(message = "obligatorio, mayor o igual que 0") @Min(0) int stock) {
}
