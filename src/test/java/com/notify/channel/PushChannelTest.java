package com.notify.channel;

import com.notify.channel.push.PushChannel;
import com.notify.channel.push.PushNotification;
import com.notify.channel.push.provider.PushProvider;
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
 * Tests del PushChannel — misma estructura para consistencia.
 */
@ExtendWith(MockitoExtension.class)
class PushChannelTest {

    @Mock
    private PushProvider pushProvider;

    private PushChannel pushChannel;

    @BeforeEach
    void setUp() {
        lenient().when(pushProvider.getProviderName()).thenReturn("MockPushProvider");
        pushChannel = new PushChannel(pushProvider);
    }

    @Test
    @DisplayName("Delega envío push al provider correctamente")
    void shouldDelegateToProvider() {
        PushNotification push = PushNotification.builder()
                .deviceToken("fcm-test-token-1234567890")
                .title("Nuevo mensaje")
                .message("Tienes un mensaje nuevo")
                .build();

        NotificationResult expected = NotificationResult.success(push.getId(), "fcm-mock-1");
        when(pushProvider.send(any())).thenReturn(expected);

        NotificationResult result = pushChannel.send(push);

        assertTrue(result.isSuccess());
        verify(pushProvider, times(1)).send(push);
    }

    @Test
    @DisplayName("getChannelType retorna PUSH_NOTIFICATION")
    void shouldReturnPushChannelType() {
        assertEquals(ChannelType.PUSH_NOTIFICATION, pushChannel.getChannelType());
    }
}
