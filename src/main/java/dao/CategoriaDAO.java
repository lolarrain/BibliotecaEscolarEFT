package dao;

import modelo.Categoria;
import java.sql.SQLException;
import java.util.List;

/**
 * Define las operaciones de persistencia de categorías.
 */
public interface CategoriaDAO {

    List<Categoria> listar() throws SQLException;

    void agregar(Categoria categoria) throws SQLException;

    boolean actualizar(Categoria categoria) throws SQLException;

    boolean eliminar(int id) throws SQLException;
}