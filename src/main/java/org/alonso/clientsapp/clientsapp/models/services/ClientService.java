package org.alonso.clientsapp.clientsapp.models.services;

import java.util.List;
import java.util.Optional;

import org.alonso.clientsapp.clientsapp.models.entity.Client;
import org.alonso.clientsapp.clientsapp.models.entity.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientService {
    List<Client> findAll();

    Page<Client> findAll(Pageable page);

    Optional<Client> findById(Long id);

    Client save(Client client);

    void delete(Long id);

    List<Region> findRegions();
}
