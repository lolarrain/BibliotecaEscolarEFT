package modelo;

import java.time.LocalDate;

/** Libro que permanece prestado, con identificación del estudiante. */
public record ReportePrestamoActivo(
        int idPrestamo,
        String titulo,
        String estudiante,
        LocalDate fechaPrestamo,
        LocalDate fechaVencimiento
) {}