package com.mandione.todo_list.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * UserDetails personnalisé contenant l'ID de l'utilisateur.
 * Nécessaire pour lier les tâches à l'utilisateur connecté.
 */
@Getter
public class CustomUserDetails extends User {

    private final Long id;

    public CustomUserDetails(Long id, String username, String password,
                             Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
    }
}