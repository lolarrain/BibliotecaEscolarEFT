package controlador;

import config.Sesion;
import dao.ReporteDAO;
import dao.impl.ReporteDAOImpl;
import modelo.ReporteLibro;
import modelo.ReporteHistorial;
import modelo.ReportePrestamoActivo;

import java.sql.SQLException;
import java.util.List;

/** Control de permisos y consultas de reportes. */
public class ControladorReportes {
    private final ReporteDAO reporteDAO;

    public ControladorReportes() {
        this.reporteDAO = new ReporteDAOImpl();
    }

    private void verificarPermisos() {
        if (!Sesion.esBibliotecario()) {
            throw new SecurityException("No tienes permisos para consultar reportes.");
        }
    }

    public List<ReporteLibro> listarLibrosMasPrestados() throws SQLException {
        verificarPermisos();
        return reporteDAO.listarLibrosMasPrestados();
    }

    public List<ReporteHistorial> listarHistorialEstudiante(int idEstudiante) throws SQLException {
        verificarPermisos();
        if (idEstudiante <= 0) {
            throw new IllegalArgumentException("Selecciona un estudiante válido.");
        }
        return reporteDAO.listarHistorialEstudiante(idEstudiante);
    }

    public List<ReportePrestamoActivo> listarPrestamosActivos() throws SQLException {
        verificarPermisos();
        return reporteDAO.listarPrestamosActivos();
    }
}

