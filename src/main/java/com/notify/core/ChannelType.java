package com.notify.core;

/**
 * Tipos de canales de notificación soportados por la librería.
 */
public enum ChannelType {

    EMAIL,              // Correo electrónico (SendGrid, Mailgun, SES)
    SMS,                // Mensaje de texto (Twilio, Amazon SNS)
    PUSH_NOTIFICATION   // Notificación móvil (Firebase FCM, OneSignal)
}
