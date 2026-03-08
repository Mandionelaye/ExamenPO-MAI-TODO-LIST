package com.mandione.todo_list.controller.api;

import com.mandione.todo_list.dto.LoginRequest;
import com.mandione.todo_list.dto.UserDto;
import com.mandione.todo_list.security.CustomUserDetails;
import com.mandione.todo_list.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur REST pour l'authentification API.
 * Responsabilité unique : Exposition des endpoints REST JSON documentés.
 * Respect du principe SRP.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentification", description = "API d'inscription et de gestion des utilisateurs")
public class AuthRestController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @Operation(
            summary = "Inscription utilisateur",
            description = "Crée un nouveau compte utilisateur avec validation des données d'entrée"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Utilisateur créé avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.Response.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "409", description = "Utilisateur existe déjà")
    })
    @PostMapping("/register")
    public ResponseEntity<UserDto.Response> register(@Valid @RequestBody UserDto.Request request) {
        log.info("API Inscription: {}", request.getUsername());
        UserDto.Response created = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Connexion (Login)",
            description = "Authentifie un utilisateur et crée une session HTTP. " +
                    "Pour Swagger: utilisez 'Authorize' avec Basic Auth OU envoyez username/password dans le body."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Connexion réussie"),
            @ApiResponse(responseCode = "401", description = "Identifiants incorrects")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {

        log.info("Tentative de connexion API pour: {}", loginRequest.getUsername());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            HttpSession session = request.getSession(true);

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            Map<String, Object> result = new HashMap<>();
            result.put("message", "Connexion réussie");
            result.put("username", userDetails.getUsername());
            result.put("userId", userDetails.getId());
            result.put("sessionId", session.getId());

            // Définir le cookie de session explicitement pour Swagger
            response.setHeader("Set-Cookie", "JSESSIONID=" + session.getId() + "; Path=/; HttpOnly; SameSite=Lax");

            log.info("Connexion API réussie pour: {} (ID: {})", userDetails.getUsername(), userDetails.getId());
            return ResponseEntity.ok(result);

        } catch (BadCredentialsException e) {
            log.warn("Échec de connexion API pour: {}", loginRequest.getUsername());

            Map<String, Object> error = new HashMap<>();
            error.put("code", 401);
            error.put("message", "Nom d'utilisateur ou mot de passe incorrect");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @Operation(
            summary = "Déconnexion (Logout)",
            description = "Déconnecte l'utilisateur et invalide la session."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Déconnexion réussie"),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    @SecurityRequirement(name = "basicAuth")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(Authentication authentication,
                                    HttpServletRequest request,
                                    HttpServletResponse response) {

        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            log.info("Déconnexion API réussie pour: {}", authentication.getName());
        }

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        Map<String, String> result = new HashMap<>();
        result.put("message", "Déconnexion réussie");

        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Profil utilisateur connecté",
            description = "Récupère les informations de l'utilisateur actuellement connecté"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profil récupéré"),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    @SecurityRequirement(name = "basicAuth")
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 401);
            error.put("message", "Vous n'êtes pas connecté");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Map<String, Object> profile = new HashMap<>();
        profile.put("id", userDetails.getId());
        profile.put("username", userDetails.getUsername());
        profile.put("authorities", userDetails.getAuthorities());

        return ResponseEntity.ok(profile);
    }

    @Operation(summary = "Vérifier disponibilité username")
    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Object>> checkUsername(
            @Parameter(description = "Nom d'utilisateur à vérifier") @RequestParam String username) {
        boolean exists = userService.existsByUsername(username);
        Map<String, Object> response = new HashMap<>();
        response.put("username", username);
        response.put("available", !exists);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Vérifier disponibilité email")
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Object>> checkEmail(
            @Parameter(description = "Email à vérifier") @RequestParam String email) {
        boolean exists = userService.existsByEmail(email);
        Map<String, Object> response = new HashMap<>();
        response.put("email", email);
        response.put("available", !exists);
        return ResponseEntity.ok(response);
    }
}