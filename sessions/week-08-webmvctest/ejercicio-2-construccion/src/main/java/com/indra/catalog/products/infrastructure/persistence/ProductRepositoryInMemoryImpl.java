package com.indra.catalog.products.infrastructure.persistence;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.indra.catalog.products.domain.ProductRepository;
import com.indra.catalog.products.domain.model.Product;

public class ProductRepositoryInMemoryImpl implements ProductRepository {

    private Map<String, Product> products;

    public ProductRepositoryInMemoryImpl() {
        this.products = new ConcurrentHashMap<>();
    }

    @Override
    public Product save(Product supplier) {
        products.put(supplier.getId(), supplier);
        return supplier;
    }

    @Override
    public Optional<Product> findById(String id) {
        return Optional.ofNullable(products.get(id));
    }

    @Override
    public boolean existsById(String id) {
        return products.containsKey(id);
    }

    @Override
    public void deleteById(String id) {
        products.remove(id);
    };
}
