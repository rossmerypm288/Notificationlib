package com.notify.validation;

import com.notify.channel.email.EmailNotification;
import com.notify.channel.push.PushNotification;
import com.notify.channel.sms.SmsNotification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests del NotificationValidator.
 *
 * Verifica reglas de validación por canal:
 * - Email: formato RFC 5322, subject requerido
 * - SMS: formato E.164, máximo 160 caracteres
 * - Push: device token mínimo, título requerido
 */
class NotificationValidatorTest {

    private NotificationValidator validator;

    @BeforeEach
    void setUp() {
        validator = new NotificationValidator();
    }

    // VALIDACIONES DE EMAIL
    @Nested
    @DisplayName("Validación de Email")
    class EmailValidation {

        @Test
        @DisplayName("Email válido pasa todas las validaciones")
        void validEmailShouldPass() {
            EmailNotification email = EmailNotification.builder()
                    .to("user@example.com")
                    .subject("Test")
                    .message("Hello")
                    .build();

            ValidationResult result = validator.validate(email);

            assertTrue(result.isValid());
        }

        @Test
        @DisplayName("Email sin @ es inválido")
        void emailWithoutAtShouldFail() {
            EmailNotification email = EmailNotification.builder()
                    .to("invalid-email")
                    .subject("Test")
                    .message("Hello")
                    .build();

            ValidationResult result = validator.validate(email);

            assertFalse(result.isValid());
            assertTrue(result.getErrors().stream()
                    .anyMatch(e -> e.contains("email inválido")));
        }

        @Test
        @DisplayName("Email sin subject es inválido")
        void emailWithoutSubjectShouldFail() {
            EmailNotification email = EmailNotification.builder()
                    .to("user@example.com")
                    .subject("")
                    .message("Hello")
                    .build();

            ValidationResult result = validator.validate(email);

            assertFalse(result.isValid());
            assertTrue(result.getErrors().stream()
                    .anyMatch(e -> e.contains("asunto")));
        }
    }

    // VALIDACIONES DE SMS
    @Nested
    @DisplayName("Validación de SMS")
    class SmsValidation {

        @Test
        @DisplayName("SMS con teléfono E.164 y mensaje corto es válido")
        void validSmsShouldPass() {
            SmsNotification sms = SmsNotification.builder()
                    .to("+51999888777")
                    .message("Hola, tu código es 1234")
                    .build();

            ValidationResult result = validator.validate(sms);

            assertTrue(result.isValid());
        }

        @Test
        @DisplayName("SMS sin código de país es inválido")
        void smsWithoutCountryCodeShouldFail() {
            SmsNotification sms = SmsNotification.builder()
                    .to("999888777")  // Falta el +51
                    .message("Test")
                    .build();

            ValidationResult result = validator.validate(sms);

            assertFalse(result.isValid());
            assertTrue(result.getErrors().stream()
                    .anyMatch(e -> e.contains("E.164")));
        }

        @Test
        @DisplayName("SMS mayor a 160 caracteres es inválido")
        void smsExceeding160CharsShouldFail() {
            String longMessage = "A".repeat(161);
            SmsNotification sms = SmsNotification.builder()
                    .to("+51999888777")
                    .message(longMessage)
                    .build();

            ValidationResult result = validator.validate(sms);

            assertFalse(result.isValid());
            assertTrue(result.getErrors().stream()
                    .anyMatch(e -> e.contains("160")));
        }
    }

    // VALIDACIONES DE PUSH
    @Nested
    @DisplayName("Validación de Push")
    class PushValidation {

        @Test
        @DisplayName("Push con token largo y título es válido")
        void validPushShouldPass() {
            PushNotification push = PushNotification.builder()
                    .deviceToken("fcm-long-device-token-1234567890abcdef")
                    .title("Nuevo mensaje")
                    .message("Tienes un nuevo mensaje")
                    .build();

            ValidationResult result = validator.validate(push);

            assertTrue(result.isValid());
        }

        @Test
        @DisplayName("Push sin título es inválido")
        void pushWithoutTitleShouldFail() {
            PushNotification push = PushNotification.builder()
                    .deviceToken("fcm-long-device-token-1234567890")
                    .title("")
                    .message("Body")
                    .build();

            ValidationResult result = validator.validate(push);

            assertFalse(result.isValid());
            assertTrue(result.getErrors().stream()
                    .anyMatch(e -> e.contains("título")));
        }
    }

    // VALIDACIONES COMUNES
    @Nested
    @DisplayName("Validaciones comunes")
    class CommonValidation {

        @Test
        @DisplayName("Notificación sin destinatario es inválida")
        void notificationWithoutRecipientShouldFail() {
            EmailNotification email = EmailNotification.builder()
                    .to("")
                    .subject("Test")
                    .message("Hello")
                    .build();

            ValidationResult result = validator.validate(email);

            assertFalse(result.isValid());
            assertTrue(result.getErrors().stream()
                    .anyMatch(e -> e.contains("destinatario")));
        }

        @Test
        @DisplayName("Notificación sin mensaje es inválida")
        void notificationWithoutMessageShouldFail() {
            EmailNotification email = EmailNotification.builder()
                    .to("user@example.com")
                    .subject("Test")
                    .message("")
                    .build();

            ValidationResult result = validator.validate(email);

            assertFalse(result.isValid());
            assertTrue(result.getErrors().stream()
                    .anyMatch(e -> e.contains("mensaje")));
        }
    }
}
