package modelo;

/**
 * Representa a un estudiante registrado en la biblioteca.
 * Hereda los atributos comunes de Usuario e implementa
 * Validable para comprobar sus datos obligatorios.
 */
public class Estudiante extends Usuario implements Validable {

    private String curso;

    public Estudiante(int id, String nombre, String rut,
                      String correo, String curso) {

        super(id, nombre, rut, correo);
        this.curso = curso;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    /**
     * Valida los datos obligatorios del estudiante.
     *
     * @throws IllegalArgumentException si algún dato es inválido.
     */
    @Override
    public void validar() {

        if (getNombre() == null || getNombre().isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del estudiante es obligatorio."
            );
        }

        if (getRut() == null || getRut().isBlank()) {
            throw new IllegalArgumentException(
                    "El RUT del estudiante es obligatorio."
            );
        }

        if (curso == null || curso.isBlank()) {
            throw new IllegalArgumentException(
                    "El curso del estudiante es obligatorio."
            );
        }

        if (getCorreo() == null || getCorreo().isBlank()) {
            throw new IllegalArgumentException(
                    "El correo del estudiante es obligatorio."
            );
        }
    }

    /**
     * Devuelve el rol correspondiente al estudiante.
     */
    @Override
    public String getRol() {
        return "estudiante";
    }

    /**
     * Representación del estudiante en componentes Swing.
     */
    @Override
    public String toString() {
        return getNombre() + " - " + curso;
    }
}