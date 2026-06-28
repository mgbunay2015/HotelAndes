package com.uti.svcrooms.exception;

/**
 * Excepción lanzada cuando se viola una regla de negocio.
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
