package com.notify.channel.sms.provider;

import com.notify.channel.sms.SmsNotification;
import com.notify.core.NotificationResult;

/**
 * Contrato para proveedores de SMS (Twilio, Amazon SNS, Vonage, etc.).
 */
public interface SmsProvider {

    NotificationResult send(SmsNotification notification);

    String getProviderName();
}
