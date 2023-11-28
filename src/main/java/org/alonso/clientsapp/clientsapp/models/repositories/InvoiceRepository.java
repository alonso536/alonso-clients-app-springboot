package org.alonso.clientsapp.clientsapp.models.repositories;

import org.alonso.clientsapp.clientsapp.models.entity.Invoice;
import org.springframework.data.repository.CrudRepository;

public interface InvoiceRepository extends CrudRepository<Invoice, Long> {

}
