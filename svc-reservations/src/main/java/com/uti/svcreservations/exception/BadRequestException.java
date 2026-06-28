package com.uti.svcreservations.exception;

/**
 * Excepción lanzada cuando los datos de entrada son inválidos.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
