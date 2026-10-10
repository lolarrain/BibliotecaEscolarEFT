package controlador;

import config.Sesion;
import dao.CategoriaDAO;
import dao.impl.CategoriaDAOImpl;
import modelo.Categoria;

import java.sql.SQLException;
import java.util.List;

/**
 * Coordina la gestión de categorías y sus permisos.
 */
public class ControladorCategorias {

    private final CategoriaDAO categoriaDAO;

    public ControladorCategorias() {
        this.categoriaDAO = new CategoriaDAOImpl();
    }

    public List<Categoria> listarCategorias() throws SQLException {
        return categoriaDAO.listar();
    }

    public void agregarCategoria(Categoria categoria)
            throws SQLException {

        verificarPermisos();
        validarCategoria(categoria);

        categoriaDAO.agregar(categoria);
    }

    public boolean actualizarCategoria(Categoria categoria)
            throws SQLException {

        verificarPermisos();
        validarCategoria(categoria);

        if (categoria.getId() <= 0) {
            throw new IllegalArgumentException(
                    "El identificador de la categoría no es válido."
            );
        }

        return categoriaDAO.actualizar(categoria);
    }

    public boolean eliminarCategoria(int id) throws SQLException {

        verificarPermisos();

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "El identificador de la categoría no es válido."
            );
        }

        return categoriaDAO.eliminar(id);
    }

    private void validarCategoria(Categoria categoria) {

        if (categoria == null
                || categoria.getNombre() == null
                || categoria.getNombre().isBlank()) {

            throw new IllegalArgumentException(
                    "El nombre de la categoría es obligatorio."
            );
        }
    }

    private void verificarPermisos() {

        if (!Sesion.esBibliotecario()) {
            throw new SecurityException(
                    "No tienes permisos para administrar categorías."
            );
        }
    }
}
