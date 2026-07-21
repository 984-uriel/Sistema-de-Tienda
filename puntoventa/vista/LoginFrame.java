package puntoventa.vista;

import puntoventa.controlador.VentaControlador;
import puntoventa.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Arrays;

public class LoginFrame extends JFrame {
    private static final Color FONDO = new Color(239, 246, 255);
    private static final Color AZUL = EstiloVisual.PRIMARIO;
    private final VentaControlador controlador;
    private final JTextField usuarioField = new JTextField(22);
    private final JPasswordField contrasenaField = new JPasswordField(22);
    private final JLabel estado = new JLabel(" ");

    public LoginFrame(VentaControlador controlador) {
        super("Iniciar sesión | Punto de venta");
        this.controlador = controlador;
        construirInterfaz();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        SwingUtilities.invokeLater(this::prepararConexion);
    }

    private void construirInterfaz() {
        JPanel raiz = new JPanel(new GridLayout(1, 2));
        raiz.setPreferredSize(new Dimension(860, 520));
        raiz.add(crearPanelBienvenida());

        JPanel formulario = new JPanel();
        formulario.setBackground(Color.WHITE);
        formulario.setBorder(new EmptyBorder(58, 60, 52, 60));
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));

        JLabel marca = etiqueta("ACCESO SEGURO", 12, Font.BOLD, AZUL);
        JLabel titulo = etiqueta("¡Hola de nuevo!", 30, Font.BOLD, EstiloVisual.TEXTO);
        JLabel subtitulo = etiqueta("Ingresa tus datos para continuar", 14, Font.PLAIN,
                EstiloVisual.TEXTO_SUAVE);
        JLabel usuarioLabel = etiqueta("Usuario o correo", 13, Font.BOLD, EstiloVisual.TEXTO);
        JLabel contrasenaLabel = etiqueta("Contraseña", 13, Font.BOLD, EstiloVisual.TEXTO);

        prepararCampo(usuarioField);
        prepararCampo(contrasenaField);
        JButton ingresar = new BotonRedondeado("Iniciar sesión");
        ingresar.addActionListener(e -> iniciarSesion());
        contrasenaField.addActionListener(e -> iniciarSesion());
        estado.setForeground(EstiloVisual.PELIGRO);
        estado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        estado.setAlignmentX(Component.LEFT_ALIGNMENT);

        formulario.add(marca);
        formulario.add(Box.createVerticalStrut(12));
        formulario.add(titulo);
        formulario.add(Box.createVerticalStrut(7));
        formulario.add(subtitulo);
        formulario.add(Box.createVerticalStrut(38));
        formulario.add(usuarioLabel);
        formulario.add(Box.createVerticalStrut(8));
        formulario.add(usuarioField);
        formulario.add(Box.createVerticalStrut(19));
        formulario.add(contrasenaLabel);
        formulario.add(Box.createVerticalStrut(8));
        formulario.add(contrasenaField);
        formulario.add(Box.createVerticalStrut(27));
        formulario.add(ingresar);
        formulario.add(Box.createVerticalStrut(10));
        formulario.add(estado);
        formulario.add(Box.createVerticalGlue());
        raiz.add(formulario);
        setContentPane(raiz);
    }

    private JPanel crearPanelBienvenida() {
        JPanel panel = new PanelDegradado();
        panel.setBorder(new EmptyBorder(64, 52, 56, 52));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel icono = etiqueta("PV", 22, Font.BOLD, Color.WHITE);
        icono.setHorizontalAlignment(SwingConstants.CENTER);
        icono.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 110), 2));
        icono.setPreferredSize(new Dimension(58, 58));
        icono.setMaximumSize(new Dimension(58, 58));
        JLabel marca = etiqueta("PUNTO DE VENTA", 14, Font.BOLD, new Color(219, 234, 254));
        JLabel titulo = etiqueta("Todo tu negocio,\nen un solo lugar.", 31, Font.BOLD, Color.WHITE);
        titulo.setText("<html>Todo tu negocio,<br>en un solo lugar.</html>");
        JLabel detalle = etiqueta("Ventas, inventario y clientes organizados\npara hacer tu día más sencillo.",
                14, Font.PLAIN, new Color(219, 234, 254));
        detalle.setText("<html>Ventas, inventario y clientes organizados<br>para hacer tu día más sencillo.</html>");
        JLabel estadoSistema = etiqueta("●  Sistema listo para comenzar", 12, Font.BOLD,
                new Color(187, 247, 208));

        panel.add(icono);
        panel.add(Box.createVerticalStrut(18));
        panel.add(marca);
        panel.add(Box.createVerticalStrut(58));
        panel.add(titulo);
        panel.add(Box.createVerticalStrut(18));
        panel.add(detalle);
        panel.add(Box.createVerticalGlue());
        panel.add(estadoSistema);
        return panel;
    }

    private JLabel etiqueta(String texto, int tamano, int estilo, Color color) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(new Font("Segoe UI", estilo, tamano));
        etiqueta.setForeground(color);
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
        return etiqueta;
    }

    private void prepararCampo(JComponent campo) {
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        campo.setPreferredSize(new Dimension(320, 46));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EstiloVisual.BORDE),
                new EmptyBorder(10, 12, 10, 12)
        ));
        campo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AZUL, 2),
                        new EmptyBorder(9, 11, 9, 11)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(EstiloVisual.BORDE),
                        new EmptyBorder(10, 12, 10, 12)
                ));
            }
        });
    }

    private static class PanelDegradado extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setPaint(new GradientPaint(0, 0, new Color(30, 64, 175),
                    getWidth(), getHeight(), new Color(37, 99, 235)));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(new Color(255, 255, 255, 15));
            g2.fillOval(-90, getHeight() - 180, 280, 280);
            g2.fillOval(getWidth() - 130, -80, 230, 230);
            g2.dispose();
        }
    }

    private static class BotonRedondeado extends JButton {
        BotonRedondeado(String texto) {
            super(texto);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(Color.WHITE);
            setBackground(AZUL);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
            setPreferredSize(new Dimension(320, 46));
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setBorder(new EmptyBorder(12, 18, 12, 18));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getModel().isPressed() ? EstiloVisual.PRIMARIO_OSCURO : getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private void prepararConexion() {
        if (!controlador.conectar()) {
            estado.setText("No se pudo conectar con la base de datos.");
        } else {
            usuarioField.requestFocusInWindow();
        }
    }

    private void iniciarSesion() {
        char[] clave = contrasenaField.getPassword();
        try {
            Usuario usuario = controlador.autenticar(usuarioField.getText(), clave);
            if (usuario == null) {
                estado.setText("Usuario o contraseña incorrectos.");
                contrasenaField.setText("");
                return;
            }
            dispose();
            new PuntoVentaFrame(controlador).setVisible(true);
        } catch (IllegalArgumentException | IllegalStateException e) {
            estado.setText(e.getMessage());
        } finally {
            Arrays.fill(clave, '\0');
        }
    }
}
