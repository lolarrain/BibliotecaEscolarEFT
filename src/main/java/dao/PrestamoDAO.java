package dao;

import modelo.Prestamo;
import modelo.PrestamoDetalle;

import java.sql.SQLException;
import java.util.List;

/**
 * Define las operaciones de persistencia
 * relacionadas con préstamos y devoluciones.
 */
public interface PrestamoDAO {

    /**
     * Registra un préstamo y descuenta el stock.
     */
    void registrarPrestamo(Prestamo prestamo) throws SQLException;

    /**
     * Registra una devolución y recupera el stock.
     *
     * @return true si la devolución fue registrada.
     */
    boolean registrarDevolucion(int idPrestamo) throws SQLException;

    List<PrestamoDetalle> listarPendientesDeEstudiante(int idEstudiante) throws SQLException;

    boolean perteneceAEstudiante(int idPrestamo, int idEstudiante) throws SQLException;

    /**
     * Obtiene los préstamos pendientes.
     */
    List<Prestamo> listarPendientes() throws SQLException;

    /**
     * Obtiene los préstamos pendientes con información
     * del estudiante y del libro.
     */
    List<PrestamoDetalle> listarPendientesDetallados()
            throws SQLException;
}