package vista;

import controlador.ControladorPrestamos;
import modelo.PrestamoDetalle;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Panel de gestión de devoluciones.
 * Muestra préstamos pendientes y permite registrar devoluciones.
 * Utiliza SwingWorker para ejecutar operaciones en segundo plano.
 */
public class PanelDevoluciones extends JPanel {

    private final ControladorPrestamos controlador;
    private final JTable tablaPrestamos;
    private final DefaultTableModel modeloTabla;

    private final JButton btnDevolver;
    private final JButton btnActualizar;

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public PanelDevoluciones() {

        controlador = new ControladorPrestamos();

        setLayout(new BorderLayout(10, 10));
        setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        // Encabezado.
        JLabel titulo = new JLabel("Devoluciones pendientes");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));

        add(titulo, BorderLayout.NORTH);

        // Tabla de préstamos pendientes.
        String[] columnas = {
                "ID", "Estudiante", "Libro",
                "Fecha préstamo", "Vencimiento", "Estado"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {

            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columna) {
                return columna == 0
                        ? Integer.class
                        : String.class;
            }
        };

        tablaPrestamos = new JTable(modeloTabla);
        tablaPrestamos.setAutoCreateRowSorter(true);

        // Aplicar formato visual común.
        EstiloTablas.aplicar(tablaPrestamos);

        tablaPrestamos.getColumnModel()
                .getColumn(0).setPreferredWidth(40);

        tablaPrestamos.getColumnModel()
                .getColumn(1).setPreferredWidth(140);

        tablaPrestamos.getColumnModel()
                .getColumn(2).setPreferredWidth(220);

        tablaPrestamos.getColumnModel()
                .getColumn(3).setPreferredWidth(110);

        tablaPrestamos.getColumnModel()
                .getColumn(4).setPreferredWidth(110);

        tablaPrestamos.getColumnModel()
                .getColumn(5).setPreferredWidth(90);

        add(
                new JScrollPane(tablaPrestamos),
                BorderLayout.CENTER
        );

        // Botones.
        btnDevolver = new JButton("Registrar devolución");
        btnActualizar = new JButton("Actualizar listado");

        btnDevolver.addActionListener(
                e -> registrarDevolucion()
        );

        btnActualizar.addActionListener(
                e -> cargarPrestamos()
        );

        JPanel panelInferior = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        panelInferior.add(btnDevolver);
        panelInferior.add(btnActualizar);

        add(panelInferior, BorderLayout.SOUTH);

        cargarPrestamos();
    }

    /**
     * Recupera los préstamos pendientes desde MySQL.
     * La consulta se ejecuta en segundo plano.
     */
    private void cargarPrestamos() {

        cambiarEstadoControles(false);

        SwingWorker<List<PrestamoDetalle>, Void> worker =
                new SwingWorker<>() {

                    @Override
                    protected List<PrestamoDetalle> doInBackground()
                            throws Exception {

                        return controlador.listarPrestamosDetallados();
                    }

                    @Override
                    protected void done() {

                        try {
                            List<PrestamoDetalle> prestamos = get();

                            modeloTabla.setRowCount(0);

                            for (PrestamoDetalle prestamo : prestamos) {

                                modeloTabla.addRow(new Object[]{
                                        prestamo.id(),
                                        prestamo.estudiante(),
                                        prestamo.libro(),
                                        prestamo.fechaPrestamo().format(FORMATO),
                                        prestamo.fechaVencimiento().format(FORMATO),
                                        prestamo.atrasado()
                                                ? "Atrasado"
                                                : "Pendiente"
                                });
                            }

                        } catch (InterruptedException e) {

                            Thread.currentThread().interrupt();
                            GestorErrores.mostrar(
                                    PanelDevoluciones.this, e
                            );

                        } catch (ExecutionException e) {

                            GestorErrores.mostrar(
                                    PanelDevoluciones.this, e
                            );

                        } finally {

                            cambiarEstadoControles(true);
                        }
                    }
                };

        worker.execute();
    }

    /**
     * Registra la devolución del préstamo seleccionado.
     */
    private void registrarDevolucion() {

        int fila = tablaPrestamos.getSelectedRow();

        if (fila == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona un préstamo para devolver.",
                    "Sin selección",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // Convertir la fila visual al índice del modelo.
        int filaModelo =
                tablaPrestamos.convertRowIndexToModel(fila);

        int idPrestamo =
                (int) modeloTabla.getValueAt(filaModelo, 0);

        String estudiante =
                (String) modeloTabla.getValueAt(filaModelo, 1);

        String libro =
                (String) modeloTabla.getValueAt(filaModelo, 2);

        // Confirmar la operación.
        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Confirmas la devolución de \"" + libro + "\""
                        + "\npor el estudiante " + estudiante + "?",
                "Confirmar devolución",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        cambiarEstadoControles(false);

        SwingWorker<Boolean, Void> worker =
                new SwingWorker<>() {

                    @Override
                    protected Boolean doInBackground()
                            throws Exception {

                        return controlador.registrarDevolucion(
                                idPrestamo
                        );
                    }

                    @Override
                    protected void done() {

                        try {
                            boolean devuelto = get();

                            if (devuelto) {

                                JOptionPane.showMessageDialog(
                                        PanelDevoluciones.this,
                                        "Devolución registrada correctamente.",
                                        "Operación exitosa",
                                        JOptionPane.INFORMATION_MESSAGE
                                );

                            } else {

                                JOptionPane.showMessageDialog(
                                        PanelDevoluciones.this,
                                        "El préstamo ya fue devuelto o no existe.",
                                        "Aviso",
                                        JOptionPane.WARNING_MESSAGE
                                );
                            }

                        } catch (InterruptedException e) {

                            Thread.currentThread().interrupt();
                            GestorErrores.mostrar(
                                    PanelDevoluciones.this, e
                            );

                        } catch (ExecutionException e) {

                            GestorErrores.mostrar(
                                    PanelDevoluciones.this, e
                            );

                        } finally {

                            // Actualizar los préstamos pendientes.
                            cargarPrestamos();
                        }
                    }
                };

        worker.execute();
    }

    /**
     * Habilita o deshabilita los controles
     * durante las operaciones en segundo plano.
     */
    private void cambiarEstadoControles(boolean habilitar) {

        tablaPrestamos.setEnabled(habilitar);
        btnDevolver.setEnabled(habilitar);
        btnActualizar.setEnabled(habilitar);
    }
}