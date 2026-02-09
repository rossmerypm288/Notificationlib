package com.notify.core;

import com.notify.channel.email.EmailNotification;
import com.notify.channel.sms.SmsNotification;
import com.notify.exception.ChannelNotFoundException;
import com.notify.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests del NotificationService — orquestador principal de la librería.
 */
@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationChannel<EmailNotification> emailChannel;

    @Mock
    private NotificationChannel<SmsNotification> smsChannel;

    private NotificationService service;

    @BeforeEach
    void setUp() {
        // Configurar mocks para que retornen el ChannelType correcto
        lenient().when(emailChannel.getChannelType()).thenReturn(ChannelType.EMAIL);
        lenient().when(smsChannel.getChannelType()).thenReturn(ChannelType.SMS);
        lenient().when(emailChannel.isAvailable()).thenReturn(true);
        lenient().when(smsChannel.isAvailable()).thenReturn(true);

        service = NotificationService.builder()
                .channel(emailChannel)
                .channel(smsChannel)
                .build();
    }

    //Helpers para crear notificaciones válidas

    private EmailNotification createValidEmail() {
        return EmailNotification.builder()
                .to("test@example.com")
                .subject("Test Subject")
                .message("Test message body")
                .from("noreply@app.com")
                .build();
    }

    private SmsNotification createValidSms() {
        return SmsNotification.builder()
                .to("+51999888777")
                .message("Test SMS")
                .from("+15551234567")
                .build();
    }

    // TESTS DE ENVÍO SÍNCRONO
    @Nested
    @DisplayName("Envío síncrono")
    class SyncSendTests {

        @Test
        @DisplayName("Envía email exitosamente cuando el canal retorna success")
        void shouldSendEmailSuccessfully() {
           //configurar mock para retornar éxito
            EmailNotification email = createValidEmail();
            NotificationResult expectedResult = NotificationResult.success(email.getId(), "sg-123");
            when(emailChannel.send(any())).thenReturn(expectedResult);

            NotificationResult result = service.send(email);

            assertTrue(result.isSuccess());
            assertEquals("sg-123", result.getProviderMessageId());
            verify(emailChannel, times(1)).send(any());
        }

        @Test
        @DisplayName("Envía SMS exitosamente enrutando al canal correcto")
        void shouldSendSmsSuccessfully() {
            SmsNotification sms = createValidSms();
            NotificationResult expectedResult = NotificationResult.success(sms.getId(), "SM-456");
            when(smsChannel.send(any())).thenReturn(expectedResult);

            NotificationResult result = service.send(sms);

            assertTrue(result.isSuccess());
            assertEquals("SM-456", result.getProviderMessageId());
            verify(smsChannel, times(1)).send(any());
            // Verificar que NO se llamó al canal de email (enrutamiento correcto)
            verify(emailChannel, never()).send(any());
        }

        @Test
        @DisplayName("Retorna failure cuando el canal lanza excepción inesperada")
        void shouldReturnFailureOnChannelException() {
            EmailNotification email = createValidEmail();
            when(emailChannel.send(any())).thenThrow(new RuntimeException("Connection timeout"));

            NotificationResult result = service.send(email);

            assertFalse(result.isSuccess());
            assertTrue(result.getErrorMessage().contains("Connection timeout"));
        }
    }

    // TESTS DE VALIDACIÓN
    @Nested
    @DisplayName("Validación de notificaciones")
    class ValidationTests {

        @Test
        @DisplayName("Lanza ValidationException con email inválido")
        void shouldThrowValidationExceptionForInvalidEmail() {
            EmailNotification invalidEmail = EmailNotification.builder()
                    .to("not-an-email")  // Formato inválido
                    .subject("Test")
                    .message("Body")
                    .build();

            assertThrows(ValidationException.class, () -> service.send(invalidEmail));
            // Verificar que NUNCA se intentó enviar
            verify(emailChannel, never()).send(any());
        }

        @Test
        @DisplayName("Lanza ValidationException cuando el mensaje está vacío")
        void shouldThrowValidationExceptionForEmptyMessage() {
            EmailNotification emptyMessage = EmailNotification.builder()
                    .to("valid@email.com")
                    .subject("Test")
                    .message("")  // Mensaje vacío
                    .build();

            assertThrows(ValidationException.class, () -> service.send(emptyMessage));
        }

        @Test
        @DisplayName("Lanza ValidationException para teléfono SMS inválido")
        void shouldThrowValidationExceptionForInvalidPhone() {
            SmsNotification invalidSms = SmsNotification.builder()
                    .to("12345")  // Sin formato E.164
                    .message("Test")
                    .build();

            assertThrows(ValidationException.class, () -> service.send(invalidSms));
        }
    }

    // TESTS DE CANAL NO REGISTRADO
    @Nested
    @DisplayName("Canal no encontrado")
    class ChannelNotFoundTests {

        @Test
        @DisplayName("Lanza ChannelNotFoundException para canal Push no registrado")
        void shouldThrowWhenChannelNotRegistered() {
            // El servicio solo tiene Email y SMS, no Push
            com.notify.channel.push.PushNotification push = com.notify.channel.push.PushNotification.builder()
                    .deviceToken("fcm-token-123456789012345")
                    .title("Test")
                    .message("Body")
                    .build();

            assertThrows(ChannelNotFoundException.class, () -> service.send(push));
        }
    }

    // TESTS DE ENVÍO ASÍNCRONO
    @Nested
    @DisplayName("Envío asíncrono")
    class AsyncSendTests {

        @Test
        @DisplayName("sendAsync retorna CompletableFuture con resultado exitoso")
        void shouldSendAsyncSuccessfully() throws Exception {
            EmailNotification email = createValidEmail();
            NotificationResult expected = NotificationResult.success(email.getId(), "sg-async-1");
            when(emailChannel.sendAsync(any())).thenReturn(
                    CompletableFuture.completedFuture(expected)
            );

            CompletableFuture<NotificationResult> future = service.sendAsync(email);
            NotificationResult result = future.get(); // Espera resultado

            assertTrue(result.isSuccess());
        }

        @Test
        @DisplayName("sendAsync retorna failure en CompletableFuture si validación falla")
        void shouldReturnFailureFutureOnValidationError() throws Exception {
            EmailNotification invalid = EmailNotification.builder()
                    .to("bad-email")
                    .subject("Test")
                    .message("Body")
                    .build();

            CompletableFuture<NotificationResult> future = service.sendAsync(invalid);
            NotificationResult result = future.get();

            assertFalse(result.isSuccess());
        }
    }
    // TESTS DE ENVÍO EN LOTE
    @Nested
    @DisplayName("Envío en lote")
    class BatchSendTests {

        @Test
        @DisplayName("sendBatch envía múltiples notificaciones y retorna todos los resultados")
        void shouldSendBatchSuccessfully() throws Exception {
            EmailNotification email = createValidEmail();
            SmsNotification sms = createValidSms();

            when(emailChannel.sendAsync(any())).thenReturn(
                    CompletableFuture.completedFuture(NotificationResult.success(email.getId(), "sg-1"))
            );
            when(smsChannel.sendAsync(any())).thenReturn(
                    CompletableFuture.completedFuture(NotificationResult.success(sms.getId(), "SM-1"))
            );

            CompletableFuture<List<NotificationResult>> batchFuture =
                    service.sendBatch(List.of(email, sms));

            List<NotificationResult> results = batchFuture.get();

            assertEquals(2, results.size());
            assertTrue(results.stream().allMatch(NotificationResult::isSuccess));
        }
    }

    // TESTS DEL BUILDER
    @Nested
    @DisplayName("Builder del servicio")
    class BuilderTests {

        @Test
        @DisplayName("Lanza excepción si se construye sin canales")
        void shouldThrowWhenBuildingWithNoChannels() {
            assertThrows(IllegalStateException.class, () ->
                    NotificationService.builder().build()
            );
        }

        @Test
        @DisplayName("isChannelAvailable retorna true para canales registrados")
        void shouldReportAvailableChannels() {
            assertTrue(service.isChannelAvailable(ChannelType.EMAIL));
            assertTrue(service.isChannelAvailable(ChannelType.SMS));
            assertFalse(service.isChannelAvailable(ChannelType.PUSH_NOTIFICATION));
        }
    }
}
