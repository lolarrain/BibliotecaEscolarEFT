
package controlador;

import config.Sesion;
import dao.LibroDAO;
import dao.impl.LibroDAOImpl;
import modelo.Libro;

import java.sql.SQLException;
import java.util.List;

/**
 * Controla la gestión de libros, sus validaciones
 * y el acceso a datos mediante LibroDAO.
 */
public class ControladorLibros {

    private final LibroDAO libroDAO;

    public ControladorLibros() {
        this.libroDAO = new LibroDAOImpl();
    }

    /**
     * Comprueba los permisos de administración.
     */
    private void verificarPermisos() {
        if (!Sesion.esBibliotecario()) {
            throw new SecurityException(
                    "No tienes permisos para modificar libros."
            );
        }
    }

    /**
     * Obtiene todos los libros registrados.
     */
    public List<Libro> listarLibros() throws SQLException {
        return libroDAO.listar();
    }

    /**
     * Registra un nuevo libro.
     */
    public void agregarLibro(Libro libro) throws SQLException {
        verificarPermisos();

        if (libro == null) {
            throw new IllegalArgumentException(
                    "Debes proporcionar un libro válido."
            );
        }

        libro.validar();
        libroDAO.agregar(libro);
    }

    /**
     * Actualiza un libro existente.
     */
    public void actualizarLibro(Libro libro) throws SQLException {
        verificarPermisos();

        if (libro == null) {
            throw new IllegalArgumentException(
                    "Debes proporcionar un libro válido."
            );
        }

        if (libro.getId() <= 0) {
            throw new IllegalArgumentException(
                    "El identificador del libro no es válido."
            );
        }

        libro.validar();
        libroDAO.actualizar(libro);
    }

    /**
     * Elimina un libro según su identificador.
     */
    public boolean eliminarLibro(int id) throws SQLException {
        verificarPermisos();

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "El identificador del libro no es válido."
            );
        }

        return libroDAO.eliminar(id);
    }
}
