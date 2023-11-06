package me.choicore.example.domain.exception;

public class OrderProcessingException extends RuntimeException {

    public OrderProcessingException() {
        super();
    }

    public OrderProcessingException(String message) {
        super(message);
    }

    public OrderProcessingException(final Throwable cause) {
        super(cause);
    }

    public OrderProcessingException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
