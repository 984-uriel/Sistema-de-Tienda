package puntoventa.vista;

import puntoventa.controlador.VentaControlador;
import puntoventa.controlador.VentaControlador.ResultadoVenta;
import puntoventa.modelo.Producto;
import puntoventa.modelo.Stock;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class PuntoVentaFrame extends JFrame {

    private static final Color AZUL = new Color(31, 78, 121);
    private static final Color AZUL_CLARO = new Color(74, 163, 223);
    private static final Color FONDO = new Color(244, 247, 250);
    private static final Color TEXTO_SUAVE = new Color(89, 100, 112);

    private final VentaControlador controlador;
    private final NumberFormat moneda = NumberFormat.getCurrencyInstance(Locale.of("es", "MX"));
    private final Map<Integer, Integer> carrito = new LinkedHashMap<>();
    private final JComboBox<String> productoCombo = new JComboBox<>();
    private final JSpinner cantidadSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1, 1));
    private final JLabel productoInfo = new JLabel();
    private final JLabel totalValor = new JLabel();
    private final JLabel balanceTitulo = new JLabel("Saldo pendiente");
    private final JLabel balanceValor = new JLabel();
    private final JTextField recibidoField = new JTextField();
    private final JComboBox<String> metodoCombo = new JComboBox<>(
            new String[]{"Efectivo", "Tarjeta", "Transferencia", "Crédito"}
    );
    private final JLabel conexionEstado = new JLabel();
    private final DefaultTableModel carritoModelo = modeloNoEditable(
            "ID", "Producto", "Precio", "Cantidad", "Subtotal"
    );
    private final DefaultTableModel pagosModelo = modeloNoEditable(
            "Folio", "Fecha", "Artículos", "Método", "Total", "Estado"
    );
    private final JTable carritoTabla = new JTable(carritoModelo);

    public PuntoVentaFrame(VentaControlador controlador) {
        super("Punto de venta | Registro de pagos");
        this.controlador = controlador;
        cargarProductos();
        configurarVentana();
        construirInterfaz();
        conectarEventos();
        actualizarProductoSeleccionado();
        actualizarResumen();
        verificarConexion();
    }

    private void configurarVentana() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 720));
        setSize(1240, 800);
        setLocationRelativeTo(null);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                controlador.cerrarConexion();
            }
        });
    }

    private void construirInterfaz() {
        JPanel contenido = new JPanel(new BorderLayout(18, 18));
        contenido.setBackground(FONDO);
        contenido.setBorder(new EmptyBorder(20, 24, 24, 24));
        contenido.add(crearEncabezado(), BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new GridLayout(1, 2, 18, 0));
        cuerpo.setOpaque(false);
        cuerpo.add(crearPanelVenta());
        cuerpo.add(crearPanelPago());
        contenido.add(cuerpo, BorderLayout.CENTER);
        setContentPane(contenido);
    }

    private JPanel crearEncabezado() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        JLabel titulo = new JLabel("Registro de ventas y pagos");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        titulo.setForeground(AZUL);
        JLabel subtitulo = new JLabel("Selecciona varios productos, arma el carrito y registra un solo pago");
        subtitulo.setForeground(TEXTO_SUAVE);
        textos.add(titulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(subtitulo);

        JButton administrador = botonAzul("Panel administrador");
        administrador.addActionListener(e -> new AdministradorFrame(controlador).setVisible(true));
        conexionEstado.setFont(new Font("SansSerif", Font.BOLD, 13));
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        acciones.setOpaque(false);
        acciones.add(conexionEstado);
        acciones.add(administrador);
        panel.add(textos, BorderLayout.WEST);
        panel.add(acciones, BorderLayout.EAST);
        return panel;
    }

    private JPanel crearPanelVenta() {
        JPanel panel = tarjeta();
        panel.setLayout(new BorderLayout(0, 14));
        JLabel titulo = tituloTarjeta("Productos y carrito");

        JPanel selector = new JPanel(new GridBagLayout());
        selector.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(4, 0, 4, 8);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        selector.add(new JLabel("Producto"), gbc);
        gbc.gridy = 1;
        selector.add(productoCombo, gbc);
        gbc.gridy = 2;
        productoInfo.setForeground(TEXTO_SUAVE);
        selector.add(productoInfo, gbc);
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.35;
        selector.add(new JLabel("Cantidad"), gbc);
        gbc.gridy = 4;
        selector.add(cantidadSpinner, gbc);
        JButton agregar = botonAzul("Agregar al carrito");
        agregar.addActionListener(e -> agregarAlCarrito());
        gbc.gridx = 1;
        gbc.weightx = 0.65;
        selector.add(agregar, gbc);

        configurarTablaCarrito();
        JScrollPane scroll = new JScrollPane(carritoTabla);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(222, 228, 234)));
        JButton quitar = new JButton("Quitar producto seleccionado");
        quitar.addActionListener(e -> quitarDelCarrito());

        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.setOpaque(false);
        centro.add(selector, BorderLayout.NORTH);
        centro.add(scroll, BorderLayout.CENTER);
        centro.add(quitar, BorderLayout.SOUTH);
        panel.add(titulo, BorderLayout.NORTH);
        panel.add(centro, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelPago() {
        JPanel panel = tarjeta();
        panel.setLayout(new BorderLayout(0, 14));
        panel.add(tituloTarjeta("Cobro e historial"), BorderLayout.NORTH);

        JPanel contenido = new JPanel(new BorderLayout(0, 16));
        contenido.setOpaque(false);
        contenido.add(crearFormularioPago(), BorderLayout.NORTH);

        JTable pagosTabla = new JTable(pagosModelo);
        pagosTabla.setRowHeight(28);
        pagosTabla.setFillsViewportHeight(true);
        JPanel historial = new JPanel(new BorderLayout(0, 8));
        historial.setOpaque(false);
        JLabel etiqueta = new JLabel("Pagos recientes");
        etiqueta.setFont(new Font("SansSerif", Font.BOLD, 15));
        historial.add(etiqueta, BorderLayout.NORTH);
        historial.add(new JScrollPane(pagosTabla), BorderLayout.CENTER);
        contenido.add(historial, BorderLayout.CENTER);
        panel.add(contenido, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearFormularioPago() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(5, 0, 5, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Método de pago"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel("Monto recibido"), gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(metodoCombo, gbc);
        gbc.gridx = 1;
        panel.add(recibidoField, gbc);

        JPanel resumen = new JPanel(new GridLayout(2, 2, 8, 6));
        resumen.setBackground(new Color(235, 242, 249));
        resumen.setBorder(new EmptyBorder(12, 14, 12, 14));
        JLabel totalTitulo = new JLabel("Total a pagar");
        totalValor.setHorizontalAlignment(SwingConstants.RIGHT);
        totalValor.setFont(new Font("SansSerif", Font.BOLD, 21));
        totalValor.setForeground(AZUL);
        balanceValor.setHorizontalAlignment(SwingConstants.RIGHT);
        balanceValor.setFont(new Font("SansSerif", Font.BOLD, 15));
        resumen.add(totalTitulo);
        resumen.add(totalValor);
        resumen.add(balanceTitulo);
        resumen.add(balanceValor);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 0, 8, 0);
        panel.add(resumen, gbc);

        JButton registrar = botonAzul("Registrar pago y finalizar venta");
        registrar.setPreferredSize(new Dimension(0, 44));
        registrar.addActionListener(e -> registrarPago());
        gbc.gridy = 3;
        panel.add(registrar, gbc);
        return panel;
    }

    private void conectarEventos() {
        productoCombo.addActionListener(e -> actualizarProductoSeleccionado());
        recibidoField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { actualizarResumen(); }
            public void removeUpdate(DocumentEvent e) { actualizarResumen(); }
            public void changedUpdate(DocumentEvent e) { actualizarResumen(); }
        });
    }

    private void cargarProductos() {
        for (Producto producto : controlador.getProductos()) {
            productoCombo.addItem(producto.getNombre() + " — " + moneda.format(producto.getPrecio()));
        }
    }

    private Producto productoSeleccionado() {
        int indice = productoCombo.getSelectedIndex();
        return indice < 0 ? null : controlador.getProductos().get(indice);
    }

    private void actualizarProductoSeleccionado() {
        Producto producto = productoSeleccionado();
        if (producto == null) {
            return;
        }
        Stock stock = controlador.getStock(producto.getIdProducto());
        int enCarrito = carrito.getOrDefault(producto.getIdProducto(), 0);
        int disponibles = Math.max(0, stock.getCantidadActual() - enCarrito);
        productoInfo.setText("Stock disponible: " + disponibles + " | " + producto.getDescripcion());
        SpinnerNumberModel modelo = (SpinnerNumberModel) cantidadSpinner.getModel();
        modelo.setMaximum(Math.max(1, disponibles));
        modelo.setValue(1);
        cantidadSpinner.setEnabled(disponibles > 0);
    }

    private void agregarAlCarrito() {
        Producto producto = productoSeleccionado();
        if (producto == null) {
            return;
        }
        int cantidad = (int) cantidadSpinner.getValue();
        Stock stock = controlador.getStock(producto.getIdProducto());
        int nuevaCantidad = carrito.getOrDefault(producto.getIdProducto(), 0) + cantidad;
        if (nuevaCantidad > stock.getCantidadActual()) {
            aviso("No hay suficientes existencias de " + producto.getNombre() + ".");
            return;
        }
        carrito.put(producto.getIdProducto(), nuevaCantidad);
        refrescarCarrito();
        actualizarProductoSeleccionado();
    }

    private void quitarDelCarrito() {
        int fila = carritoTabla.getSelectedRow();
        if (fila < 0) {
            aviso("Selecciona un producto del carrito para quitarlo.");
            return;
        }
        int idProducto = (int) carritoModelo.getValueAt(fila, 0);
        carrito.remove(idProducto);
        refrescarCarrito();
        actualizarProductoSeleccionado();
    }

    private void refrescarCarrito() {
        carritoModelo.setRowCount(0);
        for (Producto producto : controlador.getProductos()) {
            Integer cantidad = carrito.get(producto.getIdProducto());
            if (cantidad != null) {
                carritoModelo.addRow(new Object[]{
                        producto.getIdProducto(), producto.getNombre(), moneda.format(producto.getPrecio()),
                        cantidad, moneda.format(producto.getPrecio() * cantidad)
                });
            }
        }
        actualizarResumen();
    }

    private void registrarPago() {
        try {
            ResultadoVenta resultado = controlador.registrarVenta(
                    new LinkedHashMap<>(carrito), leerMonto(true), (String) metodoCombo.getSelectedItem()
            );
            pagosModelo.insertRow(0, new Object[]{
                    resultado.folio(), resultado.fecha(), resultado.cantidad(), resultado.pago().getMetodo(),
                    moneda.format(resultado.total()), resultado.adeudo().getEstado()
            });
            JOptionPane.showMessageDialog(
                    this,
                    "Venta registrada correctamente.\nFolio: " + resultado.folio()
                            + "\nArtículos: " + resultado.cantidad()
                            + "\nTotal: " + moneda.format(resultado.total())
                            + "\nCambio: " + moneda.format(resultado.cambio())
                            + "\nSaldo pendiente: " + moneda.format(resultado.adeudo().getSaldoPendiente()),
                    "Pago registrado", JOptionPane.INFORMATION_MESSAGE
            );
            carrito.clear();
            recibidoField.setText("");
            refrescarCarrito();
            actualizarProductoSeleccionado();
        } catch (IllegalArgumentException e) {
            aviso(e.getMessage());
        }
    }

    private void actualizarResumen() {
        double total = controlador.calcularTotal(carrito);
        double recibido = leerMonto(false);
        totalValor.setText(moneda.format(total));
        if (recibido >= total && total > 0) {
            balanceTitulo.setText("Cambio");
            balanceValor.setForeground(new Color(36, 140, 94));
            balanceValor.setText(moneda.format(recibido - total));
        } else {
            balanceTitulo.setText("Saldo pendiente");
            balanceValor.setForeground(new Color(190, 80, 55));
            balanceValor.setText(moneda.format(Math.max(0, total - recibido)));
        }
    }

    private double leerMonto(boolean obligatorio) {
        String texto = recibidoField.getText().trim().replace(',', '.');
        if (texto.isEmpty()) {
            if (obligatorio) throw new IllegalArgumentException("Escribe el monto recibido.");
            return 0;
        }
        try {
            double monto = Double.parseDouble(texto);
            if (monto < 0) throw new IllegalArgumentException("El monto no puede ser negativo.");
            return monto;
        } catch (NumberFormatException e) {
            if (obligatorio) throw new IllegalArgumentException("Escribe un monto válido.");
            return 0;
        }
    }

    private void configurarTablaCarrito() {
        carritoTabla.setRowHeight(28);
        carritoTabla.setFillsViewportHeight(true);
        carritoTabla.getColumnModel().getColumn(0).setMinWidth(0);
        carritoTabla.getColumnModel().getColumn(0).setMaxWidth(0);
        carritoTabla.getColumnModel().getColumn(0).setPreferredWidth(0);
    }

    private JPanel tarjeta() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 230, 236)),
                new EmptyBorder(18, 18, 18, 18)
        ));
        return panel;
    }

    private JLabel tituloTarjeta(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("SansSerif", Font.BOLD, 20));
        label.setForeground(AZUL);
        return label;
    }

    private JButton botonAzul(String texto) {
        JButton boton = new JButton(texto);
        boton.setUI(new BasicButtonUI());
        boton.setFont(new Font("SansSerif", Font.BOLD, 13));
        boton.setForeground(Color.WHITE);
        boton.setBackground(AZUL_CLARO);
        boton.setBorder(new EmptyBorder(10, 14, 10, 14));
        boton.setFocusPainted(false);
        boton.setOpaque(true);
        return boton;
    }

    private void verificarConexion() {
        boolean conectado = controlador.conectar();
        conexionEstado.setText(conectado ? "● Base de datos conectada" : "● Sin conexión");
        conexionEstado.setForeground(conectado ? new Color(36, 140, 94) : new Color(190, 65, 65));
    }

    private void aviso(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Revisa los datos", JOptionPane.WARNING_MESSAGE);
    }

    private static DefaultTableModel modeloNoEditable(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    public static void aplicarEstiloDelSistema() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Swing utilizará su estilo predeterminado.
        }
    }
}
