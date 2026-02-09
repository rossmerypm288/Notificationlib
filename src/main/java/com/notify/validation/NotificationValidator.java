package com.notify.validation;

import com.notify.channel.email.EmailNotification;
import com.notify.channel.push.PushNotification;
import com.notify.channel.sms.SmsNotification;
import com.notify.core.Notification;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Validador central de notificaciones.
 */
public class NotificationValidator {

    // Regex simplificado para email
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // Regex para teléfono internacional [código país][número]
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+[1-9]\\d{6,14}$");

    /**
     * Valida una notificación aplicando reglas comunes y específicas.
     *
     * @param notification Notificación a validar
     * @return ValidationResult con lista de errores (vacía si es válida)
     */
    public ValidationResult validate(Notification notification) {
        List<String> errors = new ArrayList<>();

        //Validaciones comunes a todos los canales
        validateCommon(notification, errors);

        //Validaciones específicas por tipo de canal
        if (errors.isEmpty()) {
            validateSpecific(notification, errors);
        }

        return errors.isEmpty() ? ValidationResult.valid() : ValidationResult.invalid(errors);
    }

    /**
     * aplican a CUALQUIER tipo de notificación.
     */
    private void validateCommon(Notification notification, List<String> errors) {
        if (notification == null) {
            errors.add("La notificación no puede ser nula");
            return;
        }

        if (notification.getRecipient() == null || notification.getRecipient().isBlank()) {
            errors.add("El destinatario es obligatorio");
        }

        if (notification.getMessage() == null || notification.getMessage().isBlank()) {
            errors.add("El mensaje es obligatorio");
        }
    }

    /**
     * se aplican según el tipo concreto de notificación.
     * Usa pattern matching.
     */
    private void validateSpecific(Notification notification, List<String> errors) {
        // Pattern matching con instanceof — feature de Java 16+
        if (notification instanceof EmailNotification email) {
            validateEmail(email, errors);
        } else if (notification instanceof SmsNotification sms) {
            validateSms(sms, errors);
        } else if (notification instanceof PushNotification push) {
            validatePush(push, errors);
        }
    }

    /**
     * Validación de Email:
     * - Formato de correo válido (RFC 5322 simplificado)
     * - Subject obligatorio
     */
    private void validateEmail(EmailNotification email, List<String> errors) {
        if (!EMAIL_PATTERN.matcher(email.getRecipient()).matches()) {
            errors.add("Formato de email inválido: " + email.getRecipient());
        }

        if (email.getSubject() == null || email.getSubject().isBlank()) {
            errors.add("El asunto (subject) es obligatorio para Email");
        }
    }

    /**
     * Validación de SMS:
     * - Formato E.164 internacional (como requiere Twilio)
     * - Longitud máxima de 160 caracteres (estándar GSM)
     */
    private void validateSms(SmsNotification sms, List<String> errors) {
        if (!PHONE_PATTERN.matcher(sms.getRecipient()).matches()) {
            errors.add("Formato de teléfono inválido (se espera E.164, ej: +51999888777): "
                    + sms.getRecipient());
        }

        if (sms.getMessage().length() > 160) {
            errors.add("SMS excede 160 caracteres (tiene " + sms.getMessage().length() + ")");
        }
    }

    /**
     * Validación de Push Notification:
     * - Device token no vacío (requerido por Firebase FCM y OneSignal)
     * - Título obligatorio (Firebase lo requiere para notificaciones visibles)
     */
    private void validatePush(PushNotification push, List<String> errors) {
        if (push.getRecipient() == null || push.getRecipient().length() < 10) {
            errors.add("Device token inválido o demasiado corto");
        }

        if (push.getTitle() == null || push.getTitle().isBlank()) {
            errors.add("El título es obligatorio para Push Notification");
        }
    }
}
