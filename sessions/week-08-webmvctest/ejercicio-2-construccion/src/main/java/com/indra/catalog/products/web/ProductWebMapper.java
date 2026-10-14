package com.indra.catalog.products.web;

import com.indra.catalog.products.domain.model.Product;
import com.indra.catalog.products.web.dto.CreateProductRequest;
import com.indra.catalog.products.web.dto.ProductResponse;

import org.springframework.stereotype.Component;

@Component
public class ProductWebMapper {

    public Product toDomain(CreateProductRequest request) {
        Product product = new Product();
        product.setId(request.id());
        product.setName(request.name());
        product.setPrice(request.price());
        product.setStock(request.stock());
        return product;
    }

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getPrice(), product.getStock());
    }
}
