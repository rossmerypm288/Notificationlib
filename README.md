# notification-lib – Backend Challenge

Librería modular para el envío de notificaciones Email, SMS y Push,
diseñada con principios de código limpio y extensibilidad.

---

## Decisiones de diseño

- **Arquitectura hexagonal**: Core desacoplado de proveedores, permite agregar canales sin impactar lógica principal
- **Patrón Strategy**: Cada canal encapsula su comportamiento y validaciones
- **Result Pattern**: `NotificationResult` evita excepciones como control de flujo en envíos batch
- **RuntimeException**: No forzar try-catch, tendencia moderna en Java

---

## Arquitectura

```
┌─────────────────────────────────────────────┐
│              Código Cliente                  │
└─────────────────────┬───────────────────────┘
                      ▼
┌─────────────────────────────────────────────┐
│           NotificationService                │
│              (Facade)                        │
└──────┬──────────────┬──────────────┬────────┘
       ▼              ▼              ▼
  EmailChannel   SmsChannel    PushChannel
       ▼              ▼              ▼
  SendGrid       Twilio        Firebase
  Mailgun        Amazon SNS    OneSignal
```

---

## Patrones de diseño

| Patrón | Uso |
|--------|-----|
| **Strategy** | Canales intercambiables |
| **Facade** | `NotificationService` como punto único |
| **Builder** | Construcción de notificaciones y configuración |
| **Factory Method** | `NotificationResult.success()` / `failure()` |

---

## Principios SOLID

- **SRP**: Cada clase una responsabilidad
- **OCP**: Nuevos canales sin modificar código existente
- **LSP**: Canales sustituibles vía `NotificationChannel`
- **ISP**: Interfaces específicas por canal
- **DIP**: Dependencias sobre abstracciones

---

## Quick Start

```java
NotificationService service = NotificationService.builder()
    .channel(new EmailChannel(new SendGridProvider(config)))
    .build();

EmailNotification email = EmailNotification.builder()
    .to("user@mail.com")
    .subject("Bienvenido")
    .message("Gracias por registrarte")
    .build();

NotificationResult result = service.send(email);
```

---

## Proveedores soportados (simulados)

| Canal | Proveedores |
|-------|-------------|
| Email | SendGrid, Mailgun |
| SMS | Twilio, Amazon SNS |
| Push | Firebase, OneSignal |

---

## Ejecutar

```bash
# Tests
mvn clean test

# Docker
docker build -t notification-lib .
docker run --rm notification-lib
```

---

## Requisitos

- Java 21+
- Maven 3.8+

---

## Autor

**Rossmery Liz Pecho Medoza**  
Java Backend Developer
