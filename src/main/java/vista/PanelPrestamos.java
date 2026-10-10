package vista;

import config.Sesion;
import config.Sesion;
import controlador.ControladorEstudiantes;
import controlador.ControladorLibros;
import controlador.ControladorPrestamos;
import modelo.Estudiante;
import modelo.Libro;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Panel de registro de préstamos.
 * Utiliza controladores para consultar datos y registrar
 * préstamos sin bloquear la interfaz gráfica.
 */
public class PanelPrestamos extends JPanel {

    private final ControladorPrestamos controladorPrestamos;
    private final ControladorEstudiantes controladorEstudiantes;
    private final ControladorLibros controladorLibros;

    private final JComboBox<Estudiante> cbEstudiantes;
    private final JComboBox<Libro> cbLibros;

    private final JButton btnRegistrar;
    private final JButton btnActualizar;

    public PanelPrestamos() {

        controladorPrestamos = new ControladorPrestamos();
        controladorEstudiantes = new ControladorEstudiantes();
        controladorLibros = new ControladorLibros();

        setLayout(new BorderLayout(10, 10));
        setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        JLabel titulo = new JLabel("Registro de préstamos");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        add(titulo, BorderLayout.NORTH);

        // Formulario de selección.
        JPanel formulario = new JPanel(
                new GridLayout(2, 2, 10, 15)
        );

        formulario.setBorder(
                BorderFactory.createEmptyBorder(20, 10, 20, 10)
        );

        cbEstudiantes = new JComboBox<>();
        cbLibros = new JComboBox<>();

        formulario.add(new JLabel("Estudiante:"));
        formulario.add(cbEstudiantes);

        formulario.add(new JLabel("Libro disponible:"));
        formulario.add(cbLibros);

        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.add(formulario, BorderLayout.NORTH);

        add(panelCentral, BorderLayout.CENTER);

        // Botones.
        btnRegistrar = new JButton("Registrar préstamo");
        btnActualizar = new JButton("Actualizar listado");

        btnRegistrar.addActionListener(e -> registrarPrestamo());
        btnActualizar.addActionListener(e -> cargarDatos());

        JPanel panelInferior = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        panelInferior.add(btnRegistrar);
        panelInferior.add(btnActualizar);

        add(panelInferior, BorderLayout.SOUTH);

        cargarDatos();
    }

    /**
     * Recupera estudiantes y libros disponibles
     * mediante los controladores correspondientes.
     */
    private void cargarDatos() {

        cambiarEstadoControles(false);

        SwingWorker<DatosPrestamo, Void> worker =
                new SwingWorker<>() {

                    @Override
                    protected DatosPrestamo doInBackground()
                            throws Exception {

                        List<Estudiante> estudiantes = Sesion.esBibliotecario()
                                ? controladorEstudiantes.listarEstudiantes()
                                : List.of(controladorEstudiantes.obtenerEstudianteActual());

                        List<Libro> libros =
                                controladorLibros.listarLibros()
                                        .stream()
                                        .filter(libro -> libro.getStock() > 0)
                                        .toList();

                        return new DatosPrestamo(estudiantes, libros);
                    }

                    @Override
                    protected void done() {

                        try {
                            DatosPrestamo datos = get();

                            cbEstudiantes.removeAllItems();
                            cbLibros.removeAllItems();

                            for (Estudiante estudiante : datos.estudiantes()) {
                                cbEstudiantes.addItem(estudiante);
                            }

                            for (Libro libro : datos.libros()) {
                                cbLibros.addItem(libro);
                            }

                        } catch (InterruptedException e) {

                            Thread.currentThread().interrupt();
                            GestorErrores.mostrar(PanelPrestamos.this, e);

                        } catch (ExecutionException e) {

                            GestorErrores.mostrar(PanelPrestamos.this, e);

                        } finally {
                            cambiarEstadoControles(true);
                        }
                    }
                };

        worker.execute();
    }

    /**
     * Registra el préstamo seleccionado.
     */
    private void registrarPrestamo() {

        Estudiante estudiante =
                (Estudiante) cbEstudiantes.getSelectedItem();

        Libro libro =
                (Libro) cbLibros.getSelectedItem();

        if (estudiante == null || libro == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Debes seleccionar un estudiante y un libro disponible.",
                    "Selección incompleta",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        cambiarEstadoControles(false);

        SwingWorker<Void, Void> worker =
                new SwingWorker<>() {

                    @Override
                    protected Void doInBackground()
                            throws Exception {

                        controladorPrestamos.registrarPrestamo(
                                estudiante.getId(),
                                libro.getId()
                        );

                        return null;
                    }

                    @Override
                    protected void done() {

                        try {
                            get();

                            JOptionPane.showMessageDialog(
                                    PanelPrestamos.this,
                                    "Préstamo registrado correctamente.",
                                    "Operación exitosa",
                                    JOptionPane.INFORMATION_MESSAGE
                            );

                        } catch (InterruptedException e) {

                            Thread.currentThread().interrupt();
                            GestorErrores.mostrar(PanelPrestamos.this, e);

                        } catch (ExecutionException e) {

                            GestorErrores.mostrar(PanelPrestamos.this, e);

                        } finally {
                            cargarDatos();
                        }
                    }
                };

        worker.execute();
    }

    /**
     * Habilita o deshabilita los controles durante las operaciones.
     */
    private void cambiarEstadoControles(boolean habilitar) {

        cbEstudiantes.setEnabled(habilitar && Sesion.esBibliotecario());
        cbLibros.setEnabled(habilitar);
        btnRegistrar.setEnabled(
                habilitar
                        && cbEstudiantes.getItemCount() > 0
                        && cbLibros.getItemCount() > 0
        );
        btnActualizar.setEnabled(habilitar);
    }

    /**
     * Agrupa la información necesaria para registrar préstamos.
     */
    private record DatosPrestamo(
            List<Estudiante> estudiantes,
            List<Libro> libros
    ) {
    }
}