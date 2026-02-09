package com.notify.examples;

import com.notify.channel.email.EmailChannel;
import com.notify.channel.email.EmailNotification;
import com.notify.channel.email.provider.MailgunProvider;
import com.notify.channel.email.provider.SendGridProvider;
import com.notify.channel.push.PushChannel;
import com.notify.channel.push.PushNotification;
import com.notify.channel.push.provider.FirebaseProvider;
import com.notify.channel.sms.SmsChannel;
import com.notify.channel.sms.SmsNotification;
import com.notify.channel.sms.provider.TwilioProvider;
import com.notify.config.ProviderConfig;
import com.notify.core.NotificationResult;
import com.notify.core.NotificationService;
import com.notify.exception.ValidationException;
import com.notify.template.MessageTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Ejemplos de uso de la librería de notificaciones.
 */
public class NotificationExamples {

    public static void main(String[] args) throws Exception {
        System.out.println("===========================================");
        System.out.println("  NOTIFICATION LIBRARY - EJEMPLOS DE USO");
        System.out.println("===========================================\n");

        // EJEMPLO 1: Configuración de proveedores
        // Las credenciales deben venir de variables de entorno (seguridad)
        System.out.println("▶ Ejemplo 1: Configuración de proveedores");

        // Configurar SendGrid para emails
        ProviderConfig sendGridConfig = ProviderConfig.builder("sendgrid")
                .property("apiKey", System.getenv().getOrDefault("SENDGRID_API_KEY", "SG.fake-api-key-demo"))
                .property("fromEmail", "noreply@miapp.com")
                .build();

        // Configurar Twilio para SMS
        ProviderConfig twilioConfig = ProviderConfig.builder("twilio")
                .property("accountSid", System.getenv().getOrDefault("TWILIO_ACCOUNT_SID", "AC-fake-sid-demo"))
                .property("authToken", System.getenv().getOrDefault("TWILIO_AUTH_TOKEN", "fake-auth-token"))
                .property("fromNumber", "+15551234567")
                .build();

        // Configurar Firebase para Push
        ProviderConfig firebaseConfig = ProviderConfig.builder("firebase")
                .property("projectId", "mi-proyecto-firebase")
                .property("serviceAccountKey", "{\"type\":\"service_account\",\"project_id\":\"demo\"}")
                .build();

        // EJEMPLO 2: Crear el servicio con los 3 canales
        System.out.println("▶ Ejemplo 2: Crear NotificationService con 3 canales\n");

        NotificationService service = NotificationService.builder()
                .channel(new EmailChannel(new SendGridProvider(sendGridConfig)))
                .channel(new SmsChannel(new TwilioProvider(twilioConfig)))
                .channel(new PushChannel(new FirebaseProvider(firebaseConfig)))
                .build();

        // EJEMPLO 3: Enviar Email
        System.out.println("\n▶ Ejemplo 3: Enviar Email");

        EmailNotification email = EmailNotification.builder()
                .to("usuario@correo.com")
                .from("noreply@miapp.com")
                .subject("¡Bienvenido a MiApp!")
                .message("Gracias por registrarte. Tu cuenta ha sido creada exitosamente.")
                .htmlContent("<h1>Bienvenido</h1><p>Gracias por registrarte.</p>")
                .cc(List.of("admin@miapp.com"))
                .build();

        NotificationResult emailResult = service.send(email);
        System.out.println("Resultado Email: " + emailResult);

        // EJEMPLO 4: Enviar SMS
        System.out.println("\n▶ Ejemplo 4: Enviar SMS");

        SmsNotification sms = SmsNotification.builder()
                .to("+51999888777")
                .from("+15551234567")
                .message("Tu código de verificación es: 847291")
                .build();

        NotificationResult smsResult = service.send(sms);
        System.out.println("Resultado SMS: " + smsResult);

        // EJEMPLO 5: Enviar Push Notification
        System.out.println("\n▶ Ejemplo 5: Enviar Push Notification");

        PushNotification push = PushNotification.builder()
                .deviceToken("fcm-device-token-abc123def456ghi789")
                .title("Nuevo pedido")
                .message("Tu pedido #12345 ha sido confirmado")
                .imageUrl("https://miapp.com/images/order-confirmed.png")
                .data(Map.of("orderId", "12345", "action", "VIEW_ORDER"))
                .build();

        NotificationResult pushResult = service.send(push);
        System.out.println("Resultado Push: " + pushResult);

        // EJEMPLO 6: Envío asíncrono (no bloqueante)
        System.out.println("\n▶ Ejemplo 6: Envío asíncrono");

        CompletableFuture<NotificationResult> future = service.sendAsync(email);

        // Puede continuar haciendo cosas mientras se envía
        System.out.println("  Notificación enviándose en segundo plano...");

        // Cuando necesite el resultado:
        future.thenAccept(result ->
                System.out.println("  Resultado async: " + result)
        ).join();

        // EJEMPLO 7: Envío en lote
        System.out.println("\n▶ Ejemplo 7: Envío en lote (batch)");

        CompletableFuture<List<NotificationResult>> batchFuture =
                service.sendBatch(List.of(email, sms, push));

        List<NotificationResult> batchResults = batchFuture.get();
        System.out.println("  Total enviados: " + batchResults.size());
        batchResults.forEach(r -> System.out.println("  → " + r));

        // EJEMPLO 8: Manejo de errores
        System.out.println("\n▶ Ejemplo 8: Manejo de errores de validación");

        try {
            EmailNotification invalidEmail = EmailNotification.builder()
                    .to("not-an-email")       //Email inválido
                    .subject("")               //Subject vacío
                    .message("Hello")
                    .build();

            service.send(invalidEmail);
        } catch (ValidationException e) {
            System.out.println("  Error de validación capturado: " + e.getMessage());
        }

        // EJEMPLO 9: Uso de templates
        System.out.println("\n▶ Ejemplo 9: Templates de mensajes");

        MessageTemplate welcomeTemplate = MessageTemplate.of(
                "Hola {{nombre}}, tu pedido #{{orderId}} ha sido {{status}}. " +
                "Llegará el {{fecha}}."
        );

        String renderedMessage = welcomeTemplate.render(Map.of(
                "nombre", "Carlos",
                "orderId", "ORD-2024-001",
                "status", "enviado",
                "fecha", "15 de enero"
        ));

        System.out.println("  Template renderizado: " + renderedMessage);

        // Usa el template en una notificación
        EmailNotification templateEmail = EmailNotification.builder()
                .to("carlos@correo.com")
                .subject("Actualización de pedido")
                .message(renderedMessage)
                .build();

        NotificationResult templateResult = service.send(templateEmail);
        System.out.println("  Resultado: " + templateResult);

        // EJEMPLO 10: Cambiar de proveedor (SendGrid → Mailgun)
        System.out.println("\n▶ Ejemplo 10: Cambio de proveedor transparente");

        ProviderConfig mailgunConfig = ProviderConfig.builder("mailgun")
                .property("apiKey", "key-fake-mailgun-demo")
                .property("domain", "mg.miapp.com")
                .build();

        NotificationService serviceWithMailgun = NotificationService.builder()
                .channel(new EmailChannel(new MailgunProvider(mailgunConfig))) // ← Solo esta línea cambia
                .channel(new SmsChannel(new TwilioProvider(twilioConfig)))
                .channel(new PushChannel(new FirebaseProvider(firebaseConfig)))
                .build();

        // El mismo email se envía ahora por Mailgun en vez de SendGrid
        NotificationResult mailgunResult = serviceWithMailgun.send(email);
        System.out.println("  Resultado con Mailgun: " + mailgunResult);

        System.out.println("\n===========================================");
        System.out.println("  TODOS LOS EJEMPLOS EJECUTADOS EXITOSAMENTE");
        System.out.println("===========================================");
    }
}
