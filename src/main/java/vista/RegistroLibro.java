package vista;

import controlador.ControladorCategorias;
import controlador.ControladorLibros;
import modelo.Categoria;
import modelo.Libro;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * Formulario para registrar y editar libros.
 * Utiliza controladores para gestionar las operaciones
 * y GestorErrores para presentar las excepciones.
 */
public class RegistroLibro extends JDialog {

    private final JTextField txtTitulo = new JTextField();
    private final JTextField txtAutor = new JTextField();
    private final JTextField txtIsbn = new JTextField();
    private final JTextField txtEditorial = new JTextField();

    private final JSpinner spStock = new JSpinner(
            new SpinnerNumberModel(0, 0, 100000, 1)
    );

    private final JComboBox<Categoria> cbCategoria =
            new JComboBox<>();

    private final ControladorLibros controlador;
    private final ControladorCategorias controladorCategorias;

    private Libro libroEditar;
    private boolean guardado = false;

    /**
     * Constructor para registrar un nuevo libro.
     */
    public RegistroLibro(Window ventanaPadre,
                         ControladorLibros controlador) {

        super(
                ventanaPadre,
                "Registrar libro",
                ModalityType.APPLICATION_MODAL
        );

        this.controlador = controlador;
        this.controladorCategorias = new ControladorCategorias();

        setSize(480, 370);
        setLocationRelativeTo(ventanaPadre);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        setLayout(new BorderLayout(10, 10));

        // Formulario de datos.
        JPanel formulario = new JPanel(
                new GridLayout(6, 2, 10, 10)
        );

        formulario.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 10, 20)
        );

        formulario.add(new JLabel("Título:"));
        formulario.add(txtTitulo);

        formulario.add(new JLabel("Autor:"));
        formulario.add(txtAutor);

        formulario.add(new JLabel("ISBN:"));
        formulario.add(txtIsbn);

        formulario.add(new JLabel("Editorial:"));
        formulario.add(txtEditorial);

        formulario.add(new JLabel("Stock:"));
        formulario.add(spStock);

        formulario.add(new JLabel("Categoría:"));
        formulario.add(cbCategoria);

        add(formulario, BorderLayout.CENTER);

        // Botones.
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        JPanel panelBotones = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        add(panelBotones, BorderLayout.SOUTH);

        btnGuardar.addActionListener(e -> guardarLibro());
        btnCancelar.addActionListener(e -> dispose());

        getRootPane().setDefaultButton(btnGuardar);

        cargarCategorias();
    }

    /**
     * Constructor para editar un libro existente.
     */
    public RegistroLibro(Window ventanaPadre,
                         ControladorLibros controlador,
                         Libro libro) {

        this(ventanaPadre, controlador);

        this.libroEditar = libro;

        setTitle("Editar libro");

        txtTitulo.setText(libro.getTitulo());
        txtAutor.setText(libro.getAutor());
        txtIsbn.setText(libro.getIsbn());
        txtEditorial.setText(libro.getEditorial());
        spStock.setValue(libro.getStock());

        // Seleccionar la categoría actual del libro.
        for (int i = 0; i < cbCategoria.getItemCount(); i++) {

            Categoria categoria = cbCategoria.getItemAt(i);

            if (categoria.getId() == libro.getIdCategoria()) {
                cbCategoria.setSelectedIndex(i);
                break;
            }
        }
    }

    /**
     * Recupera las categorías a través del controlador.
     */
    private void cargarCategorias() {

        try {
            cbCategoria.removeAllItems();

            for (Categoria categoria :
                    controladorCategorias.listarCategorias()) {

                cbCategoria.addItem(categoria);
            }

            if (cbCategoria.getItemCount() == 0) {
                GestorErrores.mostrar(
                        this,
                        new IllegalArgumentException(
                                "No existen categorías registradas."
                        )
                );
            }

        } catch (SQLException e) {
            GestorErrores.mostrar(this, e);
        }
    }

    /**
     * Registra o actualiza un libro.
     * Las validaciones se ejecutan desde el modelo
     * mediante ControladorLibros.
     */
    private void guardarLibro() {

        try {
            Categoria categoria =
                    (Categoria) cbCategoria.getSelectedItem();

            int idCategoria = categoria == null
                    ? 0
                    : categoria.getId();

            int id = libroEditar == null
                    ? 0
                    : libroEditar.getId();

            Libro libro = new Libro(
                    id,
                    txtTitulo.getText().trim(),
                    txtAutor.getText().trim(),
                    txtIsbn.getText().trim(),
                    txtEditorial.getText().trim(),
                    (Integer) spStock.getValue(),
                    idCategoria
            );

            if (libroEditar == null) {
                controlador.agregarLibro(libro);
            } else {
                controlador.actualizarLibro(libro);
            }

            guardado = true;

            JOptionPane.showMessageDialog(
                    this,
                    libroEditar == null
                            ? "Libro registrado correctamente."
                            : "Libro actualizado correctamente.",
                    "Operación exitosa",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dispose();

        } catch (IllegalArgumentException | SecurityException
                 | SQLException e) {

            GestorErrores.mostrar(this, e);
        }
    }

    /**
     * Indica si la operación se realizó correctamente.
     *
     * @return true si el libro fue guardado.
     */
    public boolean isGuardado() {
        return guardado;
    }
}
