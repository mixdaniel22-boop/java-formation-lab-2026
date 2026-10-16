package com.indra.catalog.products.web.dto;

import java.math.BigDecimal;

import com.indra.catalog.products.domain.model.Product;

public record ProductResponse(String id, String name, BigDecimal price, int stock) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(product.id(), product.name(), product.price(), product.stock());
    }
}
