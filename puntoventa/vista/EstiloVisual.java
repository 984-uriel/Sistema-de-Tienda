package puntoventa.vista;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Font;

/** Tema visual compartido. No contiene lógica de negocio. */
public final class EstiloVisual {
    public static final Color PRIMARIO = new Color(37, 99, 235);
    public static final Color PRIMARIO_OSCURO = new Color(30, 64, 175);
    public static final Color FONDO = new Color(245, 247, 251);
    public static final Color SUPERFICIE = Color.WHITE;
    public static final Color TEXTO = new Color(15, 23, 42);
    public static final Color TEXTO_SUAVE = new Color(100, 116, 139);
    public static final Color BORDE = new Color(226, 232, 240);
    public static final Color EXITO = new Color(22, 163, 74);
    public static final Color PELIGRO = new Color(220, 38, 38);

    private EstiloVisual() { }

    public static void aplicar() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
            // Swing conserva el estilo disponible en el equipo.
        }

        Font normal = new Font("Segoe UI", Font.PLAIN, 14);
        Font negrita = normal.deriveFont(Font.BOLD);
        UIManager.put("defaultFont", normal);
        UIManager.put("Label.font", normal);
        UIManager.put("Button.font", negrita);
        UIManager.put("TextField.font", normal);
        UIManager.put("PasswordField.font", normal);
        UIManager.put("ComboBox.font", normal);
        UIManager.put("Table.font", normal);
        UIManager.put("TableHeader.font", negrita.deriveFont(13f));
        UIManager.put("Table.rowHeight", 34);
        UIManager.put("Table.gridColor", BORDE);
        UIManager.put("Table.selectionBackground", new Color(219, 234, 254));
        UIManager.put("Table.selectionForeground", TEXTO);
        UIManager.put("TableHeader.background", new Color(248, 250, 252));
        UIManager.put("TableHeader.foreground", TEXTO_SUAVE);
        UIManager.put("TabbedPane.font", negrita);
        UIManager.put("TabbedPane.selected", SUPERFICIE);
        UIManager.put("control", SUPERFICIE);
        UIManager.put("nimbusBase", PRIMARIO);
        UIManager.put("nimbusFocus", new Color(147, 197, 253));
        UIManager.put("nimbusLightBackground", SUPERFICIE);
        UIManager.put("text", TEXTO);
    }

    public static Border bordeCampo() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                BorderFactory.createEmptyBorder(9, 11, 9, 11)
        );
    }

    public static void prepararCampo(JComponent campo) {
        campo.setBorder(bordeCampo());
        campo.setBackground(SUPERFICIE);
        campo.setForeground(TEXTO);
    }
}
