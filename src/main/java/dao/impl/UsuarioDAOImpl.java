package dao.impl;

import config.DatabaseConnection;
import dao.UsuarioDAO;
import modelo.Bibliotecario;
import modelo.Estudiante;
import modelo.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implementa la consulta y autenticación de usuarios
 * mediante JDBC.
 */
public class UsuarioDAOImpl implements UsuarioDAO {

    /**
     * Busca una cuenta por correo electrónico.
     */
    @Override
    public Usuario buscarPorCorreo(String correo) throws SQLException {

        String sql = "SELECT * FROM usuarios WHERE correo = ?";

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement =
                     conexion.prepareStatement(sql)) {

            statement.setString(1, correo);

            try (ResultSet resultado = statement.executeQuery()) {

                if (resultado.next()) {

                    int id = resultado.getInt("id");
                    String nombre = resultado.getString("nombre");
                    String rut = resultado.getString("rut");
                    String correoUsuario = resultado.getString("correo");
                    String rol = resultado.getString("rol");

                    Usuario usuario;

                    if ("bibliotecario".equalsIgnoreCase(rol)) {

                        usuario = new Bibliotecario(
                                id, nombre, rut, correoUsuario
                        );

                    } else if ("estudiante".equalsIgnoreCase(rol)) {

                        usuario = new Estudiante(
                                id, nombre, rut, correoUsuario, ""
                        );

                    } else {

                        throw new SQLException(
                                "Rol de usuario no reconocido: " + rol
                        );
                    }

                    usuario.setContrasena(
                            resultado.getString("contraseña")
                    );

                    return usuario;
                }
            }
        }

        return null;
    }

    /**
     * Autentica las credenciales y evita conservar
     * la contraseña en el usuario autenticado.
     */
    @Override
    public Usuario autenticar(String correo, String contrasena)
            throws SQLException {

        Usuario usuario = buscarPorCorreo(correo);

        if (usuario != null
                && usuario.getContrasena() != null
                && usuario.getContrasena().equals(contrasena)) {

            usuario.setContrasena(null);
            return usuario;
        }

        return null;
    }
}
