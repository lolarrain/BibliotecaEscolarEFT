package dao.impl;

import config.DatabaseConnection;
import dao.ReporteDAO;
import modelo.ReporteLibro;
import modelo.ReporteHistorial;
import modelo.ReportePrestamoActivo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Consultas de reportes ejecutadas mediante JDBC. */
public class ReporteDAOImpl implements ReporteDAO {

    @Override
    public List<ReporteLibro> listarLibrosMasPrestados() throws SQLException {
        List<ReporteLibro> reportes = new ArrayList<>();
        String sql = """
                SELECT l.id, l.titulo, l.autor, COUNT(p.id) AS total_prestamos
                FROM libros l
                LEFT JOIN prestamos p ON l.id = p.id_libro
                GROUP BY l.id, l.titulo, l.autor
                ORDER BY total_prestamos DESC, l.titulo ASC
                """;
        try (Connection conexion = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql);
             ResultSet resultado = statement.executeQuery()) {
            while (resultado.next()) {
                reportes.add(new ReporteLibro(
                        resultado.getInt("id"),
                        resultado.getString("titulo"),
                        resultado.getString("autor"),
                        resultado.getInt("total_prestamos")));
            }
        }
        return reportes;
    }

    @Override
    public List<ReporteHistorial> listarHistorialEstudiante(int idEstudiante) throws SQLException {
        List<ReporteHistorial> historial = new ArrayList<>();
        String sql = """
                SELECT p.id, e.nombre AS estudiante, l.titulo AS libro,
                       p.fecha_prestamo, p.fecha_devolucion, p.devuelto
                FROM prestamos p
                JOIN estudiantes e ON e.id = p.id_estudiante
                JOIN libros l ON l.id = p.id_libro
                WHERE p.id_estudiante = ?
                ORDER BY p.fecha_prestamo DESC, p.id DESC
                """;
        try (Connection conexion = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setInt(1, idEstudiante);
            try (ResultSet resultado = statement.executeQuery()) {
                while (resultado.next()) {
                    historial.add(new ReporteHistorial(
                            resultado.getInt("id"),
                            resultado.getString("estudiante"),
                            resultado.getString("libro"),
                            resultado.getDate("fecha_prestamo").toLocalDate(),
                            resultado.getDate("fecha_devolucion").toLocalDate(),
                            resultado.getBoolean("devuelto")));
                }
            }
        }
        return historial;
    }

    @Override
    public List<ReportePrestamoActivo> listarPrestamosActivos() throws SQLException {
        List<ReportePrestamoActivo> activos = new ArrayList<>();
        String sql = """
                SELECT p.id, l.titulo, e.nombre AS estudiante,
                       p.fecha_prestamo, p.fecha_devolucion
                FROM prestamos p
                JOIN libros l ON l.id = p.id_libro
                JOIN estudiantes e ON e.id = p.id_estudiante
                WHERE p.devuelto = 0
                ORDER BY p.fecha_devolucion ASC, p.id ASC
                """;
        try (Connection conexion = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql);
             ResultSet resultado = statement.executeQuery()) {
            while (resultado.next()) {
                activos.add(new ReportePrestamoActivo(
                        resultado.getInt("id"),
                        resultado.getString("titulo"),
                        resultado.getString("estudiante"),
                        resultado.getDate("fecha_prestamo").toLocalDate(),
                        resultado.getDate("fecha_devolucion").toLocalDate()));
            }
        }
        return activos;
    }
}
