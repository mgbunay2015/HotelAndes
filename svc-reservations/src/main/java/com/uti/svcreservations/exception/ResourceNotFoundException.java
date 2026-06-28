package com.uti.svcreservations.exception;

/**
 * Excepción lanzada cuando un recurso no existe.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
