package modelo;

/**
 * Define un contrato para validar las reglas
 * de negocio de las entidades del sistema.
 */
public interface Validable {

    /**
     * Comprueba que los datos de la entidad sean válidos.
     *
     * @throws IllegalArgumentException si alguna regla
     * de validación no se cumple.
     */
    void validar();
}