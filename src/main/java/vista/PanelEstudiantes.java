package vista;

import controlador.ControladorEstudiantes;
import modelo.Estudiante;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

/**
 * Panel de gestión de estudiantes.
 * Permite consultar, agregar, editar y eliminar estudiantes.
 */
public class PanelEstudiantes extends JPanel {

    private final ControladorEstudiantes controlador;
    private final JTable tablaEstudiantes;
    private final DefaultTableModel modeloTabla;

    public PanelEstudiantes() {

        controlador = new ControladorEstudiantes();

        setLayout(new BorderLayout(10, 10));
        setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        JLabel titulo = new JLabel("Gestión de estudiantes");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        add(titulo, BorderLayout.NORTH);

        String[] columnas = {
                "ID", "Nombre", "RUT", "Curso", "Correo"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {

            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columna) {
                return columna == 0 ? Integer.class : String.class;
            }
        };

        tablaEstudiantes = new JTable(modeloTabla);
        tablaEstudiantes.setAutoCreateRowSorter(true);

        // Aplicar formato visual común.
        EstiloTablas.aplicar(tablaEstudiantes);

        tablaEstudiantes.getColumnModel()
                .getColumn(0).setPreferredWidth(40);
        tablaEstudiantes.getColumnModel()
                .getColumn(1).setPreferredWidth(150);
        tablaEstudiantes.getColumnModel()
                .getColumn(2).setPreferredWidth(110);
        tablaEstudiantes.getColumnModel()
                .getColumn(3).setPreferredWidth(120);
        tablaEstudiantes.getColumnModel()
                .getColumn(4).setPreferredWidth(200);

        add(new JScrollPane(tablaEstudiantes), BorderLayout.CENTER);

        JButton btnAgregar = new JButton("Agregar estudiante");
        JButton btnEditar = new JButton("Editar estudiante");
        JButton btnEliminar = new JButton("Eliminar estudiante");
        JButton btnActualizar = new JButton("Actualizar listado");

        btnAgregar.addActionListener(e -> agregarEstudiante());
        btnEditar.addActionListener(e -> editarEstudiante());
        btnEliminar.addActionListener(e -> eliminarEstudiante());
        btnActualizar.addActionListener(e -> cargarEstudiantes());

        JPanel panelInferior = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        panelInferior.add(btnAgregar);
        panelInferior.add(btnEditar);
        panelInferior.add(btnEliminar);
        panelInferior.add(btnActualizar);

        add(panelInferior, BorderLayout.SOUTH);

        cargarEstudiantes();
    }

    /**
     * Recupera los estudiantes desde MySQL.
     */
    private void cargarEstudiantes() {

        try {
            var estudiantes = controlador.listarEstudiantes();
            modeloTabla.setRowCount(0);

            for (Estudiante estudiante : estudiantes) {

                modeloTabla.addRow(new Object[]{
                        estudiante.getId(),
                        estudiante.getNombre(),
                        estudiante.getRut(),
                        estudiante.getCurso(),
                        estudiante.getCorreo()
                });
            }

        } catch (SQLException e) {
            mostrarError("No fue posible cargar los estudiantes.");
        }
    }

    /**
     * Abre el formulario para registrar un estudiante.
     */
    private void agregarEstudiante() {

        Window ventanaPadre =
                SwingUtilities.getWindowAncestor(this);

        RegistroEstudiante formulario =
                new RegistroEstudiante(ventanaPadre, controlador);

        formulario.setVisible(true);

        if (formulario.isGuardado()) {
            cargarEstudiantes();
        }
    }

    /**
     * Abre el formulario para modificar el estudiante seleccionado.
     */
    private void editarEstudiante() {

        int fila = tablaEstudiantes.getSelectedRow();

        if (fila == -1) {
            mostrarAviso("Selecciona un estudiante para editar.");
            return;
        }

        int filaModelo =
                tablaEstudiantes.convertRowIndexToModel(fila);

        Estudiante estudiante = new Estudiante(
                (int) modeloTabla.getValueAt(filaModelo, 0),
                (String) modeloTabla.getValueAt(filaModelo, 1),
                (String) modeloTabla.getValueAt(filaModelo, 2),
                (String) modeloTabla.getValueAt(filaModelo, 4),
                (String) modeloTabla.getValueAt(filaModelo, 3)
        );

        Window ventanaPadre =
                SwingUtilities.getWindowAncestor(this);

        RegistroEstudiante formulario =
                new RegistroEstudiante(
                        ventanaPadre,
                        controlador,
                        estudiante
                );

        formulario.setVisible(true);

        if (formulario.isGuardado()) {
            cargarEstudiantes();
        }
    }

    /**
     * Elimina el estudiante seleccionado, previa confirmación.
     */
    private void eliminarEstudiante() {

        int fila = tablaEstudiantes.getSelectedRow();

        if (fila == -1) {
            mostrarAviso("Selecciona un estudiante para eliminar.");
            return;
        }

        int filaModelo =
                tablaEstudiantes.convertRowIndexToModel(fila);

        int id = (int) modeloTabla.getValueAt(filaModelo, 0);
        String nombre =
                (String) modeloTabla.getValueAt(filaModelo, 1);

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas eliminar al estudiante \"" + nombre + "\"?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean eliminado = controlador.eliminarEstudiante(id);

            if (eliminado) {
                JOptionPane.showMessageDialog(
                        this,
                        "Estudiante eliminado correctamente."
                );
                cargarEstudiantes();
            } else {
                mostrarAviso("No se encontró el estudiante.");
            }

        } catch (SQLException e) {
            mostrarError(
                    "No fue posible eliminar al estudiante. "
                            + "Comprueba si tiene préstamos asociados."
            );
        }
    }

    /**
     * Muestra un mensaje de advertencia.
     */
    private void mostrarAviso(String mensaje) {
        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Aviso",
                JOptionPane.WARNING_MESSAGE
        );
    }

    /**
     * Muestra un mensaje de error.
     */
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}