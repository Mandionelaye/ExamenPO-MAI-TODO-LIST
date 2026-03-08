package com.mandione.todo_list.controller.api;

import com.mandione.todo_list.dto.TaskDto;
import com.mandione.todo_list.entity.Task.TaskStatus;
import com.mandione.todo_list.security.CustomUserDetails;
import com.mandione.todo_list.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour la gestion des tâches API.
 * Responsabilité unique : Exposition des endpoints REST JSON documentés.
 * Respect du principe SRP.
 */
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Tâches", description = "API de gestion des tâches (CRUD complet)")
public class TaskRestController {

    private final TaskService taskService;

    private Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Utilisateur non authentifié");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }

    @Operation(
            summary = "Lister toutes les tâches",
            description = "Récupère la liste des tâches de l'utilisateur connecté. Nécessite d'être authentifié."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste récupérée"),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    @SecurityRequirement(name = "basicAuth")
    @GetMapping
    public ResponseEntity<?> getAllTasks(Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            List<TaskDto.Response> tasks = taskService.findAllByUserId(userId);
            return ResponseEntity.ok(tasks);
        } catch (IllegalStateException e) {
            return unauthorizedResponse();
        }
    }

    @Operation(summary = "Récupérer une tâche par ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tâche trouvée"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "404", description = "Tâche non trouvée")
    })
    @SecurityRequirement(name = "basicAuth")
    @GetMapping("/{id}")
    public ResponseEntity<?> getTask(
            @Parameter(description = "ID de la tâche") @PathVariable Long id,
            Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            return ResponseEntity.ok(taskService.findById(id, userId));
        } catch (IllegalStateException e) {
            return unauthorizedResponse();
        }
    }

    @Operation(summary = "Créer une nouvelle tâche")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tâche créée"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    @SecurityRequirement(name = "basicAuth")
    @PostMapping
    public ResponseEntity<?> createTask(
            @Valid @RequestBody TaskDto.CreateRequest taskDto,
            Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            TaskDto.Response created = taskService.createTask(userId, taskDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalStateException e) {
            return unauthorizedResponse();
        }
    }

    @Operation(summary = "Modifier une tâche")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tâche mise à jour"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "404", description = "Tâche non trouvée")
    })
    @SecurityRequirement(name = "basicAuth")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskDto.UpdateRequest taskDto,
            Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            return ResponseEntity.ok(taskService.updateTask(id, userId, taskDto));
        } catch (IllegalStateException e) {
            return unauthorizedResponse();
        }
    }

    @Operation(summary = "Modifier le statut d'une tâche")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Statut mis à jour"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "404", description = "Tâche non trouvée")
    })
    @SecurityRequirement(name = "basicAuth")
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestBody TaskDto.StatusUpdateRequest statusDto,
            Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            return ResponseEntity.ok(taskService.updateStatus(id, userId, statusDto.getStatus()));
        } catch (IllegalStateException e) {
            return unauthorizedResponse();
        }
    }

    @Operation(summary = "Marquer une tâche comme terminée")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tâche terminée"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "404", description = "Tâche non trouvée")
    })
    @SecurityRequirement(name = "basicAuth")
    @PostMapping("/{id}/complete")
    public ResponseEntity<?> completeTask(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            return ResponseEntity.ok(taskService.markAsCompleted(id, userId));
        } catch (IllegalStateException e) {
            return unauthorizedResponse();
        }
    }

    @Operation(summary = "Supprimer une tâche")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tâche supprimée"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "404", description = "Tâche non trouvée")
    })
    @SecurityRequirement(name = "basicAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            taskService.deleteTask(id, userId);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return unauthorizedResponse();
        }
    }

    private ResponseEntity<Map<String, Object>> unauthorizedResponse() {
        Map<String, Object> error = new HashMap<>();
        error.put("code", 401);
        error.put("message", "Vous n'êtes pas connecté");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
}