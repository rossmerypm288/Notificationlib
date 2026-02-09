package com.notify.template;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Motor de templates simple para mensajes de notificación.
 */
public class MessageTemplate {

    // Regex para encontrar {{variable}} en el template
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{(\\w+)}}");

    private final String templateText;

    private MessageTemplate(String templateText) {
        if (templateText == null || templateText.isBlank()) {
            throw new IllegalArgumentException("El template no puede ser vacío");
        }
        this.templateText = templateText;
    }

    /**
     * Factory method para crear un template.
     */
    public static MessageTemplate of(String templateText) {
        return new MessageTemplate(templateText);
    }

    /**
     * Renderiza el template reemplazando {{variables}} con los valores del mapa.
     *
     * @param variables Mapa de nombre → valor para cada variable
     * @return String con las variables reemplazadas
     * @throws IllegalArgumentException si faltan variables requeridas
     */
    public String render(Map<String, String> variables) {
        Matcher matcher = VARIABLE_PATTERN.matcher(templateText);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String variableName = matcher.group(1);
            String value = variables.get(variableName);

            if (value == null) {
                throw new IllegalArgumentException(
                        "Variable requerida no proporcionada: {{" + variableName + "}}"
                );
            }

            matcher.appendReplacement(result, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    /**
     * Retorna el texto original del template (sin renderizar).
     */
    public String getRawTemplate() {
        return templateText;
    }

    @Override
    public String toString() {
        return "MessageTemplate[" + templateText + "]";
    }
}
