package puntoventa.vista;

import puntoventa.controlador.VentaControlador;
import puntoventa.controlador.VentaControlador.ResultadoVenta;
import puntoventa.modelo.Producto;
import puntoventa.modelo.Stock;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.NumberFormat;
import java.util.Locale;

public class AdministradorFrame extends JFrame {

    private static final Color AZUL_OSCURO = new Color(25, 55, 87);
    private static final Color AZUL = new Color(50, 130, 190);
    private static final Color FONDO = new Color(243, 247, 250);
    private static final Color TEXTO_SUAVE = new Color(91, 104, 117);

    private final VentaControlador controlador;
    private final NumberFormat moneda = NumberFormat.getCurrencyInstance(Locale.of("es", "MX"));
    private final CardLayout tarjetasLayout = new CardLayout();
    private final JPanel tarjetas = new JPanel(tarjetasLayout);
    private final JLabel tituloSeccion = new JLabel("Resumen");
    private final JLabel ventasValor = crearValorTarjeta();
    private final JLabel ingresosValor = crearValorTarjeta();
    private final JLabel stockValor = crearValorTarjeta();
    private final JLabel adeudosValor = crearValorTarjeta();
    private final DefaultTableModel ventasModelo = modeloNoEditable(
            "Folio", "Fecha", "Cliente", "Método", "Total", "Recibido", "Estado"
    );
    private final DefaultTableModel adeudosModelo = modeloNoEditable(
            "Folio", "Cliente", "Total", "Pagado", "Saldo", "Estado"
    );
    private final DefaultTableModel inventarioModelo = modeloNoEditable(
            "ID", "Producto", "Cantidad", "Mínimo", "Máximo", "Estado"
    );
    private final DefaultTableModel productosModelo = modeloNoEditable(
            "ID", "Producto", "Descripción", "Precio", "Categoría", "Proveedor"
    );

    public AdministradorFrame(VentaControlador controlador) {
        super("Administración | Punto de venta");
        this.controlador = controlador;
        configurarVentana();
        construirInterfaz();
        actualizarDatos();
    }

