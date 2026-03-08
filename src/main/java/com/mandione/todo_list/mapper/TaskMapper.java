package com.mandione.todo_list.mapper;

import com.mandione.todo_list.dto.TaskDto;
import com.mandione.todo_list.entity.Task;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

/**
 * Mapper pour l'entité Task.
 * Respect du principe SRP : Transformation des données uniquement.
 */
@Component
public class TaskMapper implements EntityMapper<Task, TaskDto.Response> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public TaskDto.Response toDto(Task task) {
        if (task == null) {
            return null;
        }

        return TaskDto.Response.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt() != null ? task.getCreatedAt().format(FORMATTER) : null)
                .updatedAt(task.getUpdatedAt() != null ? task.getUpdatedAt().format(FORMATTER) : null)
                .userId(task.getUser() != null ? task.getUser().getId() : null)
                .build();
    }

    @Override
    public Task toEntity(TaskDto.Response dto) {
        throw new UnsupportedOperationException("Conversion Response DTO -> Entity non supportée");
    }

    /**
     * Convertit un DTO de création en Entité
     */
    public Task toEntity(TaskDto.CreateRequest request) {
        if (request == null) {
            return null;
        }

        return Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .dueDate(request.getDueDate())
                .build();
    }

    /**
     * Met à jour une entité existante avec les données du DTO
     */
    public void updateEntityFromDto(Task task, TaskDto.UpdateRequest request) {
        if (request == null || task == null) {
            return;
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setDueDate(request.getDueDate());
    }
}