package dao.impl;

import config.DatabaseConnection;
import dao.PrestamoDAO;
import modelo.Prestamo;
import modelo.PrestamoDetalle;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementa las operaciones de préstamos y devoluciones
 * mediante JDBC, transacciones y sincronización.
 */
public class PrestamoDAOImpl implements PrestamoDAO {

    /**
     * Registra un préstamo y descuenta una unidad del stock
     * mediante una transacción JDBC.
     */
    @Override
    public synchronized void registrarPrestamo(Prestamo prestamo)
            throws SQLException {

        String sqlStock = """
                UPDATE libros
                SET stock = stock - 1
                WHERE id = ? AND stock > 0
                """;

        String sqlPrestamo = """
                INSERT INTO prestamos
                (id_estudiante, id_libro, fecha_prestamo,
                 fecha_devolucion, devuelto)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection()) {

            conexion.setAutoCommit(false);

            try {
                // Descontar una unidad del stock.
                try (PreparedStatement statement =
                             conexion.prepareStatement(sqlStock)) {

                    statement.setInt(1, prestamo.getIdLibro());

                    int filas = statement.executeUpdate();

                    if (filas == 0) {
                        throw new SQLException(
                                "El libro no existe o no tiene stock disponible."
                        );
                    }
                }

                // Registrar el préstamo.
                try (PreparedStatement statement =
                             conexion.prepareStatement(sqlPrestamo)) {

                    statement.setInt(1, prestamo.getIdEstudiante());
                    statement.setInt(2, prestamo.getIdLibro());

                    statement.setDate(
                            3,
                            Date.valueOf(prestamo.getFechaPrestamo())
                    );

                    statement.setDate(
                            4,
                            Date.valueOf(prestamo.getFechaDevolucion())
                    );

                    statement.setBoolean(
                            5,
                            prestamo.isDevuelto()
                    );

                    statement.executeUpdate();
                }

                conexion.commit();

            } catch (SQLException e) {

                try {
                    conexion.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }

                throw e;
            }
        }
    }

    /**
     * Registra una devolución y recupera una unidad del stock.
     * Impide registrar dos veces la misma devolución.
     */
    @Override
    public synchronized boolean registrarDevolucion(int idPrestamo)
            throws SQLException {

        String sqlDevolucion = """
                UPDATE prestamos
                SET devuelto = 1
                WHERE id = ? AND devuelto = 0
                """;

        String sqlStock = """
                UPDATE libros
                SET stock = stock + 1
                WHERE id = (
                    SELECT id_libro
                    FROM prestamos
                    WHERE id = ?
                )
                """;

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection()) {

            conexion.setAutoCommit(false);

            try {
                // Marcar el préstamo como devuelto.
                try (PreparedStatement statement =
                             conexion.prepareStatement(sqlDevolucion)) {

                    statement.setInt(1, idPrestamo);

                    int filas = statement.executeUpdate();

                    if (filas == 0) {
                        conexion.rollback();
                        return false;
                    }
                }

                // Recuperar el stock.
                try (PreparedStatement statement =
                             conexion.prepareStatement(sqlStock)) {

                    statement.setInt(1, idPrestamo);

                    int filas = statement.executeUpdate();

                    if (filas != 1) {
                        throw new SQLException(
                                "No fue posible actualizar el stock."
                        );
                    }
                }

                conexion.commit();
                return true;

            } catch (SQLException e) {

                try {
                    conexion.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }

                throw e;
            }
        }
    }

    /**
     * Consulta los préstamos pendientes de devolución.
     */
    @Override
    public List<Prestamo> listarPendientes() throws SQLException {

        List<Prestamo> prestamos = new ArrayList<>();

        String sql = """
                SELECT id, id_estudiante, id_libro,
                       fecha_prestamo, fecha_devolucion, devuelto
                FROM prestamos
                WHERE devuelto = 0
                ORDER BY fecha_devolucion ASC
                """;

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement =
                     conexion.prepareStatement(sql);
             ResultSet resultado = statement.executeQuery()) {

            while (resultado.next()) {

                Prestamo prestamo = new Prestamo(
                        resultado.getInt("id"),
                        resultado.getInt("id_estudiante"),
                        resultado.getInt("id_libro"),
                        resultado.getDate("fecha_prestamo").toLocalDate(),
                        resultado.getDate("fecha_devolucion").toLocalDate(),
                        resultado.getBoolean("devuelto")
                );

                prestamos.add(prestamo);
            }
        }

        return prestamos;
    }

    /**
     * Consulta préstamos pendientes con nombres y títulos.
     */
    @Override
    public List<PrestamoDetalle> listarPendientesDetallados()
            throws SQLException {

        List<PrestamoDetalle> prestamos = new ArrayList<>();

        String sql = """
                SELECT
                    p.id,
                    e.nombre AS estudiante,
                    l.titulo AS libro,
                    p.fecha_prestamo,
                    p.fecha_devolucion
                FROM prestamos p
                JOIN estudiantes e ON p.id_estudiante = e.id
                JOIN libros l ON p.id_libro = l.id
                WHERE p.devuelto = 0
                ORDER BY p.fecha_devolucion ASC
                """;

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement =
                     conexion.prepareStatement(sql);
             ResultSet resultado = statement.executeQuery()) {

            while (resultado.next()) {

                LocalDate fechaPrestamo =
                        resultado.getDate("fecha_prestamo").toLocalDate();

                LocalDate fechaVencimiento =
                        resultado.getDate("fecha_devolucion").toLocalDate();

                boolean atrasado =
                        LocalDate.now().isAfter(fechaVencimiento);

                prestamos.add(new PrestamoDetalle(
                        resultado.getInt("id"),
                        resultado.getString("estudiante"),
                        resultado.getString("libro"),
                        fechaPrestamo,
                        fechaVencimiento,
                        atrasado
                ));
            }
        }

        return prestamos;
    }

    @Override
    public boolean perteneceAEstudiante(int idPrestamo, int idEstudiante) throws SQLException {
        String sql = "SELECT 1 FROM prestamos WHERE id = ? AND id_estudiante = ? AND devuelto = 0";
        try (Connection conexion = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setInt(1, idPrestamo);
            statement.setInt(2, idEstudiante);
            try (ResultSet resultado = statement.executeQuery()) {
                return resultado.next();
            }
        }
    }

    @Override
    public List<PrestamoDetalle> listarPendientesDeEstudiante(int idEstudiante)
            throws SQLException {
        List<PrestamoDetalle> prestamos = new ArrayList<>();
        String sql = """
                SELECT p.id, e.nombre AS estudiante, l.titulo AS libro,
                       p.fecha_prestamo, p.fecha_devolucion
                FROM prestamos p
                JOIN estudiantes e ON e.id = p.id_estudiante
                JOIN libros l ON l.id = p.id_libro
                WHERE p.devuelto = 0 AND p.id_estudiante = ?
                ORDER BY p.fecha_devolucion ASC
                """;
        try (Connection conexion = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setInt(1, idEstudiante);
            try (ResultSet resultado = statement.executeQuery()) {
                while (resultado.next()) {
                    LocalDate vencimiento = resultado.getDate("fecha_devolucion").toLocalDate();
                    prestamos.add(new PrestamoDetalle(
                            resultado.getInt("id"),
                            resultado.getString("estudiante"),
                            resultado.getString("libro"),
                            resultado.getDate("fecha_prestamo").toLocalDate(),
                            vencimiento,
                            LocalDate.now().isAfter(vencimiento)));
                }
            }
        }
        return prestamos;
    }
}