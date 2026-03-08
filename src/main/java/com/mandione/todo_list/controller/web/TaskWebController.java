package com.mandione.todo_list.controller.web;

import com.mandione.todo_list.dto.TaskDto;
import com.mandione.todo_list.entity.Task.TaskStatus;
import com.mandione.todo_list.security.CustomUserDetails;
import com.mandione.todo_list.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Contrôleur pour l'interface web de gestion des tâches (Thymeleaf).
 * Respect du principe SRP : Gestion des vues uniquement.
 */

@Controller
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Slf4j
public class TaskWebController {

    private final TaskService taskService;

    /**
     * Récupère l'ID réel de l'utilisateur connecté depuis CustomUserDetails.
     */
    private Long getCurrentUserId(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }

    @GetMapping
    public String listTasks(Model model, Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        List<TaskDto.Response> tasks = taskService.findAllByUserId(userId);
        model.addAttribute("tasks", tasks);
        log.info("Liste des tâches chargée pour l'utilisateur ID: {}", userId);
        return "tasks/list";
    }

    @GetMapping("/new")
    public String newTaskForm(Model model) {
        model.addAttribute("taskDto", new TaskDto.CreateRequest());
        model.addAttribute("statuses", TaskStatus.values());
        return "tasks/form";
    }

    @PostMapping
    public String createTask(
            @Valid @ModelAttribute("taskDto") TaskDto.CreateRequest taskDto,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("statuses", TaskStatus.values());
            return "tasks/form";
        }

        Long userId = getCurrentUserId(authentication);
        log.info("Création d'une tâche pour l'utilisateur ID: {}", userId);

        taskService.createTask(userId, taskDto);
        redirectAttributes.addFlashAttribute("successMessage", "Tâche créée avec succès !");
        return "redirect:/tasks";
    }

    @GetMapping("/{id}/edit")
    public String editTaskForm(@PathVariable Long id, Model model, Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        model.addAttribute("task", taskService.findById(id, userId));
        model.addAttribute("statuses", TaskStatus.values());
        return "tasks/edit";
    }

    @PostMapping("/{id}")
    public String updateTask(
            @PathVariable Long id,
            @Valid @ModelAttribute("taskDto") TaskDto.UpdateRequest taskDto,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("statuses", TaskStatus.values());
            return "tasks/edit";
        }

        Long userId = getCurrentUserId(authentication);
        taskService.updateTask(id, userId, taskDto);
        redirectAttributes.addFlashAttribute("successMessage", "Tâche mise à jour avec succès !");
        return "redirect:/tasks";
    }

    @PostMapping("/{id}/complete")
    public String completeTask(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Long userId = getCurrentUserId(authentication);
        taskService.markAsCompleted(id, userId);
        redirectAttributes.addFlashAttribute("successMessage", "Tâche marquée comme terminée !");
        return "redirect:/tasks";
    }

    @PostMapping("/{id}/delete")
    public String deleteTask(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Long userId = getCurrentUserId(authentication);
        taskService.deleteTask(id, userId);
        redirectAttributes.addFlashAttribute("successMessage", "Tâche supprimée avec succès !");
        return "redirect:/tasks";
    }
}