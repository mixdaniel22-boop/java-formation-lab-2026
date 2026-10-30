package com.indra.jpa.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.indra.jpa.orders.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}