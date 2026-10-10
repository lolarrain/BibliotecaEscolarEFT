package controlador;

import config.Sesion;
import dao.PrestamoDAO;
import dao.impl.PrestamoDAOImpl;
import modelo.Estudiante;
import modelo.Prestamo;
import modelo.PrestamoDetalle;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/** Gestión transaccional de préstamos con permisos según el usuario. */
public class ControladorPrestamos {
    private final PrestamoDAO prestamoDAO = new PrestamoDAOImpl();

    private void verificarBibliotecario() {
        if (!Sesion.esBibliotecario()) {
            throw new SecurityException("Esta operación requiere permisos de bibliotecario.");
        }
    }

    public void registrarPrestamo(int idEstudiante, int idLibro) throws SQLException {
        if (!Sesion.esBibliotecario()) {
            Estudiante actual = new ControladorEstudiantes().obtenerEstudianteActual();
            if (actual.getId() != idEstudiante) {
                throw new SecurityException("Solo puedes registrar préstamos a tu nombre.");
            }
        }
        LocalDate fecha = LocalDate.now();
        Prestamo prestamo = new Prestamo(
                0, idEstudiante, idLibro, fecha,
                Prestamo.calcularVencimiento(fecha), false);
        prestamo.validar();
        prestamoDAO.registrarPrestamo(prestamo);
    }

    public boolean registrarDevolucion(int idPrestamo) throws SQLException {
        if (idPrestamo <= 0) {
            throw new IllegalArgumentException("El identificador del préstamo no es válido.");
        }
        if (!Sesion.esBibliotecario()) {
            Estudiante actual = new ControladorEstudiantes().obtenerEstudianteActual();
            if (!prestamoDAO.perteneceAEstudiante(idPrestamo, actual.getId())) {
                throw new SecurityException("Solo puedes devolver préstamos a tu nombre.");
            }
        }
        return prestamoDAO.registrarDevolucion(idPrestamo);
    }

    public List<Prestamo> listarPrestamosPendientes() throws SQLException {
        verificarBibliotecario();
        return prestamoDAO.listarPendientes();
    }

    public List<PrestamoDetalle> listarPrestamosDetallados() throws SQLException {
        if (Sesion.esBibliotecario()) {
            return prestamoDAO.listarPendientesDetallados();
        }
        Estudiante actual = new ControladorEstudiantes().obtenerEstudianteActual();
        return prestamoDAO.listarPendientesDeEstudiante(actual.getId());
    }
}