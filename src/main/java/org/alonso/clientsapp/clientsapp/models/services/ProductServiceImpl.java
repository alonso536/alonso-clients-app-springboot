package org.alonso.clientsapp.clientsapp.models.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.alonso.clientsapp.clientsapp.models.entity.Product;
import org.alonso.clientsapp.clientsapp.models.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Product> findAll() {
        List<Product> products = (List<Product>) productRepository.findAll();
        return products
                .stream()
                .filter(p -> p.getStatus())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findByName(String term) {
        List<Product> products = (List<Product>) productRepository.findByName(term);
        return products
                .stream()
                .filter(p -> p.getStatus())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> findAll(Pageable page) {
        return productRepository.findAll(page);
    }

    @Override
    @Transactional
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }
}
