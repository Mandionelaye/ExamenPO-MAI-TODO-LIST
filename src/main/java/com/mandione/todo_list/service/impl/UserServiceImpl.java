package com.mandione.todo_list.service.impl;

import com.mandione.todo_list.dto.UserDto;
import com.mandione.todo_list.entity.User;
import com.mandione.todo_list.exception.ResourceAlreadyExistsException;
import com.mandione.todo_list.exception.ResourceNotFoundException;
import com.mandione.todo_list.mapper.UserMapper;
import com.mandione.todo_list.repository.UserRepository;
import com.mandione.todo_list.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implémentation du service utilisateur.
 * Respect du principe SRP : Gestion métier des utilisateurs uniquement.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto.Response createUser(UserDto.Request userDto) {
        log.info("Création d'un nouvel utilisateur: {}", userDto.getUsername());

        // Vérification unicité
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new ResourceAlreadyExistsException("Nom d'utilisateur déjà utilisé: " + userDto.getUsername());
        }

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new ResourceAlreadyExistsException("Email déjà utilisé: " + userDto.getEmail());
        }

        // Création de l'entité
        User user = userMapper.toEntity(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        // Sauvegarde
        User savedUser = userRepository.save(user);
        log.info("Utilisateur créé avec succès: ID={}", savedUser.getId());

        return userMapper.toDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto.Response findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé: " + username));
        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}