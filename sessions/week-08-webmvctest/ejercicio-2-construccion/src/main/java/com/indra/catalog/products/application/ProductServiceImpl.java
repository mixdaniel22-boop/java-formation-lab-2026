package com.indra.catalog.products.application;

import java.util.concurrent.atomic.AtomicLong;

import com.indra.catalog.products.domain.ProductRepository;
import com.indra.catalog.products.domain.exception.ProductNotFoundException;
import com.indra.catalog.products.domain.model.Product;

public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private AtomicLong sequence;

    public ProductServiceImpl(ProductRepository repository) {
        this.repository = repository;
        this.sequence = new AtomicLong();
    }

    @Override
    public Product create(CreateProductCommand command) {
        Product product = new Product("PROD-%03d".formatted(sequence.incrementAndGet()), command.name(), command.price(), command.stock());
        return repository.save(product);
    }

    @Override
    public Product findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
