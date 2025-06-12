package com.vmtecnologia.vm_teste_tecnico.exception;

/**
 * Exceção para erros de negócio que devem ser mostrados ao usuário final
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }
}