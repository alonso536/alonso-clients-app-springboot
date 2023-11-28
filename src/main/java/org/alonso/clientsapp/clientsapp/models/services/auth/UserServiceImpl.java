package org.alonso.clientsapp.clientsapp.models.services.auth;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.alonso.clientsapp.clientsapp.models.dto.UserDto;
import org.alonso.clientsapp.clientsapp.models.dto.mappers.UserMapper;
import org.alonso.clientsapp.clientsapp.models.entity.Role;
import org.alonso.clientsapp.clientsapp.models.entity.User;
import org.alonso.clientsapp.clientsapp.models.repositories.RoleRepository;
import org.alonso.clientsapp.clientsapp.models.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        List<User> users = (List<User>) userRepository.findAll();
        return users
                .stream()
                .map(u -> UserMapper.builder().setUser(u).build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> findById(Long id) {
        return userRepository.findById(id).map(u -> UserMapper.builder().setUser(u).build());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> findByUsername(String username) {
        return userRepository.findByUsername(username).map(u -> UserMapper.builder().setUser(u).build());
    }

    @Override
    public UserDto save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Optional<Role> roleOptional = roleRepository.findByName("ROLE_USER");
        if (roleOptional.isPresent()) {
            user.addRole(roleOptional.orElseThrow());
        }

        if (user.isAdmin()) {
            Optional<Role> roleAdminOptional = roleRepository.findByName("ROLE_ADMIN");
            if (roleAdminOptional.isPresent()) {
                user.addRole(roleAdminOptional.orElseThrow());
            }
        }
        return UserMapper.builder().setUser(userRepository.save(user)).build();
    }

    @Override
    public void remove(Long id) {
        userRepository.deleteById(id);
    }
}
