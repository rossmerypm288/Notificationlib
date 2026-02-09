package com.notify.channel.push.provider;

import com.notify.channel.push.PushNotification;
import com.notify.core.NotificationResult;

/**
 * Contrato para proveedores de Push Notification (Firebase FCM, OneSignal, etc.).
 */
public interface PushProvider {

    NotificationResult send(PushNotification notification);

    String getProviderName();
}
