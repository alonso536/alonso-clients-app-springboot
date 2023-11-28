package org.alonso.clientsapp.clientsapp.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.alonso.clientsapp.clientsapp.models.entity.Invoice;
import org.alonso.clientsapp.clientsapp.models.services.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        Optional<Invoice> optionalInvoice = null;
        Map<String, Object> response = new HashMap<>();

        try {
            optionalInvoice = invoiceService.findById(id);
        } catch (DataAccessException e) {
            response.put("msg", "Error en la conexión a la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return ResponseEntity.internalServerError().body(response);
        }

        if (optionalInvoice.isPresent()) {
            response.put("invoice", optionalInvoice.orElseThrow());
            return ResponseEntity.ok().body(response);
        }

        response.put("msg", "No existe una factura con el id " + id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @PostMapping
    public ResponseEntity<?> store(@RequestBody Invoice invoice) {
        Map<String, Object> response = new HashMap<>();
        Invoice newInvoice = invoiceService.save(invoice);

        response.put("msg", "Factura creada con exito");
        response.put("invoice", newInvoice);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> destroy(@PathVariable Long id) {
        Optional<Invoice> optionalInvoice = null;
        Map<String, Object> response = new HashMap<>();

        try {
            optionalInvoice = invoiceService.findById(id);
        } catch (DataAccessException e) {
            response.put("msg", "Error en la conexión a la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return ResponseEntity.internalServerError().body(response);
        }

        if (!optionalInvoice.isPresent()) {
            response.put("msg", "No existe una factura con el id " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        invoiceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}