package modelo;

/**
 * Representa un usuario del sistema de biblioteca.
 * Define los atributos y comportamientos comunes.
 */
public abstract class Usuario {

    private int id;
    private String nombre;
    private String rut;
    private String correo;
    private String contrasena;

    public Usuario(int id, String nombre, String rut, String correo) {
        this.id = id;
        this.nombre = nombre;
        this.rut = rut;
        this.correo = correo;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRut() {
        return rut;
    }

    public String getCorreo() {
        return correo;
    }

    /**
     * Devuelve el rol correspondiente al usuario.
     */
    public abstract String getRol();


    /**
     * Devuelve la contraseña almacenada del usuario.
     */
    public String getContrasena() {
        return contrasena;
    }

    /**
     * Establece la contraseña del usuario.
     */
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}

