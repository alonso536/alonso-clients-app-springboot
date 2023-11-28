package org.alonso.clientsapp.clientsapp.models.services.auth;

import java.util.List;
import java.util.Optional;

import org.alonso.clientsapp.clientsapp.models.dto.UserDto;
import org.alonso.clientsapp.clientsapp.models.entity.User;

public interface UserService {
    List<UserDto> findAll();

    Optional<UserDto> findById(Long id);

    Optional<UserDto> findByUsername(String username);

    UserDto save(User user);

    void remove(Long id);
}
