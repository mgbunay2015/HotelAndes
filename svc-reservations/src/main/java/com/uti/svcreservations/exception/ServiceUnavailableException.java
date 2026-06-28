package com.uti.svcreservations.exception;

/**
 * Excepción lanzada cuando svc-rooms no está disponible (Circuit Breaker abierto).
 */
public class ServiceUnavailableException extends RuntimeException {

    public ServiceUnavailableException(String message) {
        super(message);
    }
}
