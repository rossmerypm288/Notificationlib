package com.notify.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Resultado de la validación de una notificación.
 */
public class ValidationResult {

    private final List<String> errors;

    private ValidationResult(List<String> errors) {
        this.errors = Collections.unmodifiableList(errors);
    }

    /**
     * Crea un resultado válido (sin errores).
     */
    public static ValidationResult valid() {
        return new ValidationResult(List.of());
    }

    /**
     * Crea un resultado inválido con lista de errores.
     */
    public static ValidationResult invalid(List<String> errors) {
        return new ValidationResult(new ArrayList<>(errors));
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public List<String> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return isValid() ? "ValidationResult[VALID]"
                : "ValidationResult[INVALID: " + String.join(", ", errors) + "]";
    }
}
