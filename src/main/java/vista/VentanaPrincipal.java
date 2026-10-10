package vista;

import modelo.Usuario;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana principal del sistema de biblioteca escolar.
 * Organiza los módulos según el rol del usuario.
 */
public class VentanaPrincipal extends JFrame {

    private final Usuario usuario;
    private final JPanel panelContenido;

    public VentanaPrincipal(Usuario usuario) {

        if (usuario == null) {
            throw new IllegalArgumentException(
                    "Se requiere un usuario autenticado."
            );
        }

        this.usuario = usuario;

        boolean esBibliotecario =
                "bibliotecario".equalsIgnoreCase(usuario.getRol());

        setTitle("Biblioteca Escolar - Sistema de Gestión");
        setSize(850, 550);
        setMinimumSize(new Dimension(750, 450));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // Encabezado con información del usuario.
        JLabel lblUsuario = new JLabel(
                "Usuario: " + usuario.getNombre()
                        + " | Rol: " + usuario.getRol()
        );

        lblUsuario.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        add(lblUsuario, BorderLayout.NORTH);

        // Menú lateral.
        JPanel panelMenu = new JPanel(
                new GridLayout(6, 1, 5, 10)
        );

        panelMenu.setBorder(
                BorderFactory.createEmptyBorder(15, 10, 15, 10)
        );

        JButton btnLibros = new JButton("Libros");
        JButton btnEstudiantes = new JButton("Estudiantes");
        JButton btnPrestamos = new JButton("Préstamos");
        JButton btnDevoluciones = new JButton("Devoluciones");
        JButton btnReportes = new JButton("Reportes");
        JButton btnCategorias = new JButton("Categorías");

        panelMenu.add(btnLibros);
        panelMenu.add(btnEstudiantes);
        panelMenu.add(btnPrestamos);
        panelMenu.add(btnDevoluciones);
        panelMenu.add(btnReportes);
        panelMenu.add(btnCategorias);

        add(panelMenu, BorderLayout.WEST);

        // Área central de contenido.
        panelContenido = new JPanel(new BorderLayout());

        panelContenido.add(
                new JLabel(
                        "Bienvenido al sistema de biblioteca",
                        SwingConstants.CENTER
                ),
                BorderLayout.CENTER
        );

        add(panelContenido, BorderLayout.CENTER);

        // Navegación según el rol.
        btnLibros.addActionListener(
                e -> mostrarPanel(new PanelLibros(esBibliotecario))
        );

        btnEstudiantes.addActionListener(e -> {
            if (esBibliotecario) {
                mostrarPanel(new PanelEstudiantes());
            }
        });

        btnPrestamos.addActionListener(e -> mostrarPanel(new PanelPrestamos()));

        btnDevoluciones.addActionListener(e -> mostrarPanel(new PanelDevoluciones()));

        btnReportes.addActionListener(e -> {
            if (esBibliotecario) {
                mostrarPanel(new PanelReportes());
            }
        });

        // Restringir módulos administrativos.
        btnEstudiantes.setEnabled(esBibliotecario);
        btnPrestamos.setEnabled(true);
        btnDevoluciones.setEnabled(true);
        btnReportes.setEnabled(esBibliotecario);
        btnCategorias.addActionListener(e -> {
            if (esBibliotecario) mostrarPanel(new PanelCategorias());
        });
        btnCategorias.setEnabled(esBibliotecario);
    }

    /**
     * Muestra un panel dentro del área principal.
     *
     * @param panel panel que se desea visualizar.
     */
    private void mostrarPanel(JPanel panel) {

        panelContenido.removeAll();
        panelContenido.add(panel, BorderLayout.CENTER);

        panelContenido.revalidate();
        panelContenido.repaint();
    }
}