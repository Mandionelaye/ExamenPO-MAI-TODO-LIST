package com.mandione.todo_list.controller.web;

import com.mandione.todo_list.dto.UserDto;
import com.mandione.todo_list.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Contrôleur Web pour l'authentification (Thymeleaf).
 * Responsabilité unique : Gestion des vues HTML (login, register).
 * Respect du principe SRP.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthWebController {

    private final UserService userService;

    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("userDto", new UserDto.Request());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("userDto") UserDto.Request userDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            userService.createUser(userDto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Compte créé avec succès ! Vous pouvez maintenant vous connecter.");
            log.info("Inscription web réussie: {}", userDto.getUsername());
            return "redirect:/login";
        } catch (Exception e) {
            log.error("Échec inscription web: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request,
                         HttpServletResponse response,
                         Authentication authentication,
                         RedirectAttributes redirectAttributes) {

        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            log.info("Déconnexion web réussie pour: {}", authentication.getName());
        }

        redirectAttributes.addFlashAttribute("logoutMessage", "Vous avez été déconnecté avec succès.");
        return "redirect:/login";
    }
}
