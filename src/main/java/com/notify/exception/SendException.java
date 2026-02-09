package com.notify.exception;

/**
 * Se lanza cuando el proveedor falla al intentar enviar la notificación.
 */
public class SendException extends NotificationException {

    public SendException(String message) {
        super(message);
    }

    public SendException(String message, Throwable cause) {
        super(message, cause);
    }
}
