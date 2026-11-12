package co.edu.ucc.smartlibrary.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PrestamoRequest(
        @NotNull(message = "El estudianteId es obligatorio")
        @Positive(message = "El estudianteId debe ser positivo")
        Long estudianteId,
        @NotNull(message = "El libroId es obligatorio")
        @Positive(message = "El libroId debe ser positivo")
        Long libroId
) { }
