package modelo;

import java.time.LocalDate;

/** Un préstamo del historial de un estudiante. */
public record ReporteHistorial(
        int idPrestamo,
        String estudiante,
        String libro,
        LocalDate fechaPrestamo,
        LocalDate fechaVencimiento,
        boolean devuelto
) {}