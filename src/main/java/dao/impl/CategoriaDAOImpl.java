package dao.impl;

import config.DatabaseConnection;
import dao.CategoriaDAO;
import modelo.Categoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementa la persistencia de categorías mediante JDBC.
 */
public class CategoriaDAOImpl implements CategoriaDAO {

    @Override
    public List<Categoria> listar() throws SQLException {

        List<Categoria> categorias = new ArrayList<>();

        String sql = "SELECT id, nombre FROM categorias";

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement =
                     conexion.prepareStatement(sql);
             ResultSet resultado = statement.executeQuery()) {

            while (resultado.next()) {
                categorias.add(new Categoria(
                        resultado.getInt("id"),
                        resultado.getString("nombre")
                ));
            }
        }

        return categorias;
    }

    @Override
    public void agregar(Categoria categoria) throws SQLException {

        String sql = "INSERT INTO categorias (nombre) VALUES (?)";

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement =
                     conexion.prepareStatement(sql)) {

            statement.setString(1, categoria.getNombre());
            statement.executeUpdate();
        }
    }

    @Override
    public boolean actualizar(Categoria categoria)
            throws SQLException {

        String sql = """
                UPDATE categorias
                SET nombre = ?
                WHERE id = ?
                """;

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement =
                     conexion.prepareStatement(sql)) {

            statement.setString(1, categoria.getNombre());
            statement.setInt(2, categoria.getId());

            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean eliminar(int id) throws SQLException {

        String sql = "DELETE FROM categorias WHERE id = ?";

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement =
                     conexion.prepareStatement(sql)) {

            statement.setInt(1, id);

            return statement.executeUpdate() > 0;
        }
    }
}