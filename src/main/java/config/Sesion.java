package config;

import modelo.Usuario;

/**
 * Administra la sesión del usuario autenticado.
 */
public final class Sesion {

    private static Usuario usuarioActual;

    private Sesion() {
        // Evita crear instancias de esta clase.
    }

    /**
     * Establece el usuario autenticado.
     */
    public static void iniciar(Usuario usuario) {

        if (usuario == null) {
            throw new IllegalArgumentException(
                    "El usuario no puede ser nulo."
            );
        }

        usuarioActual = usuario;
    }

    /**
     * Obtiene el usuario de la sesión actual.
     */
    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    /**
     * Comprueba si el usuario es bibliotecario.
     */
    public static boolean esBibliotecario() {

        return usuarioActual != null
                && "bibliotecario".equalsIgnoreCase(
                usuarioActual.getRol()
        );
    }

    /**
     * Finaliza la sesión.
     */
    public static void cerrar() {
        usuarioActual = null;
    }
}