package org.alonso.clientsapp.clientsapp.models.services;

import java.util.List;
import java.util.Optional;

import org.alonso.clientsapp.clientsapp.models.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    List<Product> findAll();

    List<Product> findByName(String term);

    Page<Product> findAll(Pageable page);

    Product save(Product product);

    Optional<Product> findById(Long id);
}
