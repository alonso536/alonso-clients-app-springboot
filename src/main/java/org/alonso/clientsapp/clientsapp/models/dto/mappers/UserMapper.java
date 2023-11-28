package org.alonso.clientsapp.clientsapp.models.dto.mappers;

import org.alonso.clientsapp.clientsapp.models.dto.UserDto;
import org.alonso.clientsapp.clientsapp.models.entity.User;

public class UserMapper {
    private User user;

    private UserMapper() {
    }

    public static UserMapper builder() {
        return new UserMapper();
    }

    public UserMapper setUser(User user) {
        this.user = user;
        return this;
    }

    public UserDto build() {
        if (this.user == null) {
            throw new RuntimeException("Debe pasar el entity al user");
        }
        boolean isAdmin = this.user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"));
        return new UserDto(this.user.getId(), this.user.getUsername(), this.user.getEmail(), isAdmin);
    }
}
