package dao;

import modelo.Usuario;

import java.sql.SQLException;

/**
 * Define las operaciones de acceso y autenticación
 * de usuarios registrados.
 */
public interface UsuarioDAO {

    /**
     * Busca un usuario mediante su correo electrónico.
     *
     * @return usuario encontrado o null si no existe.
     */
    Usuario buscarPorCorreo(String correo) throws SQLException;

    /**
     * Verifica las credenciales de acceso.
     *
     * @return usuario autenticado o null si son incorrectas.
     */
    Usuario autenticar(String correo, String contrasena)
            throws SQLException;
}

