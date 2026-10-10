package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Gestiona las conexiones con MySQL.
 * Aplica Singleton y obtiene las credenciales
 * desde un archivo .env local.
 */
public class DatabaseConnection {

    private static DatabaseConnection instance;

    private DatabaseConnection() {
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Establece una conexión utilizando la configuración
     * almacenada en el archivo .env.
     *
     * @return conexión activa con MySQL.
     * @throws SQLException si falla la configuración o conexión.
     */
    public Connection getConnection() throws SQLException {

        Properties config = new Properties();

        try (FileInputStream archivo =
                     new FileInputStream(".env")) {

            config.load(archivo);

        } catch (IOException e) {
            throw new SQLException(
                    "No fue posible cargar el archivo .env.",
                    e
            );
        }

        String url = config.getProperty("DB_URL");
        String usuario = config.getProperty("DB_USER");
        String password = config.getProperty("DB_PASSWORD");

        if (url == null || usuario == null || password == null) {
            throw new SQLException(
                    "La configuración de MySQL está incompleta."
            );
        }

        return DriverManager.getConnection(
                url,
                usuario,
                password
        );
    }
}


