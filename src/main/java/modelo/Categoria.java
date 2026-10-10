package modelo;

/**
 * Representa una categoría de libros de la biblioteca.
 */
public class Categoria {

    private int id;
    private String nombre;

    public Categoria(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Devuelve el nombre de la categoría.
     */
    @Override
    public String toString() {
        return nombre;
    }
}