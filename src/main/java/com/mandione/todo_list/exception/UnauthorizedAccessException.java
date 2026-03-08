package com.mandione.todo_list.exception;

/**
 * Exception levée en cas d'accès non autorisé.
 */
public class UnauthorizedAccessException extends RuntimeException {

    public UnauthorizedAccessException(String message) {
        super(message);
    }
}