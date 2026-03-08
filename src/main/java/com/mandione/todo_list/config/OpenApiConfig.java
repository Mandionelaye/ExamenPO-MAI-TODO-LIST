package com.mandione.todo_list.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "basicAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("API Gestion de Tâches")
                        .version("1.0.0")
                        .description("API REST pour la gestion de tâches. " +
                                "Cliquez sur 'Authorize' et entrez vos credentials pour tester les endpoints protégés.")
                        .contact(new Contact()
                                .name("Projet Master")
                                .email("mandionelay99@gmail.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                // Ajouter le schéma de sécurité globalement
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("basic")
                                        .description("Basic authentication")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Serveur local")
                ));
    }
}