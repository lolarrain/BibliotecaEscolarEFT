package dao;

import modelo.Estudiante;

import java.sql.SQLException;
import java.util.List;

/**
 * Define las operaciones de persistencia
 * disponibles para los estudiantes.
 *
 * Su implementación JDBC corresponde a EstudianteDAOImpl.
 */
public interface EstudianteDAO {

    /**
     * Obtiene todos los estudiantes registrados.
     *
     * @return lista de estudiantes.
     * @throws SQLException si ocurre un error de base de datos.
     */
    List<Estudiante> listar() throws SQLException;

    Estudiante buscarPorRut(String rut) throws SQLException;

    /**
     * Registra un nuevo estudiante.
     *
     * @param estudiante estudiante que se desea registrar.
     * @throws SQLException si ocurre un error de base de datos.
     */
    void agregar(Estudiante estudiante) throws SQLException;

    /**
     * Actualiza los datos de un estudiante existente.
     *
     * @param estudiante estudiante con los datos actualizados.
     * @return true si se actualizó el registro.
     * @throws SQLException si ocurre un error de base de datos.
     */
    boolean actualizar(Estudiante estudiante) throws SQLException;

    /**
     * Elimina un estudiante por su identificador.
     *
     * @param id identificador del estudiante.
     * @return true si se eliminó el registro.
     * @throws SQLException si ocurre un error de base de datos.
     */
    boolean eliminar(int id) throws SQLException;
}