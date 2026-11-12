package co.edu.ucc.smartlibrary.exception;

import java.time.Instant;

public record ApiError(int codigo, String mensaje, Instant fecha) {
    public static ApiError de(int codigo, String mensaje) {
        return new ApiError(codigo, mensaje, Instant.now());
    }
}
