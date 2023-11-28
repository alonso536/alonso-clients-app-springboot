package org.alonso.clientsapp.clientsapp.models.services;

import java.util.Optional;

import org.alonso.clientsapp.clientsapp.models.entity.Invoice;

public interface InvoiceService {
    Optional<Invoice> findById(Long id);

    Invoice save(Invoice invoice);

    void delete(Long id);
}
