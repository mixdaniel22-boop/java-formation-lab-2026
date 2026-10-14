package com.indra.catalog.products.application;

import com.indra.catalog.products.web.dto.CreateProductRequest;
import com.indra.catalog.products.web.dto.ProductResponse;

public interface ProductService {

    ProductResponse create(CreateProductRequest product);

    ProductResponse findById(String id);

    void delete(String id);

}
