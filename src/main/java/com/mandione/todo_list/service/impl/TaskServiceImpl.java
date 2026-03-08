package com.mandione.todo_list.service.impl;

import com.mandione.todo_list.dto.TaskDto;
import com.mandione.todo_list.entity.Task;
import com.mandione.todo_list.entity.Task.TaskStatus;
import com.mandione.todo_list.entity.User;
import com.mandione.todo_list.exception.ResourceNotFoundException;
import com.mandione.todo_list.exception.UnauthorizedAccessException;
import com.mandione.todo_list.mapper.TaskMapper;
import com.mandione.todo_list.repository.TaskRepository;
import com.mandione.todo_list.repository.UserRepository;
import com.mandione.todo_list.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implémentation du service tâche.
 * Respect du principe SRP : Logique métier des tâches uniquement.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    @Override
    public TaskDto.Response createTask(Long userId, TaskDto.CreateRequest taskDto) {
        log.info("Création d'une tâche pour l'utilisateur: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé: " + userId));

        Task task = taskMapper.toEntity(taskDto);
        task.setUser(user);

        Task savedTask = taskRepository.save(task);
        log.info("Tâche créée avec succès: ID={}", savedTask.getId());

        return taskMapper.toDto(savedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto.Response> findAllByUserId(Long userId) {
        log.debug("Récupération des tâches pour l'utilisateur: {}", userId);

        return taskRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TaskDto.Response findById(Long taskId, Long userId) {
        log.debug("Récupération de la tâche: {} pour l'utilisateur: {}", taskId, userId);

        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Tâche non trouvée: " + taskId));

        return taskMapper.toDto(task);
    }

    @Override
    public TaskDto.Response updateTask(Long taskId, Long userId, TaskDto.UpdateRequest taskDto) {
        log.info("Mise à jour de la tâche: {} pour l'utilisateur: {}", taskId, userId);

        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Tâche non trouvée: " + taskId));

        taskMapper.updateEntityFromDto(task, taskDto);
        Task updatedTask = taskRepository.save(task);

        log.info("Tâche mise à jour avec succès: ID={}", updatedTask.getId());
        return taskMapper.toDto(updatedTask);
    }

    @Override
    public TaskDto.Response updateStatus(Long taskId, Long userId, TaskStatus status) {
        log.info("Mise à jour du statut de la tâche: {} -> {}", taskId, status);

        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Tâche non trouvée: " + taskId));

        task.setStatus(status);
        Task updatedTask = taskRepository.save(task);

        return taskMapper.toDto(updatedTask);
    }

    @Override
    public void deleteTask(Long taskId, Long userId) {
        log.info("Suppression de la tâche: {} pour l'utilisateur: {}", taskId, userId);

        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Tâche non trouvée: " + taskId));

        taskRepository.delete(task);
        log.info("Tâche supprimée avec succès: ID={}", taskId);
    }

    @Override
    public TaskDto.Response markAsCompleted(Long taskId, Long userId) {
        log.info("Marquage de la tâche comme terminée: {}", taskId);

        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Tâche non trouvée: " + taskId));

        task.markAsCompleted();
        Task updatedTask = taskRepository.save(task);

        log.info("Tâche marquée comme terminée: ID={}", taskId);
        return taskMapper.toDto(updatedTask);
    }
}