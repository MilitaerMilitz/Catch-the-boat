package com.github.militaermilitz.exception;

/**
 * @author Alexander Ley
 * @version 1.0
 * This Exception is thrown by trying to instantiate a chestGui at a location where no container is located.
 */
public class NoContainerException extends Exception{

    public NoContainerException() {
    }

    public NoContainerException(String message) {
        super(message);
    }

    public NoContainerException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoContainerException(Throwable cause) {
        super(cause);
    }

    public NoContainerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
