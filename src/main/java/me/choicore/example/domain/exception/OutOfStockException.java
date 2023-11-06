package me.choicore.example.domain.exception;

public class OutOfStockException extends RuntimeException {

    public OutOfStockException() {
        super();
    }

    public OutOfStockException(String message) {
        super(message);
    }

    public OutOfStockException(String message, Throwable cause) {
        super(message, cause);
    }

    public OutOfStockException(final Throwable cause) {
        super(cause);
    }
}
