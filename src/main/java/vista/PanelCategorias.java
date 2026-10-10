package vista;

import controlador.ControladorCategorias;
import modelo.Categoria;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

/** Administración de categorías con operaciones CRUD. */
public class PanelCategorias extends JPanel {

    private final ControladorCategorias controlador = new ControladorCategorias();
    private final DefaultTableModel modelo = new DefaultTableModel(new String[]{"ID", "Categoría"}, 0) {
        @Override public boolean isCellEditable(int fila, int columna) { return false; }
    };
    private final JTable tabla = new JTable(modelo);
    private final JButton btnAgregar = new JButton("Agregar");
    private final JButton btnEditar = new JButton("Editar");
    private final JButton btnEliminar = new JButton("Eliminar");
    private final JButton btnActualizar = new JButton("Actualizar");

    public PanelCategorias() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JLabel titulo = new JLabel("Administración de categorías");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        add(titulo, BorderLayout.NORTH);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoCreateRowSorter(true);
        EstiloTablas.aplicar(tabla);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnAgregar);
        botones.add(btnEditar);
        botones.add(btnEliminar);
        botones.add(btnActualizar);
        add(botones, BorderLayout.SOUTH);

        btnAgregar.addActionListener(e -> agregar());
        btnEditar.addActionListener(e -> editar());
        btnEliminar.addActionListener(e -> eliminar());
        btnActualizar.addActionListener(e -> cargar());
        cargar();
    }

    private Integer idSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona una categoría.");
            return null;
        }
        return (Integer) modelo.getValueAt(tabla.convertRowIndexToModel(fila), 0);
    }

    private void agregar() {
        String nombre = JOptionPane.showInputDialog(this, "Nombre de la categoría:", "Agregar categoría",
                JOptionPane.PLAIN_MESSAGE);
        if (nombre == null) return;
        ejecutarCambio(() -> controlador.agregarCategoria(new Categoria(0, nombre.trim())));
    }

    private void editar() {
        Integer id = idSeleccionado();
        if (id == null) return;
        int fila = tabla.convertRowIndexToModel(tabla.getSelectedRow());
        String actual = (String) modelo.getValueAt(fila, 1);
        String nombre = (String) JOptionPane.showInputDialog(this, "Nombre de la categoría:",
                "Editar categoría", JOptionPane.PLAIN_MESSAGE, null, null, actual);
        if (nombre == null) return;
        ejecutarCambio(() -> {
            if (!controlador.actualizarCategoria(new Categoria(id, nombre.trim()))) {
                throw new IllegalArgumentException("La categoría ya no existe.");
            }
        });
    }

    private void eliminar() {
        Integer id = idSeleccionado();
        if (id == null) return;
        int respuesta = JOptionPane.showConfirmDialog(this, "¿Eliminar la categoría seleccionada?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) return;
        ejecutarCambio(() -> {
            if (!controlador.eliminarCategoria(id)) {
                throw new IllegalArgumentException("La categoría ya no existe.");
            }
        });
    }

    @FunctionalInterface
    private interface Operacion { void ejecutar() throws Exception; }

    private void ejecutarCambio(Operacion operacion) {
        activar(false);
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception {
                operacion.ejecutar();
                return null;
            }
            @Override protected void done() {
                try {
                    get();
                    cargar();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    GestorErrores.mostrar(PanelCategorias.this, e);
                    activar(true);
                } catch (ExecutionException e) {
                    GestorErrores.mostrar(PanelCategorias.this, e);
                    activar(true);
                }
            }
        }.execute();
    }

    private void cargar() {
        activar(false);
        new SwingWorker<List<Categoria>, Void>() {
            @Override protected List<Categoria> doInBackground() throws Exception {
                return controlador.listarCategorias();
            }
            @Override protected void done() {
                try {
                    modelo.setRowCount(0);
                    for (Categoria categoria : get()) {
                        modelo.addRow(new Object[]{categoria.getId(), categoria.getNombre()});
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    GestorErrores.mostrar(PanelCategorias.this, e);
                } catch (ExecutionException e) {
                    GestorErrores.mostrar(PanelCategorias.this, e);
                } finally {
                    activar(true);
                }
            }
        }.execute();
    }

    private void activar(boolean estado) {
        btnAgregar.setEnabled(estado);
        btnEditar.setEnabled(estado);
        btnEliminar.setEnabled(estado);
        btnActualizar.setEnabled(estado);
    }
}