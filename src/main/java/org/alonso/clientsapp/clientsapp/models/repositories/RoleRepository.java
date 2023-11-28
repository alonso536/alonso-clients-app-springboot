package org.alonso.clientsapp.clientsapp.models.repositories;

import java.util.Optional;

import org.alonso.clientsapp.clientsapp.models.entity.Role;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
