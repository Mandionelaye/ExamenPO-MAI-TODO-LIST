package com.mandione.todo_list.dto;

import com.mandione.todo_list.entity.Task.TaskStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

/**
 * DTO pour la gestion des tâches.
 * Respect du principe ISP : Interfaces spécifiques pour chaque opération.
 */
public class TaskDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {

        @NotBlank(message = "Le titre est obligatoire")
        @Size(max = 100, message = "Le titre ne doit pas dépasser 100 caractères")
        private String title;

        @Size(max = 500, message = "La description ne doit pas dépasser 500 caractères")
        private String description;

        @NotNull(message = "Le statut est obligatoire")
        private TaskStatus status;

        @FutureOrPresent(message = "La date d'échéance doit être présente ou future")
        private LocalDate dueDate;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {

        @NotBlank(message = "Le titre est obligatoire")
        @Size(max = 100, message = "Le titre ne doit pas dépasser 100 caractères")
        private String title;

        @Size(max = 500, message = "La description ne doit pas dépasser 500 caractères")
        private String description;

        @NotNull(message = "Le statut est obligatoire")
        private TaskStatus status;

        @FutureOrPresent(message = "La date d'échéance doit être présente ou future")
        private LocalDate dueDate;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String title;
        private String description;
        private TaskStatus status;
        private LocalDate dueDate;
        private String createdAt;
        private String updatedAt;
        private Long userId;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusUpdateRequest {
        @NotNull(message = "Le statut est obligatoire")
        private TaskStatus status;
    }
}