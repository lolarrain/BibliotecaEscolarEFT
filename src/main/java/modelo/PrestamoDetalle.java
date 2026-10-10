package modelo;

import java.time.LocalDate;

/**
 * Representa los datos necesarios para visualizar un préstamo.
 */
public record PrestamoDetalle(
        int id,
        String estudiante,
        String libro,
        LocalDate fechaPrestamo,
        LocalDate fechaVencimiento,
        boolean atrasado
) {
}


