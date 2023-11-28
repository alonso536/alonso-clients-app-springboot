package org.alonso.clientsapp.clientsapp.models.repositories;

import java.util.List;

import org.alonso.clientsapp.clientsapp.models.entity.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProductRepository extends CrudRepository<Product, Long>, PagingAndSortingRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.name LIKE %?1% OR p.sku LIKE %?1%")
    List<Product> findByName(String term);
}
