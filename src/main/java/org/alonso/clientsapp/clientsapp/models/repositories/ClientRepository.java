package org.alonso.clientsapp.clientsapp.models.repositories;

import java.util.List;

import org.alonso.clientsapp.clientsapp.models.entity.Client;
import org.alonso.clientsapp.clientsapp.models.entity.Region;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ClientRepository extends CrudRepository<Client, Long>, PagingAndSortingRepository<Client, Long> {
    @Query("FROM Region")
    List<Region> findRegions();
}