    private void configurarVentana() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1050, 680));
        setSize(1180, 760);
        setLocationRelativeTo(null);
    }

    private void construirInterfaz() {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(FONDO);
        raiz.add(crearMenuLateral(), BorderLayout.WEST);
        raiz.add(crearContenido(), BorderLayout.CENTER);
        setContentPane(raiz);
    }

    private JPanel crearMenuLateral() {
        JPanel menu = new JPanel();
        menu.setPreferredSize(new Dimension(220, 0));
        menu.setBackground(AZUL_OSCURO);
        menu.setBorder(new EmptyBorder(24, 16, 20, 16));
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));

        JLabel marca = new JLabel("PUNTO VENTA");
        marca.setForeground(Color.WHITE);
        marca.setFont(new Font("SansSerif", Font.BOLD, 20));
        marca.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel rol = new JLabel("Panel de administrador");
        rol.setForeground(new Color(177, 198, 217));
        rol.setAlignmentX(Component.LEFT_ALIGNMENT);
        menu.add(marca);
        menu.add(Box.createVerticalStrut(5));
        menu.add(rol);
        menu.add(Box.createVerticalStrut(32));

        menu.add(crearBotonMenu("Resumen", "RESUMEN"));
        menu.add(crearBotonMenu("Productos", "PRODUCTOS"));
        menu.add(crearBotonMenu("Inventario", "INVENTARIO"));
        menu.add(crearBotonMenu("Ventas y pagos", "VENTAS"));
        menu.add(crearBotonMenu("Clientes", "CLIENTES"));
        menu.add(crearBotonMenu("Adeudos", "ADEUDOS"));
        menu.add(Box.createVerticalGlue());

        JButton cerrar = crearBotonLateral("Cerrar panel");
        cerrar.addActionListener(e -> dispose());
        menu.add(cerrar);
        return menu;
    }

    private JButton crearBotonMenu(String texto, String tarjeta) {
        JButton boton = crearBotonLateral(texto);
        boton.addActionListener(e -> {
            tituloSeccion.setText(texto);
            actualizarDatos();
            tarjetasLayout.show(tarjetas, tarjeta);
        });
        return boton;
    }

    private JButton crearBotonLateral(String texto) {
        JButton boton = new JButton(texto);
        boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 43));
        boton.setAlignmentX(Component.LEFT_ALIGNMENT);
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setFont(new Font("SansSerif", Font.BOLD, 14));
        boton.setForeground(Color.WHITE);
        boton.setBackground(AZUL_OSCURO);
        boton.setBorder(new EmptyBorder(10, 12, 10, 12));
        boton.setFocusPainted(false);
        boton.setOpaque(true);
        return boton;
    }

    private JPanel crearContenido() {
        JPanel contenido = new JPanel(new BorderLayout(0, 18));
        contenido.setBackground(FONDO);
        contenido.setBorder(new EmptyBorder(22, 25, 25, 25));

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        tituloSeccion.setFont(new Font("SansSerif", Font.BOLD, 27));
        tituloSeccion.setForeground(AZUL_OSCURO);
        JButton actualizar = new JButton("Actualizar datos");
        actualizar.setBackground(AZUL);
        actualizar.setForeground(Color.WHITE);
        actualizar.setFocusPainted(false);
        actualizar.addActionListener(e -> actualizarDatos());
        encabezado.add(tituloSeccion, BorderLayout.WEST);
        encabezado.add(actualizar, BorderLayout.EAST);

        tarjetas.setOpaque(false);
        tarjetas.add(crearResumen(), "RESUMEN");
        tarjetas.add(crearProductos(), "PRODUCTOS");
        tarjetas.add(crearInventario(), "INVENTARIO");
        tarjetas.add(crearVentas(), "VENTAS");
        tarjetas.add(crearClientes(), "CLIENTES");
        tarjetas.add(crearAdeudos(), "ADEUDOS");
        contenido.add(encabezado, BorderLayout.NORTH);
        contenido.add(tarjetas, BorderLayout.CENTER);
        return contenido;
    }

    private JPanel crearResumen() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setOpaque(false);
        JPanel indicadores = new JPanel(new GridLayout(1, 4, 14, 0));
        indicadores.setOpaque(false);
        indicadores.add(crearIndicador("Ventas registradas", ventasValor, new Color(64, 135, 200)));
        indicadores.add(crearIndicador("Ingresos", ingresosValor, new Color(45, 155, 105)));
        indicadores.add(crearIndicador("Existencias", stockValor, new Color(226, 151, 55)));
        indicadores.add(crearIndicador("Saldo pendiente", adeudosValor, new Color(205, 92, 92)));

        JPanel bienvenida = crearTarjeta();
        bienvenida.setLayout(new BorderLayout());
        JLabel texto = new JLabel(
                "<html><h2>Bienvenido al panel administrativo</h2>"
                        + "<p>Desde este espacio puedes supervisar productos, inventario, pagos, clientes y adeudos.</p>"
                        + "<p>Los indicadores se actualizan con las ventas registradas en la pantalla de cobro.</p></html>"
        );
        texto.setForeground(TEXTO_SUAVE);
        bienvenida.add(texto, BorderLayout.NORTH);
        panel.add(indicadores, BorderLayout.NORTH);
        panel.add(bienvenida, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearIndicador(String titulo, JLabel valor, Color color) {
        JPanel panel = crearTarjeta();
        panel.setLayout(new BorderLayout(0, 8));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(4, 0, 0, 0, color),
                new EmptyBorder(16, 16, 16, 16)
        ));
        JLabel etiqueta = new JLabel(titulo);
        etiqueta.setForeground(TEXTO_SUAVE);
        valor.setForeground(color.darker());
        panel.add(etiqueta, BorderLayout.NORTH);
        panel.add(valor, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearProductos() {
        return panelConTabla("Catálogo de productos", productosModelo);
    }

    private JPanel crearInventario() {
        return panelConTabla("Estado del inventario", inventarioModelo);
    }

    private JPanel crearVentas() {
        return panelConTabla("Historial de ventas y pagos de la sesión", ventasModelo);
    }

    private JPanel crearClientes() {
        DefaultTableModel modelo = modeloNoEditable("ID", "Nombre", "Correo", "Teléfono", "Dirección");
        modelo.addRow(new Object[]{
                controlador.getCliente().getIdCliente(), controlador.getCliente().getNombre(),
                controlador.getCliente().getCorreo(), controlador.getCliente().getTelefono(),
                controlador.getCliente().getDireccion()
        });
        return panelConTabla("Clientes registrados", modelo);
    }

    private JPanel crearAdeudos() {
        return panelConTabla("Ventas con saldo pendiente", adeudosModelo);
    }

    private JPanel panelConTabla(String descripcion, DefaultTableModel modelo) {
        JPanel panel = crearTarjeta();
        panel.setLayout(new BorderLayout(0, 14));
        JLabel etiqueta = new JLabel(descripcion);
        etiqueta.setFont(new Font("SansSerif", Font.BOLD, 17));
        etiqueta.setForeground(AZUL_OSCURO);
        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(30);
        tabla.setFillsViewportHeight(true);
        tabla.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        panel.add(etiqueta, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearTarjeta() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 230, 236)),
                new EmptyBorder(18, 18, 18, 18)
        ));
        return panel;
    }

    private void actualizarDatos() {
        ventasModelo.setRowCount(0);
        adeudosModelo.setRowCount(0);
        inventarioModelo.setRowCount(0);
        productosModelo.setRowCount(0);
        int existenciasTotales = 0;

        for (Producto producto : controlador.getProductos()) {
            productosModelo.addRow(new Object[]{
                    producto.getIdProducto(), producto.getNombre(), producto.getDescripcion(),
                    moneda.format(producto.getPrecio()), producto.getCategoria().getNombre(),
                    producto.getProveedor().getNombre()
            });
        }
        for (Stock stock : controlador.getInventario()) {
            inventarioModelo.addRow(new Object[]{
                    stock.getIdStock(), stock.getProducto().getNombre(), stock.getCantidadActual(),
                    stock.getStockMinimo(), stock.getStockMaximo(), stock.getEstado()
            });
            existenciasTotales += stock.getCantidadActual();
        }
        double ingresos = 0;
        double adeudos = 0;

        for (ResultadoVenta venta : controlador.getVentas()) {
            ingresos += venta.pago().getTotal();
            adeudos += venta.adeudo().getSaldoPendiente();
            ventasModelo.addRow(new Object[]{
                    venta.folio(), venta.fecha(), controlador.getCliente().getNombre(),
                    venta.pago().getMetodo(), moneda.format(venta.total()),
                    moneda.format(venta.pago().getTotal()), venta.adeudo().getEstado()
            });
            if (venta.adeudo().getSaldoPendiente() > 0) {
                adeudosModelo.addRow(new Object[]{
                        venta.folio(), controlador.getCliente().getNombre(), moneda.format(venta.total()),
                        moneda.format(venta.pago().getTotal()),
                        moneda.format(venta.adeudo().getSaldoPendiente()), venta.adeudo().getEstado()
                });
            }
        }

        ventasValor.setText(String.valueOf(controlador.getVentas().size()));
        ingresosValor.setText(moneda.format(ingresos));
        stockValor.setText(String.valueOf(existenciasTotales));
        adeudosValor.setText(moneda.format(adeudos));
    }

    private static JLabel crearValorTarjeta() {
        JLabel label = new JLabel("0");
        label.setFont(new Font("SansSerif", Font.BOLD, 24));
        return label;
    }

    private static DefaultTableModel modeloNoEditable(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }
}
