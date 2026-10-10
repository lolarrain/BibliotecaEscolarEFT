package controlador;

import config.Sesion;
import dao.EstudianteDAO;
import dao.impl.EstudianteDAOImpl;
import modelo.Estudiante;

import java.sql.SQLException;
import java.util.List;

/**
 * Controla las operaciones de gestión de estudiantes.
 * Verifica permisos, valida los datos y coordina
 * el acceso a MySQL mediante EstudianteDAO.
 */
public class ControladorEstudiantes {

    private final EstudianteDAO estudianteDAO;

    public ControladorEstudiantes() {
        this.estudianteDAO = new EstudianteDAOImpl();
    }

    /**
     * Obtiene los estudiantes registrados.
     *
     * @return lista de estudiantes.
     * @throws SQLException si ocurre un error en MySQL.
     */
    public List<Estudiante> listarEstudiantes()
            throws SQLException {

        verificarPermisos();

        return estudianteDAO.listar();
    }

    /**
     * Registra un nuevo estudiante.
     *
     * @param estudiante estudiante que se desea registrar.
     * @throws SQLException si ocurre un error en MySQL.
     * @throws IllegalArgumentException si los datos son inválidos.
     * @throws SecurityException si el usuario no está autorizado.
     */
    public void agregarEstudiante(Estudiante estudiante)
            throws SQLException {

        verificarPermisos();

        if (estudiante == null) {
            throw new IllegalArgumentException(
                    "Debes proporcionar un estudiante válido."
            );
        }

        estudiante.validar();

        estudianteDAO.agregar(estudiante);
    }

    /**
     * Actualiza los datos de un estudiante existente.
     *
     * @param estudiante estudiante con los datos actualizados.
     * @return true si se modificó el registro.
     * @throws SQLException si ocurre un error en MySQL.
     * @throws IllegalArgumentException si los datos son inválidos.
     * @throws SecurityException si el usuario no está autorizado.
     */
    public boolean actualizarEstudiante(Estudiante estudiante)
            throws SQLException {

        verificarPermisos();

        if (estudiante == null) {
            throw new IllegalArgumentException(
                    "Debes proporcionar un estudiante válido."
            );
        }

        if (estudiante.getId() <= 0) {
            throw new IllegalArgumentException(
                    "El identificador del estudiante no es válido."
            );
        }

        estudiante.validar();

        return estudianteDAO.actualizar(estudiante);
    }

    /**
     * Elimina un estudiante según su identificador.
     *
     * @param id identificador del estudiante.
     * @return true si se eliminó el registro.
     * @throws SQLException si ocurre un error en MySQL.
     * @throws IllegalArgumentException si el ID es inválido.
     * @throws SecurityException si el usuario no está autorizado.
     */
    public boolean eliminarEstudiante(int id)
            throws SQLException {

        verificarPermisos();

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "El identificador del estudiante no es válido."
            );
        }

        return estudianteDAO.eliminar(id);
    }

    /**
     * Comprueba que exista una sesión con rol bibliotecario.
     *
     * @throws SecurityException si el usuario no está autorizado.
     */
    private void verificarPermisos() {

        if (!Sesion.esBibliotecario()) {
            throw new SecurityException(
                    "No tienes permisos para administrar estudiantes."
            );
        }
    }

    /**
     * Localiza el perfil de biblioteca correspondiente a la cuenta autenticada.
     * No permite consultar libremente datos personales de otros estudiantes.
     */
    public Estudiante obtenerEstudianteActual() throws SQLException {
        if (Sesion.getUsuarioActual() == null
                || !"estudiante".equalsIgnoreCase(Sesion.getUsuarioActual().getRol())) {
            throw new SecurityException("Debes iniciar sesión como estudiante.");
        }
        Estudiante estudiante = estudianteDAO.buscarPorRut(Sesion.getUsuarioActual().getRut());
        if (estudiante == null) {
            throw new IllegalArgumentException(
                    "Tu cuenta no tiene un registro de estudiante asociado al mismo RUT.");
        }
        return estudiante;
    }
}