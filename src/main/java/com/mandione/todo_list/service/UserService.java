package com.mandione.todo_list.service;

import com.mandione.todo_list.dto.UserDto;

/**
 * Interface du service utilisateur.
 * Respect du principe DIP : Les contrôleurs dépendent de cette abstraction.
 */
public interface UserService {

    /**
     * Crée un nouvel utilisateur
     */
    UserDto.Response createUser(UserDto.Request userDto);

    /**
     * Récupère un utilisateur par son nom d'utilisateur
     */
    UserDto.Response findByUsername(String username);

    /**
     * Vérifie si un nom d'utilisateur existe déjà
     */
    boolean existsByUsername(String username);

    /**
     * Vérifie si un email existe déjà
     */
    boolean existsByEmail(String email);
}