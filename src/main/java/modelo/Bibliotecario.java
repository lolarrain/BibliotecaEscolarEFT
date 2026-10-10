package modelo;

/**
 * Representa al personal bibliotecario.
 * Hereda los atributos comunes de Usuario.
 */
public class Bibliotecario extends Usuario {

    public Bibliotecario(int id, String nombre, String rut, String correo) {
        super(id, nombre, rut, correo);
    }

    /**
     * Devuelve el rol del usuario bibliotecario.
     */
    @Override
    public String getRol() {
        return "bibliotecario";
    }
}

