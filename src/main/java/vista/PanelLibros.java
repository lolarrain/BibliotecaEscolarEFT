package vista;

import controlador.ControladorLibros;
import modelo.Libro;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import dao.impl.EstudianteDAOImpl;

/**
 * Panel de gestión del inventario de libros.
 * Permite consultar el catálogo y restringe su edición
 * según los permisos del usuario.
 */
public class PanelLibros extends JPanel {

    private final ControladorLibros controlador;
    private final JTable tablaLibros;
    private final DefaultTableModel modeloTabla;
    private final boolean esBibliotecario;

    /**
     * Crea el panel de libros según el rol del usuario.
     *
     * @param esBibliotecario true si puede administrar libros.
     */
    public PanelLibros(boolean esBibliotecario) {

        this.esBibliotecario = esBibliotecario;
        controlador = new ControladorLibros();

        setLayout(new BorderLayout(10, 10));
        setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        // Título del módulo.
        JLabel titulo = new JLabel(
                esBibliotecario
                        ? "Inventario de libros"
                        : "Catálogo de libros"
        );

        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        add(titulo, BorderLayout.NORTH);

        // Configuración de la tabla.
        String[] columnas = {
                "ID", "Título", "Autor", "ISBN",
                "Editorial", "Stock", "Categoría"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {

            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columna) {
                if (columna == 0 || columna == 5 || columna == 6) {
                    return Integer.class;
                }
                return String.class;
            }
        };

        tablaLibros = new JTable(modeloTabla);

        tablaLibros.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tablaLibros.setRowHeight(25);
        tablaLibros.setAutoCreateRowSorter(true);
        EstiloTablas.aplicar(tablaLibros);
        tablaLibros.setFillsViewportHeight(true);

        add(new JScrollPane(tablaLibros), BorderLayout.CENTER);

        // Botones del módulo.
        JButton btnAgregar = new JButton("Agregar libro");
        JButton btnEditar = new JButton("Editar libro");
        JButton btnEliminar = new JButton("Eliminar libro");
        JButton btnActualizar = new JButton("Actualizar listado");

        btnAgregar.addActionListener(e -> abrirRegistroLibro());
        btnEditar.addActionListener(e -> editarLibro());
        btnEliminar.addActionListener(e -> eliminarLibro());
        btnActualizar.addActionListener(e -> cargarLibros());

        JPanel panelInferior = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        // Solo los bibliotecarios pueden modificar el inventario.
        if (esBibliotecario) {
            panelInferior.add(btnAgregar);
            panelInferior.add(btnEditar);
            panelInferior.add(btnEliminar);
        }

        panelInferior.add(btnActualizar);

        add(panelInferior, BorderLayout.SOUTH);

        // Cargar los libros al abrir el módulo.
        cargarLibros();
    }

    /**
     * Recupera los libros desde MySQL y actualiza la tabla.
     */
    private void cargarLibros() {

        try {
            var libros = controlador.listarLibros();

            modeloTabla.setRowCount(0);

            for (Libro libro : libros) {

                modeloTabla.addRow(new Object[]{
                        libro.getId(),
                        libro.getTitulo(),
                        libro.getAutor(),
                        libro.getIsbn(),
                        libro.getEditorial(),
                        libro.getStock(),
                        libro.getIdCategoria()
                });
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "No fue posible cargar los libros.",
                    "Error de base de datos",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Comprueba los permisos de administración.
     *
     * @return true si el usuario puede modificar libros.
     */
    private boolean comprobarPermisos() {

        if (!esBibliotecario) {

            JOptionPane.showMessageDialog(
                    this,
                    "No tienes permisos para modificar el inventario.",
                    "Acceso restringido",
                    JOptionPane.WARNING_MESSAGE
            );

            return false;
        }

        return true;
    }

    /**
     * Abre el formulario para registrar un nuevo libro.
     */
    private void abrirRegistroLibro() {

        if (!comprobarPermisos()) {
            return;
        }

        Window ventanaPadre =
                SwingUtilities.getWindowAncestor(this);

        RegistroLibro formulario =
                new RegistroLibro(ventanaPadre, controlador);

        formulario.setVisible(true);

        if (formulario.isGuardado()) {
            cargarLibros();
        }
    }

    /**
     * Abre el formulario de edición del libro seleccionado.
     */
    private void editarLibro() {

        if (!comprobarPermisos()) {
            return;
        }

        int fila = tablaLibros.getSelectedRow();

        if (fila == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona un libro para editar.",
                    "Sin selección",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Convertir la fila visual al índice del modelo.
        int filaModelo =
                tablaLibros.convertRowIndexToModel(fila);

        // Recuperar los datos del libro seleccionado.
        Libro libro = new Libro(
                (int) modeloTabla.getValueAt(filaModelo, 0),
                (String) modeloTabla.getValueAt(filaModelo, 1),
                (String) modeloTabla.getValueAt(filaModelo, 2),
                (String) modeloTabla.getValueAt(filaModelo, 3),
                (String) modeloTabla.getValueAt(filaModelo, 4),
                (int) modeloTabla.getValueAt(filaModelo, 5),
                (int) modeloTabla.getValueAt(filaModelo, 6)
        );

        Window ventanaPadre =
                SwingUtilities.getWindowAncestor(this);

        RegistroLibro formulario =
                new RegistroLibro(ventanaPadre, controlador, libro);

        formulario.setVisible(true);

        // Actualizar el inventario si se guardaron cambios.
        if (formulario.isGuardado()) {
            cargarLibros();
        }
    }

    /**
     * Elimina el libro seleccionado, previa confirmación.
     */
    private void eliminarLibro() {

        if (!comprobarPermisos()) {
            return;
        }

        int fila = tablaLibros.getSelectedRow();

        if (fila == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona un libro para eliminar.",
                    "Sin selección",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int filaModelo =
                tablaLibros.convertRowIndexToModel(fila);

        int id = (int) modeloTabla.getValueAt(filaModelo, 0);
        String titulo =
                (String) modeloTabla.getValueAt(filaModelo, 1);

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas eliminar el libro \"" + titulo + "\"?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean eliminado = controlador.eliminarLibro(id);

            if (eliminado) {

                JOptionPane.showMessageDialog(
                        this,
                        "Libro eliminado correctamente."
                );

                cargarLibros();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No se encontró el libro seleccionado.",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE
                );
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "No fue posible eliminar el libro. "
                            + "Comprueba si tiene préstamos asociados.",
                    "Error de base de datos",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}