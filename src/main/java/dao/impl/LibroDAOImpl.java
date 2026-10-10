package dao.impl;

import config.DatabaseConnection;
import dao.LibroDAO;
import modelo.Libro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementa la persistencia de libros mediante JDBC.
 */
public class LibroDAOImpl implements LibroDAO {

    /**
     * Consulta todos los libros registrados.
     */
    @Override
    public List<Libro> listar() throws SQLException {

        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT * FROM libros";

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement =
                     conexion.prepareStatement(sql);
             ResultSet resultado = statement.executeQuery()) {

            while (resultado.next()) {

                Libro libro = new Libro(
                        resultado.getInt("id"),
                        resultado.getString("titulo"),
                        resultado.getString("autor"),
                        resultado.getString("isbn"),
                        resultado.getString("editorial"),
                        resultado.getInt("stock"),
                        resultado.getInt("id_categoria")
                );

                libros.add(libro);
            }
        }

        return libros;
    }

    /**
     * Registra un nuevo libro.
     */
    @Override
    public void agregar(Libro libro) throws SQLException {

        String sql = """
                INSERT INTO libros
                (titulo, autor, isbn, editorial, stock, id_categoria)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement =
                     conexion.prepareStatement(sql)) {

            statement.setString(1, libro.getTitulo());
            statement.setString(2, libro.getAutor());
            statement.setString(3, libro.getIsbn());
            statement.setString(4, libro.getEditorial());
            statement.setInt(5, libro.getStock());
            statement.setInt(6, libro.getIdCategoria());

            statement.executeUpdate();
        }
    }

    /**
     * Actualiza los datos de un libro existente.
     */
    @Override
    public void actualizar(Libro libro) throws SQLException {

        String sql = """
                UPDATE libros
                SET titulo = ?, autor = ?, isbn = ?,
                    editorial = ?, stock = ?, id_categoria = ?
                WHERE id = ?
                """;

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement =
                     conexion.prepareStatement(sql)) {

            statement.setString(1, libro.getTitulo());
            statement.setString(2, libro.getAutor());
            statement.setString(3, libro.getIsbn());
            statement.setString(4, libro.getEditorial());
            statement.setInt(5, libro.getStock());
            statement.setInt(6, libro.getIdCategoria());
            statement.setInt(7, libro.getId());

            statement.executeUpdate();
        }
    }

    /**
     * Elimina un libro por su identificador.
     */
    @Override
    public boolean eliminar(int id) throws SQLException {

        String sql = "DELETE FROM libros WHERE id = ?";

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement =
                     conexion.prepareStatement(sql)) {

            statement.setInt(1, id);

            return statement.executeUpdate() > 0;
        }
    }
}
