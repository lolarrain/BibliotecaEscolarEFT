package dao;

import modelo.ReporteLibro;
import modelo.ReporteHistorial;
import modelo.ReportePrestamoActivo;
import java.sql.SQLException;
import java.util.List;

/** Contrato de consultas de reportes de biblioteca. */
public interface ReporteDAO {
    List<ReporteLibro> listarLibrosMasPrestados() throws SQLException;
    List<ReporteHistorial> listarHistorialEstudiante(int idEstudiante) throws SQLException;
    List<ReportePrestamoActivo> listarPrestamosActivos() throws SQLException;
}