package com.notify.channel.email;

import com.notify.core.ChannelType;
import com.notify.core.Notification;

import java.util.List;
import java.util.Map;

/**
 * Notificación específica del canal Email.
 */
public class EmailNotification extends Notification {

    private final String subject;      // Asunto del correo (obligatorio)
    private final String from;         // Remitente (ej: "noreply@miapp.com")
    private final String htmlContent;  // Contenido HTML (alternativa al texto plano)
    private final List<String> cc;     // Copia
    private final List<String> bcc;    // Copia oculta

    private EmailNotification(Builder builder) {
        super(builder.to, builder.message, builder.metadata);
        this.subject = builder.subject;
        this.from = builder.from;
        this.htmlContent = builder.htmlContent;
        this.cc = builder.cc != null ? List.copyOf(builder.cc) : List.of();
        this.bcc = builder.bcc != null ? List.copyOf(builder.bcc) : List.of();
    }

    @Override
    public ChannelType getChannelType() {
        return ChannelType.EMAIL;
    }

    // Getters específicos de Email

    public String getSubject() {
        return subject;
    }

    public String getFrom() {
        return from;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public List<String> getCc() {
        return cc;
    }

    public List<String> getBcc() {
        return bcc;
    }

    //Builder

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String to;
        private String message;
        private String subject;
        private String from;
        private String htmlContent;
        private List<String> cc;
        private List<String> bcc;
        private Map<String, String> metadata;

        public Builder to(String to) { this.to = to; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder subject(String subject) { this.subject = subject; return this; }
        public Builder from(String from) { this.from = from; return this; }
        public Builder htmlContent(String htmlContent) { this.htmlContent = htmlContent; return this; }
        public Builder cc(List<String> cc) { this.cc = cc; return this; }
        public Builder bcc(List<String> bcc) { this.bcc = bcc; return this; }
        public Builder metadata(Map<String, String> metadata) { this.metadata = metadata; return this; }

        public EmailNotification build() {
            return new EmailNotification(this);
        }
    }
}
