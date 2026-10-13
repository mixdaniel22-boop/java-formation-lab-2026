package com.indra.catalog.suppliers.application;

import com.indra.catalog.suppliers.domain.Supplier;
import com.indra.catalog.suppliers.domain.SupplierNotFoundException;
import com.indra.catalog.suppliers.domain.SupplierRepository;
import com.indra.catalog.suppliers.web.SupplierWebMapper;
import com.indra.catalog.suppliers.web.dto.CreateSupplierRequest;
import com.indra.catalog.suppliers.web.dto.SupplierResponse;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository repository;
    private final SupplierWebMapper mapper;
    private AtomicLong sequence;

    public SupplierServiceImpl(SupplierRepository repository, SupplierWebMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
        this.sequence = new AtomicLong();
    }

    @Override
    public SupplierResponse create(CreateSupplierRequest request) {
        Supplier supplier = mapper.toDomain(request);
        // Normalización exigida por el área de compras: razón social en mayúsculas y
        // NIT sin puntos
        supplier.setName(supplier.getName().trim().toUpperCase());
        supplier.setTaxId(supplier.getTaxId().replace(".", "").trim());
        supplier.setInternalNotes("Creado vía API el " + LocalDateTime.now());

        supplier.setId("SUP-%03d".formatted(sequence.incrementAndGet()));
        return mapper.toResponse(repository.save(supplier));
    }

    @Override
    public SupplierResponse findById(String id) {
        return mapper.toResponse(repository.findById(id)
                .orElseThrow(() -> new SupplierNotFoundException(id)));
    }

    @Override
    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new SupplierNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
