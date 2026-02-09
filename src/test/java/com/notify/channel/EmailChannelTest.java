package com.notify.channel;

import com.notify.channel.email.EmailChannel;
import com.notify.channel.email.EmailNotification;
import com.notify.channel.email.provider.EmailProvider;
import com.notify.core.ChannelType;
import com.notify.core.NotificationResult;
import com.notify.exception.SendException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests del EmailChannel — verifica delegación correcta al provider.
 */
@ExtendWith(MockitoExtension.class)
class EmailChannelTest {

    @Mock
    private EmailProvider emailProvider;

    private EmailChannel emailChannel;

    @BeforeEach
    void setUp() {
        lenient().when(emailProvider.getProviderName()).thenReturn("MockProvider");
        emailChannel = new EmailChannel(emailProvider);
    }

    @Test
    @DisplayName("Delega el envío al provider y retorna su resultado")
    void shouldDelegateToProvider() {
        EmailNotification email = EmailNotification.builder()
                .to("test@example.com")
                .subject("Hi")
                .message("Hello")
                .build();

        NotificationResult expected = NotificationResult.success(email.getId(), "mock-123");
        when(emailProvider.send(any())).thenReturn(expected);

        NotificationResult result = emailChannel.send(email);

        assertTrue(result.isSuccess());
        assertEquals("mock-123", result.getProviderMessageId());
        verify(emailProvider, times(1)).send(email);
    }

    @Test
    @DisplayName("Propaga SendException si el provider falla")
    void shouldPropagateProviderException() {
        EmailNotification email = EmailNotification.builder()
                .to("test@example.com")
                .subject("Hi")
                .message("Hello")
                .build();

        when(emailProvider.send(any())).thenThrow(new SendException("API Error 500"));

        assertThrows(SendException.class, () -> emailChannel.send(email));
    }

    @Test
    @DisplayName("getChannelType retorna EMAIL")
    void shouldReturnEmailChannelType() {
        assertEquals(ChannelType.EMAIL, emailChannel.getChannelType());
    }

    @Test
    @DisplayName("Lanza excepción si se construye con provider nulo")
    void shouldThrowOnNullProvider() {
        assertThrows(IllegalArgumentException.class, () -> new EmailChannel(null));
    }
}
