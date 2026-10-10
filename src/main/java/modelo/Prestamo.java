package modelo;

import java.time.LocalDate;

/**
 * Representa un préstamo de libros de la biblioteca.
 * Implementa Validable para comprobar sus reglas de negocio.
 */
public class Prestamo implements Validable {

    private int id;
    private int idEstudiante;
    private int idLibro;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;
    private boolean devuelto;

    /**
     * Construye un préstamo con sus datos principales.
     *
     * @param id identificador del préstamo.
     * @param idEstudiante identificador del estudiante.
     * @param idLibro identificador del libro.
     * @param fechaPrestamo fecha en que se realiza el préstamo.
     * @param fechaDevolucion fecha de vencimiento del préstamo.
     * @param devuelto indica si el libro fue devuelto.
     */
    public Prestamo(int id, int idEstudiante, int idLibro,
                    LocalDate fechaPrestamo,
                    LocalDate fechaDevolucion,
                    boolean devuelto) {

        this.id = id;
        this.idEstudiante = idEstudiante;
        this.idLibro = idLibro;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
        this.devuelto = devuelto;
    }

    public int getId() {
        return id;
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public int getIdLibro() {
        return idLibro;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public boolean isDevuelto() {
        return devuelto;
    }

    public void setFechaDevolucion(LocalDate fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public void setDevuelto(boolean devuelto) {
        this.devuelto = devuelto;
    }

    /**
     * Valida los identificadores y fechas del préstamo.
     *
     * @throws IllegalArgumentException si los datos son inválidos.
     */
    @Override
    public void validar() {

        if (idEstudiante <= 0) {
            throw new IllegalArgumentException(
                    "Debes seleccionar un estudiante válido."
            );
        }

        if (idLibro <= 0) {
            throw new IllegalArgumentException(
                    "Debes seleccionar un libro válido."
            );
        }

        if (fechaPrestamo == null) {
            throw new IllegalArgumentException(
                    "La fecha del préstamo es obligatoria."
            );
        }

        if (fechaDevolucion == null) {
            throw new IllegalArgumentException(
                    "La fecha de vencimiento es obligatoria."
            );
        }

        if (fechaDevolucion.isBefore(fechaPrestamo)) {
            throw new IllegalArgumentException(
                    "El vencimiento no puede ser anterior al préstamo."
            );
        }
    }

    /**
     * Determina si un préstamo pendiente está atrasado.
     *
     * @return true si no ha sido devuelto y superó su vencimiento.
     */
    public boolean estaAtrasado() {

        return !devuelto
                && fechaDevolucion != null
                && LocalDate.now().isAfter(fechaDevolucion);
    }

    /**
     * Calcula el vencimiento a 14 días desde el préstamo.
     *
     * @param fechaPrestamo fecha de inicio.
     * @return fecha de vencimiento.
     */
    public static LocalDate calcularVencimiento(
            LocalDate fechaPrestamo) {

        if (fechaPrestamo == null) {
            throw new IllegalArgumentException(
                    "La fecha del préstamo no puede ser nula."
            );
        }

        return fechaPrestamo.plusDays(14);
    }
}