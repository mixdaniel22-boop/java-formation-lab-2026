package com.indra.catalog.suppliers.application;

import com.indra.catalog.suppliers.domain.Supplier;

public interface SupplierService {

    public Supplier create(Supplier supplier);

    public Supplier findById(String id);

    public void delete(String id);
}
