# 📬 Notification Library

Librería de notificaciones multi-canal para Java, agnóstica a frameworks y extensible.

Permite enviar notificaciones por **Email**, **SMS** y **Push Notification** a través de diferentes proveedores (SendGrid, Twilio, Firebase, etc.) con una interfaz unificada.

---

## 📋 Tabla de contenidos

- [Instalación](#-instalación)
- [Quick Start](#-quick-start)
- [Arquitectura](#-arquitectura)
- [Configuración](#-configuración-de-proveedores)
- [Canales y Proveedores](#-canales-y-proveedores-soportados)
- [API Reference](#-api-reference)
- [Templates](#-templates-de-mensajes)
- [Envío Asíncrono](#-envío-asíncrono)
- [Manejo de Errores](#-manejo-de-errores)
- [Extensibilidad](#-cómo-agregar-un-nuevo-canal)
- [Testing](#-testing)
- [Seguridad](#-seguridad)
- [Docker](#-docker)
- [Decisiones de Diseño](#-decisiones-de-diseño)

---

## 📦 Instalación

### Maven

```xml
<dependency>
    <groupId>com.notify</groupId>
    <artifactId>notification-lib</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Compilar desde código fuente

```bash
git clone https://github.com/tu-usuario/notification-lib.git
cd notification-lib
mvn clean package
```

---

## 🚀 Quick Start

```java
// 1. Configurar proveedores
ProviderConfig sendGridConfig = ProviderConfig.builder("sendgrid")
    .property("apiKey", System.getenv("SENDGRID_API_KEY"))
    .property("fromEmail", "noreply@miapp.com")
    .build();

// 2. Crear el servicio
NotificationService service = NotificationService.builder()
    .channel(new EmailChannel(new SendGridProvider(sendGridConfig)))
    .build();

// 3. Enviar notificación
EmailNotification email = EmailNotification.builder()
    .to("usuario@correo.com")
    .subject("Bienvenido")
    .message("Gracias por registrarte")
    .build();

NotificationResult result = service.send(email);

if (result.isSuccess()) {
    System.out.println("Enviado! ID: " + result.getProviderMessageId());
}
```

---

## 🏗 Arquitectura

```
┌─────────────────────────────────────────────────┐
│                Código Cliente                     │
│           (tu aplicación / servicio)              │
└──────────────────┬──────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────┐
│            NotificationService                    │
│          (Facade + Registry Pattern)              │
│  - Valida notificaciones                         │
│  - Enruta al canal correcto                      │
│  - Orquesta envío sync/async/batch               │
└────┬────────────┬──────────────┬────────────────┘
     │            │              │
     ▼            ▼              ▼
┌─────────┐ ┌──────────┐ ┌───────────┐
│ Email   │ │   SMS    │ │   Push    │
│ Channel │ │  Channel │ │  Channel  │
└────┬────┘ └────┬─────┘ └────┬──────┘
     │           │             │
     ▼           ▼             ▼
┌─────────┐ ┌──────────┐ ┌───────────┐
│SendGrid │ │ Twilio   │ │ Firebase  │
│Mailgun  │ │Amazon SNS│ │ OneSignal │
└─────────┘ └──────────┘ └───────────┘
```

### Patrones de diseño aplicados

| Patrón | Dónde | Por qué |
|--------|-------|---------|
| **Strategy** | `NotificationChannel`, `*Provider` | Intercambiar canales/proveedores sin modificar código |
| **Facade** | `NotificationService` | Punto de entrada único, oculta complejidad |
| **Builder** | `*Notification`, `ProviderConfig` | Construcción fluida de objetos complejos |
| **Factory Method** | `NotificationResult.success/failure` | Creación semántica de resultados |
| **Registry** | `Map<ChannelType, Channel>` en `NotificationService` | Registro dinámico de canales |
| **Template Method** | `Notification` (clase abstracta) | Estructura base que las subclases completan |

### Principios SOLID

- **SRP**: Cada clase tiene una única responsabilidad (validar, enviar, configurar)
- **OCP**: Nuevos canales/proveedores se agregan sin modificar código existente
- **LSP**: Todos los canales son sustituibles a través de `NotificationChannel`
- **ISP**: Interfaces pequeñas y específicas (`EmailProvider`, `SmsProvider`, etc.)
- **DIP**: Las clases dependen de abstracciones, no de implementaciones concretas

---

## ⚙ Configuración de Proveedores

Toda la configuración se hace mediante código Java puro (sin YAML/properties):

### Email - SendGrid

```java
ProviderConfig config = ProviderConfig.builder("sendgrid")
    .property("apiKey", System.getenv("SENDGRID_API_KEY"))
    .property("fromEmail", "noreply@miapp.com")
    .build();

EmailChannel channel = new EmailChannel(new SendGridProvider(config));
```

### Email - Mailgun

```java
ProviderConfig config = ProviderConfig.builder("mailgun")
    .property("apiKey", System.getenv("MAILGUN_API_KEY"))
    .property("domain", "mg.miapp.com")
    .build();

EmailChannel channel = new EmailChannel(new MailgunProvider(config));
```

### SMS - Twilio

```java
ProviderConfig config = ProviderConfig.builder("twilio")
    .property("accountSid", System.getenv("TWILIO_ACCOUNT_SID"))
    .property("authToken", System.getenv("TWILIO_AUTH_TOKEN"))
    .property("fromNumber", "+15551234567")
    .build();

SmsChannel channel = new SmsChannel(new TwilioProvider(config));
```

### SMS - Amazon SNS

```java
ProviderConfig config = ProviderConfig.builder("amazon-sns")
    .property("accessKey", System.getenv("AWS_ACCESS_KEY"))
    .property("secretKey", System.getenv("AWS_SECRET_KEY"))
    .property("region", "us-east-1")
    .build();

SmsChannel channel = new SmsChannel(new AmazonSnsProvider(config));
```

### Push - Firebase FCM

```java
ProviderConfig config = ProviderConfig.builder("firebase")
    .property("projectId", "mi-proyecto")
    .property("serviceAccountKey", System.getenv("FIREBASE_SERVICE_ACCOUNT"))
    .build();

PushChannel channel = new PushChannel(new FirebaseProvider(config));
```

### Push - OneSignal

```java
ProviderConfig config = ProviderConfig.builder("onesignal")
    .property("appId", System.getenv("ONESIGNAL_APP_ID"))
    .property("apiKey", System.getenv("ONESIGNAL_API_KEY"))
    .build();

PushChannel channel = new PushChannel(new OneSignalProvider(config));
```

---

## 📡 Canales y Proveedores Soportados

| Canal | Proveedor | Credenciales requeridas |
|-------|-----------|------------------------|
| Email | SendGrid | `apiKey`, `fromEmail` |
| Email | Mailgun | `apiKey`, `domain` |
| SMS | Twilio | `accountSid`, `authToken`, `fromNumber` |
| SMS | Amazon SNS | `accessKey`, `secretKey`, `region` |
| Push | Firebase FCM | `projectId`, `serviceAccountKey` |
| Push | OneSignal | `appId`, `apiKey` |

---

## 📚 API Reference

### NotificationService

```java
// Crear servicio
NotificationService service = NotificationService.builder()
    .channel(emailChannel)
    .channel(smsChannel)
    .channel(pushChannel)
    .build();

// Envío síncrono
NotificationResult result = service.send(notification);

// Envío asíncrono
CompletableFuture<NotificationResult> future = service.sendAsync(notification);

// Envío en lote
CompletableFuture<List<NotificationResult>> batch = service.sendBatch(List.of(n1, n2, n3));

// Verificar disponibilidad
boolean available = service.isChannelAvailable(ChannelType.EMAIL);
```

### EmailNotification

```java
EmailNotification email = EmailNotification.builder()
    .to("dest@correo.com")          // Obligatorio
    .subject("Asunto")               // Obligatorio
    .message("Texto plano")          // Obligatorio
    .from("origen@app.com")          // Opcional
    .htmlContent("<h1>HTML</h1>")    // Opcional
    .cc(List.of("copia@app.com"))    // Opcional
    .bcc(List.of("oculto@app.com"))  // Opcional
    .metadata(Map.of("key", "val"))  // Opcional
    .build();
```

### SmsNotification

```java
SmsNotification sms = SmsNotification.builder()
    .to("+51999888777")              // Obligatorio (E.164)
    .message("Tu código: 1234")      // Obligatorio (máx 160 chars)
    .from("+15551234567")            // Opcional
    .build();
```

### PushNotification

```java
PushNotification push = PushNotification.builder()
    .deviceToken("fcm-token-xxx")    // Obligatorio
    .title("Título")                 // Obligatorio
    .message("Cuerpo del mensaje")   // Obligatorio
    .imageUrl("https://...")         // Opcional
    .data(Map.of("key", "value"))    // Opcional (payload para la app)
    .build();
```

### NotificationResult

```java
NotificationResult result = service.send(notification);

result.isSuccess();              // true/false
result.getNotificationId();      // UUID de la notificación
result.getProviderMessageId();   // ID asignado por el proveedor
result.getErrorMessage();        // Mensaje de error (si falló)
result.getStatus();              // SENT, FAILED, PENDING, RETRYING
result.getProcessedAt();         // Timestamp de procesamiento
```

---

## 📝 Templates de Mensajes

```java
// Definir template reutilizable
MessageTemplate template = MessageTemplate.of(
    "Hola {{nombre}}, tu pedido #{{orderId}} está {{status}}"
);

// Renderizar con datos
String message = template.render(Map.of(
    "nombre", "María",
    "orderId", "ORD-001",
    "status", "en camino"
));
// → "Hola María, tu pedido #ORD-001 está en camino"

// Usar en notificación
EmailNotification email = EmailNotification.builder()
    .to("maria@correo.com")
    .subject("Actualización de pedido")
    .message(message)
    .build();
```

---

## ⚡ Envío Asíncrono

```java
// Envío no bloqueante
service.sendAsync(email)
    .thenAccept(result -> {
        if (result.isSuccess()) {
            log.info("Enviado: {}", result.getProviderMessageId());
        }
    })
    .exceptionally(error -> {
        log.error("Error: {}", error.getMessage());
        return null;
    });

// Envío en lote (paralelo)
service.sendBatch(List.of(email, sms, push))
    .thenAccept(results -> {
        long exitosos = results.stream().filter(NotificationResult::isSuccess).count();
        log.info("Enviados: {}/{}", exitosos, results.size());
    });
```

---

## ⚠ Manejo de Errores

La librería define una jerarquía clara de excepciones:

```
NotificationException (base)
├── ValidationException    → Datos inválidos (email malformado, etc.)
├── SendException          → Error del proveedor (timeout, 500, etc.)
└── ChannelNotFoundException → Canal no registrado
```

```java
try {
    service.send(notification);
} catch (ValidationException e) {
    // Error del usuario: datos inválidos
    log.warn("Datos inválidos: {}", e.getMessage());
} catch (SendException e) {
    // Error del proveedor: reintentar o alertar
    log.error("Error de envío: {}", e.getMessage());
} catch (ChannelNotFoundException e) {
    // Error de configuración: canal no registrado
    log.error("Canal no configurado: {}", e.getMessage());
}
```

Además, `NotificationResult` permite verificar sin excepciones:

```java
NotificationResult result = service.send(notification);
if (!result.isSuccess()) {
    log.warn("Falló: {}", result.getErrorMessage());
}
```

---

## 🔌 Cómo Agregar un Nuevo Canal

Ejemplo: agregar canal de **WhatsApp**.

### Paso 1: Agregar el tipo al enum

```java
public enum ChannelType {
    EMAIL, SMS, PUSH_NOTIFICATION,
    WHATSAPP  // ← Nuevo
}
```

### Paso 2: Crear el modelo de notificación

```java
public class WhatsAppNotification extends Notification {
    private final String templateName;  // WhatsApp Business usa templates

    @Override
    public ChannelType getChannelType() {
        return ChannelType.WHATSAPP;
    }
}
```

### Paso 3: Crear la interfaz del proveedor

```java
public interface WhatsAppProvider {
    NotificationResult send(WhatsAppNotification notification);
    String getProviderName();
}
```

### Paso 4: Implementar el proveedor

```java
public class TwilioWhatsAppProvider implements WhatsAppProvider {
    // Twilio también soporta WhatsApp Business API
}
```

### Paso 5: Crear el canal

```java
public class WhatsAppChannel implements NotificationChannel<WhatsAppNotification> {
    private final WhatsAppProvider provider;
    // ...
}
```

### Paso 6: Registrar en el servicio

```java
NotificationService service = NotificationService.builder()
    .channel(new EmailChannel(...))
    .channel(new WhatsAppChannel(new TwilioWhatsAppProvider(config)))  // ← Nuevo
    .build();
```

**Código existente no se modifica** — Principio Open/Closed.

---

## 🧪 Testing

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar test específico
mvn test -Dtest=NotificationServiceTest

# Ejecutar con reporte de cobertura
mvn test jacoco:report
```

Los tests usan **Mockito** para simular canales y proveedores. No se hacen conexiones reales a APIs externas.

---

## 🔒 Seguridad

### Mejores prácticas para credenciales

1. **NUNCA** hardcodear API keys en el código
2. Usar **variables de entorno** para credenciales:
   ```java
   .property("apiKey", System.getenv("SENDGRID_API_KEY"))
   ```
3. La clase `ProviderConfig` no imprime valores sensibles en `toString()`
4. Las configuraciones son **inmutables** después de construidas
5. En CI/CD, usar secrets managers (AWS Secrets Manager, Azure Key Vault, etc.)

---

## 🐳 Docker

```bash
# Construir imagen
docker build -t notification-lib .

# Ejecutar ejemplos
docker run notification-lib

# Ejecutar con variables de entorno reales
docker run \
  -e SENDGRID_API_KEY=tu-api-key \
  -e TWILIO_ACCOUNT_SID=tu-sid \
  notification-lib
```

---

## 🎯 Decisiones de Diseño

### ¿Por qué clase abstracta en vez de interfaz para Notification?
Porque necesitamos **estado compartido** (id, recipient, message, timestamps) común a todos los canales. Una interfaz solo tendría métodos, no campos.

### ¿Por qué Strategy Pattern para proveedores?
Porque el cambio de proveedor (SendGrid → Mailgun) debe ser **transparente** para el código cliente. Solo cambias la configuración, no el código.

### ¿Por qué RuntimeException en vez de checked exceptions?
Para no forzar try-catch al consumidor. Quien quiera capturar errores puede hacerlo, pero no es obligatorio. Esto sigue la tendencia moderna de Java.

### ¿Por qué Map<String, String> para ProviderConfig?
Porque cada proveedor necesita **datos diferentes** (apiKey vs accountSid vs projectId). Un Map extensible es más flexible que una clase con campos fijos.

### ¿Por qué un validador centralizado?
Las reglas de validación son pocas y simples. Un validador por canal agregaría complejidad sin beneficio real. Si crecieran, se podría refactorizar a validadores por canal.

---

## 📋 Roadmap (Qué falta por implementar)

- [ ] Circuit Breaker para failover entre proveedores
- [ ] Métricas de envío (Micrometer)
- [ ] Rate limiting por canal
- [ ] Persistencia de notificaciones enviadas
- [ ] Pub/Sub para notificar estados de envío
- [ ] Soporte para adjuntos en Email
- [ ] Canal de Slack (webhook)
- [ ] Canal de WhatsApp (Twilio/Meta Business API)

---

## 📄 Licencia

MIT License

<!-- Resuelto con apoyo de GitHub Copilot. Se utilizó como herramienta de asistencia para autocompletado y sugerencias de código durante el desarrollo. Las decisiones de arquitectura, diseño de patrones y estructura del proyecto fueron tomadas por el desarrollador. Copilot asistió principalmente en la generación de boilerplate code y documentación. -->
