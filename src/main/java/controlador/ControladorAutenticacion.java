package controlador;

import dao.UsuarioDAO;
import dao.impl.UsuarioDAOImpl;
import modelo.Usuario;

import java.sql.SQLException;

/**
 * Controla la autenticación de usuarios.
 * Valida las credenciales y delega el acceso
 * a la base de datos mediante UsuarioDAO.
 */
public class ControladorAutenticacion {

    private final UsuarioDAO usuarioDAO;

    public ControladorAutenticacion() {
        this.usuarioDAO = new UsuarioDAOImpl();
    }

    /**
     * Verifica las credenciales ingresadas.
     *
     * @param correo correo electrónico del usuario.
     * @param contrasena contraseña ingresada.
     * @return usuario autenticado o null si las credenciales son incorrectas.
     * @throws SQLException si ocurre un error de conexión o consulta.
     */
    public Usuario iniciarSesion(String correo, String contrasena)
            throws SQLException {

        if (correo == null || correo.isBlank()
                || contrasena == null || contrasena.isBlank()) {

            throw new IllegalArgumentException(
                    "Debes ingresar tu correo y contraseña."
            );
        }

        return usuarioDAO.autenticar(
                correo.trim(),
                contrasena
        );
    }
}