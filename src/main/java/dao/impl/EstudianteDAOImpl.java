
package dao.impl;

import config.DatabaseConnection;
import dao.EstudianteDAO;
import modelo.Estudiante;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementa las operaciones de persistencia
 * de estudiantes mediante JDBC.
 */
public class EstudianteDAOImpl implements EstudianteDAO {

    /**
     * Obtiene todos los estudiantes registrados.
     */
    @Override
    public List<Estudiante> listar() throws SQLException {

        List<Estudiante> estudiantes = new ArrayList<>();

        String sql = """
                SELECT id, nombre, rut, curso, correo
                FROM estudiantes
                """;

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement =
                     conexion.prepareStatement(sql);
             ResultSet resultado = statement.executeQuery()) {

            while (resultado.next()) {

                Estudiante estudiante = new Estudiante(
                        resultado.getInt("id"),
                        resultado.getString("nombre"),
                        resultado.getString("rut"),
                        resultado.getString("correo"),
                        resultado.getString("curso")
                );

                estudiantes.add(estudiante);
            }
        }

        return estudiantes;
    }

    /**
     * Registra un estudiante en MySQL.
     */
    @Override
    public void agregar(Estudiante estudiante) throws SQLException {

        String sql = """
                INSERT INTO estudiantes
                (nombre, rut, curso, correo)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement =
                     conexion.prepareStatement(sql)) {

            statement.setString(1, estudiante.getNombre());
            statement.setString(2, estudiante.getRut());
            statement.setString(3, estudiante.getCurso());
            statement.setString(4, estudiante.getCorreo());

            statement.executeUpdate();
        }
    }

    /**
     * Actualiza los datos de un estudiante.
     */
    @Override
    public boolean actualizar(Estudiante estudiante)
            throws SQLException {

        String sql = """
                UPDATE estudiantes
                SET nombre = ?, rut = ?, curso = ?, correo = ?
                WHERE id = ?
                """;

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement =
                     conexion.prepareStatement(sql)) {

            statement.setString(1, estudiante.getNombre());
            statement.setString(2, estudiante.getRut());
            statement.setString(3, estudiante.getCurso());
            statement.setString(4, estudiante.getCorreo());
            statement.setInt(5, estudiante.getId());

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Elimina un estudiante según su identificador.
     */
    @Override
    public boolean eliminar(int id) throws SQLException {

        String sql = "DELETE FROM estudiantes WHERE id = ?";

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement =
                     conexion.prepareStatement(sql)) {

            statement.setInt(1, id);

            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public Estudiante buscarPorRut(String rut) throws SQLException {
        String sql = "SELECT id, nombre, rut, correo, curso FROM estudiantes WHERE rut = ?";
        try (Connection conexion = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setString(1, rut);
            try (ResultSet resultado = statement.executeQuery()) {
                if (resultado.next()) {
                    return new Estudiante(resultado.getInt("id"),
                            resultado.getString("nombre"),
                            resultado.getString("rut"),
                            resultado.getString("correo"),
                            resultado.getString("curso"));
                }
            }
        }
        return null;
    }
}
