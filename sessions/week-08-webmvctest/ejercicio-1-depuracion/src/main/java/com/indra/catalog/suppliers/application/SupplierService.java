package com.indra.catalog.suppliers.application;

import com.indra.catalog.suppliers.web.dto.CreateSupplierRequest;
import com.indra.catalog.suppliers.web.dto.SupplierResponse;

public interface SupplierService {

    public SupplierResponse create(CreateSupplierRequest supplier);

    public SupplierResponse findById(String id);

    public void delete(String id);
}
