package com.mandione.todo_list.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO pour la création et la mise à jour d'un utilisateur.
 * Séparation des concerns : Validation et transfert de données uniquement.
 */
public class UserDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {

        @NotBlank(message = "Le nom d'utilisateur est obligatoire")
        @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit contenir entre 3 et 50 caractères")
        private String username;

        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "L'email doit être valide")
        @Size(max = 100, message = "L'email ne doit pas dépasser 100 caractères")
        private String email;

        @NotBlank(message = "Le mot de passe est obligatoire")
        @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
        private String password;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String username;
        private String email;
        private String createdAt;
    }
}