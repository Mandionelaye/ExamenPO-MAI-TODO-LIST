package com.mandione.todo_list.exception;

/**
 * Exception levée quand une ressource n'est pas trouvée.
 * Respect du principe OCP : Extension du comportement d'exception standard.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
