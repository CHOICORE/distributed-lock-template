package me.choicore.common.distributedlock.exception;


public class DistributedLockException extends RuntimeException {

    public DistributedLockException() {
    }

    public DistributedLockException(final String message) {
        super(message);
    }

    public DistributedLockException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public DistributedLockException(final Throwable cause) {
        super(cause);
    }

    public DistributedLockException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
