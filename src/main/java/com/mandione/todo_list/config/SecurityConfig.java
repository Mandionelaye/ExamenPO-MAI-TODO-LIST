package com.mandione.todo_list.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mandione.todo_list.security.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/register", "/login", "/css/**", "/js/**", "/webjars/**", "/favicon.ico").permitAll()
                        .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                        .requestMatchers("/swagger-ui/**", "/api-docs/**", "/swagger-ui.html").permitAll()
                        .anyRequest().authenticated()
                )
                // IF_REQUIRED = session si cookie présent, sinon Basic Auth fonctionne aussi
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                // Activer BOTH form login ET http basic
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/tasks", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .httpBasic(httpBasic -> {}) // Activer Basic Auth pour API
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            if (isApiRequest(request)) {
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                                Map<String, Object> error = new HashMap<>();
                                error.put("code", 401);
                                error.put("message", "Vous n'êtes pas connecté");
                                error.put("path", request.getRequestURI());

                                new ObjectMapper().writeValue(response.getOutputStream(), error);
                            } else {
                                response.sendRedirect("/login");
                            }
                        })
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**")
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(authenticationProvider());
    }

    private boolean isApiRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String acceptHeader = request.getHeader("Accept");

        if (uri != null && uri.startsWith("/api")) {
            return true;
        }
        if (acceptHeader != null && acceptHeader.contains("application/json")) {
            return true;
        }
        String contentType = request.getContentType();
        if (contentType != null && contentType.contains("application/json")) {
            return true;
        }
        return false;
    }
}