package vista;

import config.Sesion;
import controlador.ControladorAutenticacion;
import modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * Ventana de inicio de sesión de la biblioteca escolar.
 * Autentica usuarios y registra la sesión activa.
 */
public class Login extends JFrame {

    private final ControladorAutenticacion controlador;
    private final JTextField txtCorreo;
    private final JPasswordField txtContrasena;

    public Login() {

        controlador = new ControladorAutenticacion();

        setTitle("Biblioteca Escolar - Iniciar sesión");
        setSize(430, 270);
        setMinimumSize(new Dimension(430, 270));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Contenedor principal con márgenes.
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 20));

        panelPrincipal.setBorder(
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        );

        // Encabezado.
        JLabel titulo = new JLabel(
                "Biblioteca Escolar",
                SwingConstants.CENTER
        );

        titulo.setFont(new Font("Arial", Font.BOLD, 20));

        panelPrincipal.add(titulo, BorderLayout.NORTH);

        // Formulario de autenticación.
        JPanel panelFormulario = new JPanel(
                new GridLayout(2, 2, 10, 12)
        );

        JLabel lblCorreo = new JLabel("Correo:");
        JLabel lblContrasena = new JLabel("Contraseña:");

        txtCorreo = new JTextField();
        txtContrasena = new JPasswordField();

        panelFormulario.add(lblCorreo);
        panelFormulario.add(txtCorreo);

        panelFormulario.add(lblContrasena);
        panelFormulario.add(txtContrasena);

        panelPrincipal.add(panelFormulario, BorderLayout.CENTER);

        // Botones.
        JPanel panelBotones = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 15, 0)
        );

        JButton btnIngresar = new JButton("Ingresar");
        JButton btnSalir = new JButton("Salir");

        btnIngresar.setPreferredSize(new Dimension(120, 35));
        btnSalir.setPreferredSize(new Dimension(120, 35));

        panelBotones.add(btnIngresar);
        panelBotones.add(btnSalir);

        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        setContentPane(panelPrincipal);

        // Eventos.
        btnIngresar.addActionListener(e -> iniciarSesion());

        btnSalir.addActionListener(e -> {
            Sesion.cerrar();
            dispose();
        });

        getRootPane().setDefaultButton(btnIngresar);
    }

    /**
     * Valida las credenciales y registra la sesión.
     */
    private void iniciarSesion() {

        String correo = txtCorreo.getText().trim();

        char[] caracteres = txtContrasena.getPassword();
        String contrasena = new String(caracteres);

        Arrays.fill(caracteres, '\0');

        if (correo.isBlank() || contrasena.isBlank()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Debes ingresar correo y contraseña.",
                    "Campos obligatorios",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        try {

            Usuario usuario =
                    controlador.iniciarSesion(correo, contrasena);

            if (usuario != null) {

                Sesion.iniciar(usuario);

                JOptionPane.showMessageDialog(
                        this,
                        "Bienvenido, " + usuario.getNombre()
                                + "\nRol: " + usuario.getRol(),
                        "Acceso autorizado",
                        JOptionPane.INFORMATION_MESSAGE
                );

                VentanaPrincipal ventanaPrincipal =
                        new VentanaPrincipal(usuario);

                ventanaPrincipal.setVisible(true);
                dispose();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Correo o contraseña incorrectos.",
                        "Acceso denegado",
                        JOptionPane.ERROR_MESSAGE
                );

                txtContrasena.setText("");
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "No fue posible conectar con la base de datos.",
                    "Error de conexión",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}

