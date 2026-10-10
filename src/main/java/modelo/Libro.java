package modelo;

/**
 * Representa un libro del inventario de la biblioteca.
 * Implementa Validable para comprobar sus reglas de negocio.
 */
public class Libro implements Validable {

    private int id;
    private String titulo;
    private String autor;
    private String isbn;
    private String editorial;
    private int stock;
    private int idCategoria;

    /**
     * Construye un libro con sus datos principales.
     */
    public Libro(int id, String titulo, String autor,
                 String isbn, String editorial,
                 int stock, int idCategoria) {

        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.editorial = editorial;
        setStock(stock);
        this.idCategoria = idCategoria;
    }

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getEditorial() {
        return editorial;
    }

    public int getStock() {
        return stock;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    /**
     * Modifica el stock sin permitir valores negativos.
     */
    public void setStock(int stock) {

        if (stock < 0) {
            throw new IllegalArgumentException(
                    "El stock no puede ser negativo."
            );
        }

        this.stock = stock;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    /**
     * Valida los datos obligatorios del libro.
     *
     * @throws IllegalArgumentException si los datos son inválidos.
     */
    @Override
    public void validar() {

        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException(
                    "El título del libro es obligatorio."
            );
        }

        if (autor == null || autor.isBlank()) {
            throw new IllegalArgumentException(
                    "El autor del libro es obligatorio."
            );
        }

        if (isbn == null || isbn.isBlank()) {
            throw new IllegalArgumentException(
                    "El ISBN del libro es obligatorio."
            );
        }

        if (editorial == null || editorial.isBlank()) {
            throw new IllegalArgumentException(
                    "La editorial del libro es obligatoria."
            );
        }

        if (stock < 0) {
            throw new IllegalArgumentException(
                    "El stock no puede ser negativo."
            );
        }

        if (idCategoria <= 0) {
            throw new IllegalArgumentException(
                    "Debes seleccionar una categoría válida."
            );
        }
    }

    /**
     * Representación del libro en componentes Swing,
     * especialmente en listas desplegables.
     */
    @Override
    public String toString() {
        return titulo + " (Stock: " + stock + ")";
    }
}