package puntoventa.vista;

import puntoventa.controlador.VentaControlador;
import puntoventa.controlador.VentaControlador.ResultadoVenta;
import puntoventa.controlador.VentaControlador.AdeudoPendiente;
import puntoventa.modelo.Cliente;
import puntoventa.modelo.Producto;
import puntoventa.modelo.Stock;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PuntoVentaFrame extends JFrame {

    private static final Color AZUL = EstiloVisual.PRIMARIO_OSCURO;
    private static final Color AZUL_CLARO = EstiloVisual.PRIMARIO;
    private static final Color FONDO = EstiloVisual.FONDO;
    private static final Color TEXTO_SUAVE = EstiloVisual.TEXTO_SUAVE;

    private final VentaControlador controlador;
    private final NumberFormat moneda = NumberFormat.getCurrencyInstance(Locale.of("es", "MX"));
    private final Map<Integer, Integer> carrito = new LinkedHashMap<>();
    private final JComboBox<String> productoCombo = new JComboBox<>();
    private final JTextField buscarProductoField = new JTextField();
    private final JTextField codigoBarrasField = new JTextField();
    private final List<Producto> productosFiltrados = new ArrayList<>();
    private final JComboBox<String> clienteCombo = new JComboBox<>();
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
    private final DefaultTableModel adeudosModelo = modeloNoEditable(
            "ID", "Folio", "Cliente", "Fecha", "Total", "Pagado", "Saldo", "Estado"
    );
    private final JTable adeudosTabla = new JTable(adeudosModelo);
    private final JTextField abonoField = new JTextField();
    private List<AdeudoPendiente> adeudosPendientes = new ArrayList<>();

    public PuntoVentaFrame(VentaControlador controlador) {
        super("Punto de venta | Registro de pagos");
        this.controlador = controlador;
        verificarConexion();
        cargarProductos();
        cargarClientes(null);
        configurarVentana();
        construirInterfaz();
        cargarAdeudos();
        conectarEventos();
        actualizarProductoSeleccionado();
        actualizarResumen();
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
        contenido.setBorder(new EmptyBorder(24, 28, 28, 28));
        contenido.add(crearEncabezado(), BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new GridLayout(1, 2, 18, 0));
        cuerpo.setBackground(FONDO);
        cuerpo.add(crearPanelVenta());
        cuerpo.add(crearPanelPago());
        JTabbedPane pestanas = new JTabbedPane();
        pestanas.setUI(new PestanasModernasUI());
        pestanas.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pestanas.setBackground(FONDO);
        pestanas.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        pestanas.addTab("Venta principal", cuerpo);
        pestanas.addTab("Adeudos y abonos", crearPanelAdeudos());
        pestanas.addChangeListener(e -> {
            if (pestanas.getSelectedIndex() == 1) cargarAdeudos();
        });
        contenido.add(pestanas, BorderLayout.CENTER);
        aplicarEstiloControles(contenido);
        setContentPane(contenido);
    }

    private JPanel crearPanelAdeudos() {
        JPanel panel = tarjeta();
        panel.setLayout(new BorderLayout(0, 14));
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(Color.WHITE);
        encabezado.add(tituloTarjeta("Clientes con saldo pendiente"), BorderLayout.WEST);
        JButton actualizar = botonSecundario("Actualizar lista");
        actualizar.addActionListener(e -> cargarAdeudos());
        encabezado.add(actualizar, BorderLayout.EAST);

        adeudosTabla.setRowHeight(30);
        adeudosTabla.setFillsViewportHeight(true);
        adeudosTabla.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        JPanel acciones = new JPanel(new GridBagLayout());
        acciones.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.weightx = 1;
        acciones.add(new JLabel("Monto del abono"), gbc);
        gbc.gridy = 1;
        acciones.add(abonoField, gbc);
        JButton abonar = botonAzul("Registrar abono");
        abonar.addActionListener(e -> registrarAbono(false));
        gbc.gridx = 1;
        gbc.weightx = 0;
        acciones.add(abonar, gbc);
        JButton liquidar = botonAzul("Finiquitar adeudo");
        liquidar.addActionListener(e -> registrarAbono(true));
        gbc.gridx = 2;
        acciones.add(liquidar, gbc);

        panel.add(encabezado, BorderLayout.NORTH);
        panel.add(new JScrollPane(adeudosTabla), BorderLayout.CENTER);
        panel.add(acciones, BorderLayout.SOUTH);
        return panel;
    }

    private void cargarAdeudos() {
        if (adeudosModelo == null) return;
        try {
            adeudosPendientes = controlador.getAdeudosPendientes();
            adeudosModelo.setRowCount(0);
            for (AdeudoPendiente adeudo : adeudosPendientes) {
                adeudosModelo.addRow(new Object[]{adeudo.idAdeudo(), adeudo.folio(), adeudo.cliente(),
                        adeudo.fecha(), moneda.format(adeudo.montoTotal()), moneda.format(adeudo.montoPagado()),
                        moneda.format(adeudo.saldoPendiente()), adeudo.estado()});
            }
        } catch (IllegalStateException e) {
            aviso(e.getMessage());
        }
    }

    private void registrarAbono(boolean liquidar) {
        int fila = adeudosTabla.getSelectedRow();
        if (fila < 0) {
            aviso("Selecciona un adeudo de la tabla.");
            return;
        }
        AdeudoPendiente adeudo = adeudosPendientes.get(adeudosTabla.convertRowIndexToModel(fila));
        try {
            double monto = liquidar ? adeudo.saldoPendiente() : leerAbono();
            String mensaje = liquidar
                    ? "¿Confirmas que se recibió " + moneda.format(monto) + " para liquidar el adeudo?"
                    : "¿Confirmas el abono de " + moneda.format(monto) + "?";
            if (JOptionPane.showConfirmDialog(this, mensaje, "Confirmar pago",
                    JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
            controlador.registrarAbono(adeudo.idAdeudo(), monto);
            abonoField.setText("");
            cargarAdeudos();
            JOptionPane.showMessageDialog(this, liquidar ? "Adeudo liquidado correctamente."
                    : "Abono registrado correctamente.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            aviso(e.getMessage());
        }
    }

    private double leerAbono() {
        String texto = abonoField.getText().trim().replace(',', '.');
        if (texto.isEmpty()) throw new IllegalArgumentException("Escribe el monto del abono.");
        try {
            return Double.parseDouble(texto);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Escribe un abono válido.");
        }
    }

    private JPanel crearEncabezado() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(FONDO);
        JPanel textos = new JPanel();
        textos.setBackground(FONDO);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        JLabel titulo = new JLabel("Registro de ventas y pagos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 29));
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
        acciones.setBackground(FONDO);
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
        selector.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(4, 0, 4, 8);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        selector.add(new JLabel("Código de barras (escanea y presiona Enter)"), gbc);
        gbc.gridy = 1;
        selector.add(codigoBarrasField, gbc);
        gbc.gridy = 2;
        selector.add(new JLabel("Buscar producto por nombre"), gbc);
        gbc.gridy = 3;
        selector.add(buscarProductoField, gbc);
        gbc.gridy = 4;
        selector.add(new JLabel("Producto"), gbc);
        gbc.gridy = 5;
        selector.add(productoCombo, gbc);
        gbc.gridy = 6;
        productoInfo.setForeground(TEXTO_SUAVE);
        selector.add(productoInfo, gbc);
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        gbc.weightx = 0.35;
        selector.add(new JLabel("Cantidad"), gbc);
        gbc.gridy = 8;
        selector.add(cantidadSpinner, gbc);
        JButton agregar = botonAzul("Agregar al carrito");
        agregar.addActionListener(e -> agregarAlCarrito());
        gbc.gridx = 1;
        gbc.weightx = 0.65;
        selector.add(agregar, gbc);

        configurarTablaCarrito();
        JScrollPane scroll = new JScrollPane(carritoTabla);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(222, 228, 234)));
        JButton quitar = botonSecundario("Quitar producto seleccionado");
        quitar.addActionListener(e -> quitarDelCarrito());

        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.setBackground(Color.WHITE);
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
        contenido.setBackground(Color.WHITE);
        contenido.add(crearFormularioPago(), BorderLayout.NORTH);

        JTable pagosTabla = new JTable(pagosModelo);
        pagosTabla.setRowHeight(28);
        pagosTabla.setFillsViewportHeight(true);
        JPanel historial = new JPanel(new BorderLayout(0, 8));
        historial.setBackground(Color.WHITE);
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
        panel.setBackground(Color.WHITE);
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

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(new JLabel("Cliente (obligatorio si queda saldo pendiente)"), gbc);
        JPanel clientePanel = new JPanel(new BorderLayout(8, 0));
        clientePanel.setBackground(Color.WHITE);
        clientePanel.add(clienteCombo, BorderLayout.CENTER);
        JButton nuevoCliente = botonSecundario("Registrar cliente");
        nuevoCliente.addActionListener(e -> registrarCliente());
        clientePanel.add(nuevoCliente, BorderLayout.EAST);
        gbc.gridy = 3;
        panel.add(clientePanel, gbc);

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
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 0, 8, 0);
        panel.add(resumen, gbc);

        JButton registrar = botonAzul("Registrar pago y finalizar venta");
        registrar.setPreferredSize(new Dimension(0, 44));
        registrar.addActionListener(e -> registrarPago());
        gbc.gridy = 5;
        panel.add(registrar, gbc);
        return panel;
    }

    private void conectarEventos() {
        codigoBarrasField.addActionListener(e -> agregarPorCodigoBarras());
        productoCombo.addActionListener(e -> actualizarProductoSeleccionado());
        buscarProductoField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarProductos(); }
            public void removeUpdate(DocumentEvent e) { filtrarProductos(); }
            public void changedUpdate(DocumentEvent e) { filtrarProductos(); }
        });
        recibidoField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { actualizarResumen(); }
            public void removeUpdate(DocumentEvent e) { actualizarResumen(); }
            public void changedUpdate(DocumentEvent e) { actualizarResumen(); }
        });
    }

    private void agregarPorCodigoBarras() {
        Producto producto = controlador.buscarPorCodigoBarras(codigoBarrasField.getText());
        if (producto == null) {
            aviso("No existe un producto con ese código de barras.");
            codigoBarrasField.selectAll();
            return;
        }
        Stock stock = controlador.getStock(producto.getIdProducto());
        int nuevaCantidad = carrito.getOrDefault(producto.getIdProducto(), 0) + 1;
        if (stock == null || nuevaCantidad > stock.getCantidadActual()) {
            aviso("No hay existencias disponibles de " + producto.getNombre() + ".");
            return;
        }
        carrito.put(producto.getIdProducto(), nuevaCantidad);
        codigoBarrasField.setText("");
        refrescarCarrito();
        codigoBarrasField.requestFocusInWindow();
    }

    private void cargarProductos() {
        productosFiltrados.clear();
        productosFiltrados.addAll(controlador.getProductos());
        mostrarProductosFiltrados();
    }

    private void mostrarProductosFiltrados() {
        productoCombo.removeAllItems();
        for (Producto producto : productosFiltrados) {
            productoCombo.addItem(producto.getNombre() + " — " + moneda.format(producto.getPrecio()));
        }
    }

    private void filtrarProductos() {
        String filtro = buscarProductoField.getText().trim().toLowerCase(Locale.ROOT);
        productosFiltrados.clear();
        for (Producto producto : controlador.getProductos()) {
            if (producto.getNombre().toLowerCase(Locale.ROOT).contains(filtro)) {
                productosFiltrados.add(producto);
            }
        }
        mostrarProductosFiltrados();
        actualizarProductoSeleccionado();
    }

    private Producto productoSeleccionado() {
        int indice = productoCombo.getSelectedIndex();
        return indice < 0 ? null : productosFiltrados.get(indice);
    }

    private void actualizarProductoSeleccionado() {
        Producto producto = productoSeleccionado();
        if (producto == null) {
            productoInfo.setText("No hay productos disponibles en la base de datos.");
            cantidadSpinner.setEnabled(false);
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
                    new LinkedHashMap<>(carrito), leerMonto(true), (String) metodoCombo.getSelectedItem(),
                    clienteSeleccionado()
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

    private void cargarClientes(Cliente seleccionar) {
        clienteCombo.removeAllItems();
        clienteCombo.addItem("Venta sin cliente");
        int indiceSeleccionado = 0;
        int indice = 1;
        for (Cliente cliente : controlador.getClientes()) {
            clienteCombo.addItem(cliente.getNombre() + " | " + cliente.getTelefono());
            if (seleccionar != null && cliente.getIdCliente() == seleccionar.getIdCliente()) {
                indiceSeleccionado = indice;
            }
            indice++;
        }
        clienteCombo.setSelectedIndex(indiceSeleccionado);
    }

    private Cliente clienteSeleccionado() {
        int indice = clienteCombo.getSelectedIndex();
        return indice <= 0 ? null : controlador.getClientes().get(indice - 1);
    }

    private void registrarCliente() {
        JTextField nombre = new JTextField();
        JTextField correo = new JTextField();
        JTextField telefono = new JTextField();
        JTextField direccion = new JTextField();
        JPanel formulario = new JPanel(new GridLayout(0, 1, 4, 4));
        formulario.add(new JLabel("Nombre completo"));
        formulario.add(nombre);
        formulario.add(new JLabel("Correo"));
        formulario.add(correo);
        formulario.add(new JLabel("Teléfono"));
        formulario.add(telefono);
        formulario.add(new JLabel("Dirección"));
        formulario.add(direccion);
        int opcion = JOptionPane.showConfirmDialog(this, formulario, "Registrar cliente",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcion != JOptionPane.OK_OPTION) return;
        try {
            Cliente cliente = controlador.registrarCliente(nombre.getText(), correo.getText(),
                    telefono.getText(), direccion.getText());
            cargarClientes(cliente);
            JOptionPane.showMessageDialog(this, "Cliente registrado y seleccionado correctamente.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            aviso(e.getMessage());
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
        carritoTabla.setRowHeight(34);
        carritoTabla.setFillsViewportHeight(true);
        carritoTabla.getColumnModel().getColumn(0).setMinWidth(0);
        carritoTabla.getColumnModel().getColumn(0).setMaxWidth(0);
        carritoTabla.getColumnModel().getColumn(0).setPreferredWidth(0);
    }

    private JPanel tarjeta() {
        JPanel panel = new PanelRedondeado();
        panel.setBorder(new EmptyBorder(21, 21, 21, 21));
        return panel;
    }

    private JLabel tituloTarjeta(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 20));
        label.setForeground(AZUL);
        return label;
    }

    private JButton botonAzul(String texto) {
        JButton boton = new BotonRedondeado(texto);
        boton.setUI(new BasicButtonUI());
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setForeground(Color.WHITE);
        boton.setBackground(AZUL_CLARO);
        boton.setBorder(new EmptyBorder(10, 14, 10, 14));
        boton.setFocusPainted(false);
        boton.setOpaque(false);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        return boton;
    }

    private JButton botonSecundario(String texto) {
        JButton boton = new BotonRedondeado(texto);
        boton.setUI(new BasicButtonUI());
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setForeground(AZUL);
        boton.setBackground(new Color(219, 234, 254));
        boton.setBorder(new EmptyBorder(9, 13, 9, 13));
        boton.setFocusPainted(false);
        boton.setOpaque(false);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        return boton;
    }

    private void aplicarEstiloControles(Container contenedor) {
        for (Component componente : contenedor.getComponents()) {
            if (componente instanceof JTextField campo) {
                campo.setBackground(Color.WHITE);
                campo.setBorder(EstiloVisual.bordeCampo());
                campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            } else if (componente instanceof JComboBox<?> combo) {
                combo.setUI(new BasicComboBoxUI());
                combo.setBackground(Color.WHITE);
                combo.setForeground(EstiloVisual.TEXTO);
                combo.setBorder(BorderFactory.createLineBorder(EstiloVisual.BORDE));
                combo.setPreferredSize(new Dimension(combo.getPreferredSize().width, 40));
            } else if (componente instanceof JSpinner spinner) {
                spinner.setBorder(BorderFactory.createLineBorder(EstiloVisual.BORDE));
                spinner.setBackground(Color.WHITE);
            } else if (componente instanceof JTable tabla) {
                tabla.setRowHeight(34);
                tabla.setShowVerticalLines(false);
                tabla.setGridColor(EstiloVisual.BORDE);
                tabla.setSelectionBackground(new Color(219, 234, 254));
                tabla.setSelectionForeground(EstiloVisual.TEXTO);
                tabla.getTableHeader().setBackground(new Color(248, 250, 252));
                tabla.getTableHeader().setForeground(EstiloVisual.TEXTO_SUAVE);
                tabla.getTableHeader().setPreferredSize(new Dimension(0, 36));
            } else if (componente instanceof JScrollPane scroll) {
                scroll.setBorder(BorderFactory.createLineBorder(EstiloVisual.BORDE));
                scroll.getViewport().setBackground(Color.WHITE);
            }
            if (componente instanceof Container hijo) {
                aplicarEstiloControles(hijo);
            }
        }
    }

    private static class PanelRedondeado extends JPanel {
        PanelRedondeado() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(15, 23, 42, 12));
            g2.fillRoundRect(2, 3, getWidth() - 4, getHeight() - 5, 18, 18);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(1, 1, getWidth() - 4, getHeight() - 6, 18, 18);
            g2.setColor(EstiloVisual.BORDE);
            g2.drawRoundRect(1, 1, getWidth() - 4, getHeight() - 6, 18, 18);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class BotonRedondeado extends JButton {
        BotonRedondeado(String texto) {
            super(texto);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color color = getModel().isPressed() ? getBackground().darker() : getBackground();
            g2.setColor(color);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class PestanasModernasUI extends BasicTabbedPaneUI {
        @Override
        protected void installDefaults() {
            super.installDefaults();
            selectedTabPadInsets = new Insets(0, 0, 0, 0);
            tabInsets = new Insets(11, 18, 11, 18);
            contentBorderInsets = new Insets(10, 0, 0, 0);
        }

        @Override
        protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                                          int x, int y, int w, int h, boolean isSelected) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(isSelected ? Color.WHITE : new Color(226, 232, 240));
            g2.fillRoundRect(x, y + 2, w, h - 2, 12, 12);
            if (isSelected) {
                g2.setColor(EstiloVisual.PRIMARIO);
                g2.fillRoundRect(x + 12, y + h - 3, w - 24, 3, 3, 3);
            }
            g2.dispose();
        }

        @Override
        protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
            // Las propias tarjetas delimitan el contenido.
        }

        @Override
        protected void paintFocusIndicator(Graphics g, int tabPlacement, java.awt.Rectangle[] rects,
                                           int tabIndex, java.awt.Rectangle iconRect,
                                           java.awt.Rectangle textRect, boolean isSelected) {
            // Evita el borde punteado del estilo clásico.
        }
    }

    private void verificarConexion() {
        boolean conectado = controlador.estaConectado() || controlador.conectar();
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
