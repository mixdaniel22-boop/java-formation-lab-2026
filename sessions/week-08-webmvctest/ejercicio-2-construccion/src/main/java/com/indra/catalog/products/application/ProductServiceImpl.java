package com.indra.catalog.products.application;

import java.util.concurrent.atomic.AtomicLong;

import com.indra.catalog.products.domain.ProductRepository;
import com.indra.catalog.products.domain.exception.ProductNotFoundException;
import com.indra.catalog.products.domain.model.Product;
import com.indra.catalog.products.web.ProductWebMapper;
import com.indra.catalog.products.web.dto.CreateProductRequest;
import com.indra.catalog.products.web.dto.ProductResponse;

public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private final ProductWebMapper mapper;
    private AtomicLong sequence;

    public ProductServiceImpl(ProductRepository repository, ProductWebMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
        this.sequence = new AtomicLong();
    }

    @Override
    public ProductResponse create(CreateProductRequest request) {
        Product product = mapper.toDomain(request);
        // Normalización exigida por el área de compras: razón social en mayúsculas y
        // NIT sin puntos
        product.setName(product.getName().trim().toUpperCase());
        product.setPrice(product.getPrice());
        product.setStock(product.getStock());

        product.setId("SUP-%03d".formatted(sequence.incrementAndGet()));
        return mapper.toResponse(repository.save(product));
    }

    @Override
    public ProductResponse findById(String id) {
        return mapper.toResponse(repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id)));
    }

    @Override
    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
