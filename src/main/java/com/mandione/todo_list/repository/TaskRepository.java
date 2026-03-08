package com.mandione.todo_list.repository;

import com.mandione.todo_list.entity.Task;
import com.mandione.todo_list.entity.Task.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Task.
 * Respect du principe ISP : Interface spécifique aux besoins des tâches.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Récupère toutes les tâches d'un utilisateur spécifique
     */
    List<Task> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Récupère une tâche par son ID et l'ID de l'utilisateur (sécurité)
     */
    Optional<Task> findByIdAndUserId(Long id, Long userId);

    /**
     * Compte les tâches par statut pour un utilisateur
     */
    long countByUserIdAndStatus(Long userId, TaskStatus status);

    /**
     * Vérifie si une tâche appartient à un utilisateur
     */
    boolean existsByIdAndUserId(Long id, Long userId);
}
