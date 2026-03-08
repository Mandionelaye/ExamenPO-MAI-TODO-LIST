package com.mandione.todo_list.service;

import com.mandione.todo_list.dto.TaskDto;
import com.mandione.todo_list.entity.Task.TaskStatus;

import java.util.List;

/**
 * Interface du service tâche.
 * Respect du principe ISP : Interface spécifique aux opérations sur les tâches.
 */
public interface TaskService {

    /**
     * Crée une nouvelle tâche pour un utilisateur
     */
    TaskDto.Response createTask(Long userId, TaskDto.CreateRequest taskDto);

    /**
     * Récupère toutes les tâches d'un utilisateur
     */
    List<TaskDto.Response> findAllByUserId(Long userId);

    /**
     * Récupère une tâche par son ID (avec vérification propriétaire)
     */
    TaskDto.Response findById(Long taskId, Long userId);

    /**
     * Met à jour une tâche existante
     */
    TaskDto.Response updateTask(Long taskId, Long userId, TaskDto.UpdateRequest taskDto);

    /**
     * Met à jour le statut d'une tâche
     */
    TaskDto.Response updateStatus(Long taskId, Long userId, TaskStatus status);

    /**
     * Supprime une tâche
     */
    void deleteTask(Long taskId, Long userId);

    /**
     * Marque une tâche comme terminée
     */
    TaskDto.Response markAsCompleted(Long taskId, Long userId);
}