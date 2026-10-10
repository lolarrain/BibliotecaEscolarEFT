package vista;

import controlador.ControladorEstudiantes;
import controlador.ControladorReportes;
import modelo.Estudiante;
import modelo.ReporteHistorial;
import modelo.ReporteLibro;
import modelo.ReportePrestamoActivo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

/** Permite consultar los tres reportes administrativos de la biblioteca. */
public class PanelReportes extends JPanel {

    private final ControladorReportes controlador = new ControladorReportes();
    private final ControladorEstudiantes controladorEstudiantes = new ControladorEstudiantes();
    private final JComboBox<String> cbTipo = new JComboBox<>(new String[]{
            "Libros más prestados", "Historial por estudiante", "Libros actualmente prestados"
    });
    private final JComboBox<Estudiante> cbEstudiante = new JComboBox<>();
    private final JButton btnActualizar = new JButton("Actualizar reporte");
    private final DefaultTableModel modeloTabla = new DefaultTableModel() {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable tabla = new JTable(modeloTabla);

    public PanelReportes() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titulo = new JLabel("Reportes de biblioteca");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        JPanel superior = new JPanel(new BorderLayout(0, 10));
        superior.add(titulo, BorderLayout.NORTH);

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtros.add(new JLabel("Reporte:"));
        filtros.add(cbTipo);
        filtros.add(new JLabel("Estudiante:"));
        filtros.add(cbEstudiante);
        superior.add(filtros, BorderLayout.SOUTH);
        add(superior, BorderLayout.NORTH);

        tabla.setAutoCreateRowSorter(true);
        EstiloTablas.aplicar(tabla);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel inferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        inferior.add(btnActualizar);
        add(inferior, BorderLayout.SOUTH);

        cbTipo.addActionListener(e -> {
            actualizarFiltros();
            cargarReporte();
        });
        btnActualizar.addActionListener(e -> cargarReporte());
        cbEstudiante.addActionListener(e -> {
            if (cbTipo.getSelectedIndex() == 1 && cbEstudiante.isEnabled()) cargarReporte();
        });

        actualizarFiltros();
        cargarEstudiantes();
        cargarReporte();
    }

    private void actualizarFiltros() {
        cbEstudiante.setEnabled(cbTipo.getSelectedIndex() == 1
                && cbEstudiante.getItemCount() > 0);
    }

    private void cargarEstudiantes() {
        new SwingWorker<List<Estudiante>, Void>() {
            @Override protected List<Estudiante> doInBackground() throws Exception {
                return controladorEstudiantes.listarEstudiantes();
            }
            @Override protected void done() {
                try {
                    List<Estudiante> estudiantes = get();
                    cbEstudiante.setEnabled(false);
                    cbEstudiante.removeAllItems();
                    for (Estudiante estudiante : estudiantes) cbEstudiante.addItem(estudiante);
                    actualizarFiltros();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    GestorErrores.mostrar(PanelReportes.this, e);
                } catch (ExecutionException e) {
                    GestorErrores.mostrar(PanelReportes.this, e);
                }
            }
        }.execute();
    }

    private void cargarReporte() {
        int tipo = cbTipo.getSelectedIndex();
        Estudiante estudiante = (Estudiante) cbEstudiante.getSelectedItem();
        if (tipo == 1 && estudiante == null) {
            modeloTabla.setRowCount(0);
            modeloTabla.setColumnIdentifiers(new String[]{"Selecciona un estudiante"});
            return;
        }
        btnActualizar.setEnabled(false);
        cbTipo.setEnabled(false);
        int idEstudiante = estudiante == null ? 0 : estudiante.getId();

        new SwingWorker<List<Object[]>, Void>() {
            private String[] columnas;
            @Override protected List<Object[]> doInBackground() throws Exception {
                List<Object[]> filas = new java.util.ArrayList<>();
                if (tipo == 0) {
                    columnas = new String[]{"ID", "Título", "Autor", "Total préstamos"};
                    for (ReporteLibro r : controlador.listarLibrosMasPrestados()) {
                        filas.add(new Object[]{r.idLibro(), r.titulo(), r.autor(), r.totalPrestamos()});
                    }
                } else if (tipo == 1) {
                    columnas = new String[]{"ID", "Estudiante", "Libro", "Préstamo", "Vencimiento", "Estado"};
                    for (ReporteHistorial r : controlador.listarHistorialEstudiante(idEstudiante)) {
                        filas.add(new Object[]{r.idPrestamo(), r.estudiante(), r.libro(),
                                r.fechaPrestamo(), r.fechaVencimiento(),
                                r.devuelto() ? "Devuelto" : "Pendiente"});
                    }
                } else {
                    columnas = new String[]{"ID", "Libro", "Estudiante", "Préstamo", "Vencimiento"};
                    for (ReportePrestamoActivo r : controlador.listarPrestamosActivos()) {
                        filas.add(new Object[]{r.idPrestamo(), r.titulo(), r.estudiante(),
                                r.fechaPrestamo(), r.fechaVencimiento()});
                    }
                }
                return filas;
            }
            @Override protected void done() {
                try {
                    List<Object[]> filas = get();
                    modeloTabla.setRowCount(0);
                    modeloTabla.setColumnIdentifiers(columnas);
                    for (Object[] fila : filas) modeloTabla.addRow(fila);
                    EstiloTablas.aplicar(tabla);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    GestorErrores.mostrar(PanelReportes.this, e);
                } catch (ExecutionException e) {
                    GestorErrores.mostrar(PanelReportes.this, e);
                } finally {
                    btnActualizar.setEnabled(true);
                    cbTipo.setEnabled(true);
                }
            }
        }.execute();
    }
}