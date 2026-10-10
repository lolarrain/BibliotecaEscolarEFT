package dao;

import modelo.Libro;

import java.sql.SQLException;
import java.util.List;

/**
 * Define las operaciones de persistencia de libros.
 */
public interface LibroDAO {

    /**
     * Obtiene todos los libros registrados.
     */
    List<Libro> listar() throws SQLException;

    /**
     * Registra un nuevo libro.
     */
    void agregar(Libro libro) throws SQLException;

    /**
     * Actualiza los datos de un libro existente.
     */
    void actualizar(Libro libro) throws SQLException;

    /**
     * Elimina un libro según su identificador.
     *
     * @return true si se eliminó el registro.
     */
    boolean eliminar(int id) throws SQLException;
}
