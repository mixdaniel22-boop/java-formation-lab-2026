package com.indra.catalog.suppliers.application;

import com.indra.catalog.suppliers.domain.Supplier;
import com.indra.catalog.suppliers.domain.SupplierNotFoundException;
import com.indra.catalog.suppliers.domain.SupplierRepository;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository repository;
    private AtomicLong sequence;

    public SupplierServiceImpl(SupplierRepository repository) {
        this.repository = repository;
        this.sequence = new AtomicLong();
    }

    @Override
    public Supplier create(Supplier supplier) {
        // Normalización exigida por el área de compras: razón social en mayúsculas y
        // NIT sin puntos
        supplier.setName(supplier.getName().trim().toUpperCase());
        supplier.setTaxId(supplier.getTaxId().replace(".", "").trim());
        supplier.setInternalNotes("Creado vía API el " + LocalDateTime.now());

        supplier.setId("SUP-%03d".formatted(sequence.incrementAndGet()));
        return repository.save(supplier);
    }

    @Override
    public Supplier findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new SupplierNotFoundException(id));
    }

    @Override
    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new SupplierNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
