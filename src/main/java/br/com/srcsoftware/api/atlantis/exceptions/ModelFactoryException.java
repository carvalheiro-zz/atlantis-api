package br.com.srcsoftware.api.atlantis.exceptions;

/**
 * Representa erros ocorridos durante a conversão de modelos no ModelFactory.
 */
public final class ModelFactoryException extends Exception {

    private static final long serialVersionUID = 1L;

    public ModelFactoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModelFactoryException(String message) {
        super(message);
    }

    public ModelFactoryException(Throwable cause) {
        super(cause);
    }
}