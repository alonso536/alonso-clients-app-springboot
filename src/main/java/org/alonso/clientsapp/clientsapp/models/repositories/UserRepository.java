package org.alonso.clientsapp.clientsapp.models.repositories;

import java.util.Optional;

import org.alonso.clientsapp.clientsapp.models.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.username = ?1")
    Optional<User> getByUsername(String username);
}
