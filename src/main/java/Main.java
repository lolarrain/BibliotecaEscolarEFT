import vista.Login;

import javax.swing.SwingUtilities;

/**
 * Punto de entrada del sistema de biblioteca escolar.
 */
public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            Login login = new Login();
            login.setVisible(true);
        });
    }
}