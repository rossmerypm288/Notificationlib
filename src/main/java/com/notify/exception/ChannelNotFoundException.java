package com.notify.exception;

/**
 * Se lanza cuando se intenta enviar por un canal que no fue registrado.
 */
public class ChannelNotFoundException extends NotificationException {

    public ChannelNotFoundException(String message) {
        super(message);
    }
}
