package com.mandione.todo_list.exception;

/**
 * Exception levée quand une ressource existe déjà.
 */
public class ResourceAlreadyExistsException extends RuntimeException {

    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
}