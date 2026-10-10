package vista;

import javax.swing.*;
import java.awt.Component;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

/**
 * Centraliza la presentación de errores de la aplicación.
 */
public final class GestorErrores {

    private GestorErrores() {
        // Evita crear instancias.
    }

    /**
     * Muestra un mensaje comprensible según el tipo de error.
     *
     * @param padre componente desde el que se muestra el diálogo.
     * @param error excepción producida.
     */
    public static void mostrar(Component padre, Throwable error) {

        Throwable causa = obtenerCausa(error);
        String mensaje;
        String titulo;

        if (causa instanceof SecurityException) {

            titulo = "Acceso denegado";
            mensaje = causa.getMessage();

        } else if (causa instanceof IllegalArgumentException) {

            titulo = "Datos inválidos";
            mensaje = causa.getMessage();

        } else if (causa instanceof SQLException sqlError) {

            titulo = "Error de base de datos";
            mensaje = interpretarSql(sqlError);

        } else {

            titulo = "Error inesperado";
            mensaje = "Ocurrió un problema al realizar la operación.";
        }

        JOptionPane.showMessageDialog(
                padre,
                mensaje,
                titulo,
                JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * Recupera la causa original de una operación asíncrona.
     */
    private static Throwable obtenerCausa(Throwable error) {

        while (error instanceof ExecutionException
                && error.getCause() != null) {

            error = error.getCause();
        }

        return error;
    }

    /**
     * Traduce errores habituales de MySQL.
     */
    private static String interpretarSql(SQLException error) {

        String estado = error.getSQLState();

        if (estado == null) {
            return "No fue posible completar la operación en la base de datos.";
        }

        // Violación de una restricción única.
        if ("23000".equals(estado)
                && error.getErrorCode() == 1062) {

            return "Ya existe un registro con esos datos únicos. "
                    + "Comprueba el RUT, correo o ISBN.";
        }

        // Registro relacionado con otros datos.
        if (error.getErrorCode() == 1451) {

            return "No es posible eliminar este registro "
                    + "porque tiene información asociada.";
        }

        // Referencia a un registro inexistente.
        if (error.getErrorCode() == 1452) {

            return "La operación hace referencia a un registro "
                    + "que no existe.";
        }

        // Problemas de conexión.
        if (estado.startsWith("08")) {

            return "No fue posible conectar con MySQL. "
                    + "Comprueba que el servidor esté disponible.";
        }

        return "No fue posible completar la operación "
                + "en la base de datos.";
    }
}
