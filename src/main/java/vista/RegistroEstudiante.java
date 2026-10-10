
package vista;

import controlador.ControladorEstudiantes;
import modelo.Estudiante;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * Formulario para registrar y editar estudiantes.
 * Utiliza GestorErrores para presentar las excepciones
 * de forma uniforme.
 */
public class RegistroEstudiante extends JDialog {

    private final JTextField txtNombre = new JTextField();
    private final JTextField txtRut = new JTextField();
    private final JTextField txtCurso = new JTextField();
    private final JTextField txtCorreo = new JTextField();

    private final ControladorEstudiantes controlador;
    private Estudiante estudianteEditar;

    private boolean guardado = false;

    /**
     * Constructor para registrar un estudiante.
     */
    public RegistroEstudiante(Window ventanaPadre,
                              ControladorEstudiantes controlador) {

        super(ventanaPadre, "Registrar estudiante",
                ModalityType.APPLICATION_MODAL);

        this.controlador = controlador;
        this.estudianteEditar = null;

        setSize(450, 300);
        setLocationRelativeTo(ventanaPadre);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        // Formulario de datos.
        JPanel formulario = new JPanel(
                new GridLayout(4, 2, 10, 10)
        );

        formulario.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 10, 20)
        );

        formulario.add(new JLabel("Nombre:"));
        formulario.add(txtNombre);

        formulario.add(new JLabel("RUT:"));
        formulario.add(txtRut);

        formulario.add(new JLabel("Curso:"));
        formulario.add(txtCurso);

        formulario.add(new JLabel("Correo:"));
        formulario.add(txtCorreo);

        // Botones.
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        JPanel botones = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        botones.add(btnGuardar);
        botones.add(btnCancelar);

        add(formulario, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);

        btnGuardar.addActionListener(e -> guardarEstudiante());
        btnCancelar.addActionListener(e -> dispose());

        getRootPane().setDefaultButton(btnGuardar);
    }

    /**
     * Constructor para editar un estudiante existente.
     */
    public RegistroEstudiante(Window ventanaPadre,
                              ControladorEstudiantes controlador,
                              Estudiante estudiante) {

        this(ventanaPadre, controlador);

        this.estudianteEditar = estudiante;

        setTitle("Editar estudiante");

        txtNombre.setText(estudiante.getNombre());
        txtRut.setText(estudiante.getRut());
        txtCurso.setText(estudiante.getCurso());
        txtCorreo.setText(estudiante.getCorreo());
    }

    /**
     * Registra o actualiza un estudiante.
     * La validación se realiza desde el modelo
     * mediante ControladorEstudiantes.
     */
    private void guardarEstudiante() {

        try {
            int id = estudianteEditar == null
                    ? 0
                    : estudianteEditar.getId();

            Estudiante estudiante = new Estudiante(
                    id,
                    txtNombre.getText().trim(),
                    txtRut.getText().trim(),
                    txtCorreo.getText().trim(),
                    txtCurso.getText().trim()
            );

            if (estudianteEditar == null) {

                controlador.agregarEstudiante(estudiante);

            } else {

                boolean actualizado =
                        controlador.actualizarEstudiante(estudiante);

                if (!actualizado) {
                    JOptionPane.showMessageDialog(
                            this,
                            "No se encontró el estudiante.",
                            "Aviso",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }
            }

            guardado = true;

            JOptionPane.showMessageDialog(
                    this,
                    estudianteEditar == null
                            ? "Estudiante registrado correctamente."
                            : "Estudiante actualizado correctamente.",
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
     * @return true si el estudiante fue guardado.
     */
    public boolean isGuardado() {
        return guardado;
    }
}
