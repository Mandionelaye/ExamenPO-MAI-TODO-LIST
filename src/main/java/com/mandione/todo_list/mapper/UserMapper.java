package com.mandione.todo_list.mapper;

import com.mandione.todo_list.dto.UserDto;
import com.mandione.todo_list.entity.User;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

/**
 * Mapper pour l'entité User.
 * Respect du principe SRP : Responsabilité unique de transformation des données.
 */
@Component
public class UserMapper implements EntityMapper<User, UserDto.Response> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public UserDto.Response toDto(User user) {
        if (user == null) {
            return null;
        }

        return UserDto.Response.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().format(FORMATTER) : null)
                .build();
    }

    @Override
    public User toEntity(UserDto.Response dto) {
        throw new UnsupportedOperationException("Conversion DTO -> Entity non supportée pour Response");
    }

    /**
     * Convertit un DTO de création en Entité
     */
    public User toEntity(UserDto.Request request) {
        if (request == null) {
            return null;
        }

        return User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword()) // Le mot de passe sera encodé par le service
                .build();
    }
}