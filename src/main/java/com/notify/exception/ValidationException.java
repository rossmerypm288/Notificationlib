package com.notify.exception;

/**
 * Se lanza cuando la notificación tiene datos inválidos.
 */
public class ValidationException extends NotificationException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
