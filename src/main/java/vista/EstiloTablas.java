package vista;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Aplica una presentación uniforme a las tablas del sistema.
 */
public final class EstiloTablas {

    private EstiloTablas() {
        // Evita crear instancias de esta clase.
    }

    /**
     * Configura la apariencia y alineación de una tabla.
     *
     * @param tabla tabla que recibirá el formato.
     */
    public static void aplicar(JTable tabla) {

        tabla.setRowHeight(28);
        tabla.setFillsViewportHeight(true);
        tabla.setShowGrid(true);
        tabla.setGridColor(new Color(215, 220, 225));

        // Renderizador común: alinear todas las celdas a la izquierda.
        DefaultTableCellRenderer izquierda =
                new DefaultTableCellRenderer();

        izquierda.setHorizontalAlignment(SwingConstants.LEFT);
        izquierda.setBorder(
                BorderFactory.createEmptyBorder(0, 6, 0, 6)
        );

        // Aplicar a cada columna, independientemente del tipo de dato.
        for (int i = 0; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel()
                    .getColumn(i)
                    .setCellRenderer(izquierda);
        }

        // Encabezados.
        JTableHeader encabezado = tabla.getTableHeader();
        encabezado.setReorderingAllowed(false);
        encabezado.setPreferredSize(new Dimension(0, 30));

        // Mantener selección de una sola fila.
        tabla.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );
    }
}