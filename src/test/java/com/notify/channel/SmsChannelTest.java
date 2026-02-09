package com.notify.channel;

import com.notify.channel.sms.SmsChannel;
import com.notify.channel.sms.SmsNotification;
import com.notify.channel.sms.provider.SmsProvider;
import com.notify.core.ChannelType;
import com.notify.core.NotificationResult;
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
 * Tests del SmsChannel — misma estructura que EmailChannelTest.
 * Demuestra consistencia del patrón en todos los canales.
 */
@ExtendWith(MockitoExtension.class)
class SmsChannelTest {

    @Mock
    private SmsProvider smsProvider;

    private SmsChannel smsChannel;

    @BeforeEach
    void setUp() {
        lenient().when(smsProvider.getProviderName()).thenReturn("MockSmsProvider");
        smsChannel = new SmsChannel(smsProvider);
    }

    @Test
    @DisplayName("Delega el envío de SMS al provider correctamente")
    void shouldDelegateToProvider() {
        SmsNotification sms = SmsNotification.builder()
                .to("+51999888777")
                .message("Test SMS")
                .from("+15551234567")
                .build();

        NotificationResult expected = NotificationResult.success(sms.getId(), "SM-mock-1");
        when(smsProvider.send(any())).thenReturn(expected);

        NotificationResult result = smsChannel.send(sms);

        assertTrue(result.isSuccess());
        assertEquals("SM-mock-1", result.getProviderMessageId());
        verify(smsProvider, times(1)).send(sms);
    }

    @Test
    @DisplayName("getChannelType retorna SMS")
    void shouldReturnSmsChannelType() {
        assertEquals(ChannelType.SMS, smsChannel.getChannelType());
    }

    @Test
    @DisplayName("isAvailable retorna true cuando el provider existe")
    void shouldBeAvailableWithProvider() {
        assertTrue(smsChannel.isAvailable());
    }
}
