package modelo;

/**
 * Representa la cantidad de préstamos registrados
 * para un libro.
 */
public record ReporteLibro(
        int idLibro,
        String titulo,
        String autor,
        int totalPrestamos
) {
}