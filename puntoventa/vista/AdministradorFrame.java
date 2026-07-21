package puntoventa.vista;

import puntoventa.controlador.VentaControlador;
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
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdministradorFrame extends JFrame {

    private static final Color AZUL_OSCURO = new Color(15, 23, 42);
    private static final Color AZUL = EstiloVisual.PRIMARIO;
    private static final Color FONDO = EstiloVisual.FONDO;
    private static final Color TEXTO_SUAVE = EstiloVisual.TEXTO_SUAVE;
    private static final Color MENU_ACTIVO = new Color(30, 64, 175);

    private final VentaControlador controlador;
    private final NumberFormat moneda = NumberFormat.getCurrencyInstance(Locale.of("es", "MX"));
    private final CardLayout tarjetasLayout = new CardLayout();
    private final JPanel tarjetas = new JPanel(tarjetasLayout);
    private final List<JButton> botonesMenu = new ArrayList<>();
    private final JLabel tituloSeccion = new JLabel("Resumen");
    private final JLabel ventasValor = crearValorTarjeta();
    private final JLabel ingresosValor = crearValorTarjeta();
    private final JLabel stockValor = crearValorTarjeta();
    private final JLabel adeudosValor = crearValorTarjeta();
    private final DefaultTableModel adeudosModelo = modeloNoEditable(
            "Folio", "Cliente", "Total", "Pagado", "Saldo", "Estado"
    );
    private final DefaultTableModel inventarioModelo = modeloNoEditable(
            "ID", "Producto", "Cantidad", "Mínimo", "Máximo", "Estado"
    );
    private final DefaultTableModel productosModelo = modeloNoEditable(
            "ID", "Código de barras", "Producto", "Descripción", "Precio", "Categoría", "Proveedor"
    );
    private final DefaultTableModel clientesModelo = modeloNoEditable(
            "ID", "Nombre", "Correo", "Teléfono", "Dirección"
    );
    private final DefaultTableModel proveedoresModelo = modeloNoEditable(
            "ID", "Proveedor", "Teléfono", "Correo", "Productos asociados"
    );
    private final DefaultTableModel categoriasModelo = modeloNoEditable(
            "ID", "Categoría", "Descripción", "Productos"
    );
    private final DefaultTableModel pedidosModelo = modeloNoEditable(
            "Folio", "Fecha", "Cliente", "Artículos", "Total", "Estado"
    );
    private final DefaultTableModel pagosModelo = modeloNoEditable(
            "ID pago", "Fecha", "Folio", "Cliente", "Método", "Monto recibido"
    );
    private final DefaultTableModel personalModelo = modeloNoEditable(
            "ID usuario", "Nombre", "Correo", "Teléfono", "Rol", "Puesto / cargo"
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
        menu.setPreferredSize(new Dimension(238, 0));
        menu.setBackground(AZUL_OSCURO);
        menu.setBorder(new EmptyBorder(28, 18, 22, 18));
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));

        JLabel marca = new JLabel("PUNTO DE VENTA");
        marca.setForeground(Color.WHITE);
        marca.setFont(new Font("Segoe UI", Font.BOLD, 19));
        marca.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel rol = new JLabel("Panel de administrador");
        rol.setForeground(new Color(148, 163, 184));
        rol.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rol.setAlignmentX(Component.LEFT_ALIGNMENT);
        menu.add(marca);
        menu.add(Box.createVerticalStrut(5));
        menu.add(rol);
        menu.add(Box.createVerticalStrut(22));

        JButton resumen = crearBotonMenu("Resumen", "RESUMEN");
        menu.add(resumen);
        menu.add(crearBotonMenu("Productos", "PRODUCTOS"));
        menu.add(crearBotonMenu("Categorías", "CATEGORIAS"));
        menu.add(crearBotonMenu("Inventario", "INVENTARIO"));
        menu.add(crearBotonMenu("Pedidos y ventas", "VENTAS"));
        menu.add(crearBotonMenu("Pagos recibidos", "PAGOS"));
        menu.add(crearBotonMenu("Clientes", "CLIENTES"));
        menu.add(crearBotonMenu("Adeudos", "ADEUDOS"));
        menu.add(crearBotonMenu("Proveedores", "PROVEEDORES"));
        menu.add(crearBotonMenu("Personal", "PERSONAL"));
        seleccionarBoton(resumen);
        menu.add(Box.createVerticalGlue());

        JButton cerrar = crearBotonLateral("Cerrar panel");
        cerrar.addActionListener(e -> dispose());
        menu.add(cerrar);
        return menu;
    }

    private JButton crearBotonMenu(String texto, String tarjeta) {
        JButton boton = crearBotonLateral(texto);
        botonesMenu.add(boton);
        boton.addActionListener(e -> {
            tituloSeccion.setText(texto);
            seleccionarBoton(boton);
            actualizarDatos();
            tarjetasLayout.show(tarjetas, tarjeta);
        });
        return boton;
    }

    private void seleccionarBoton(JButton seleccionado) {
        for (JButton boton : botonesMenu) {
            boolean activo = boton == seleccionado;
            boton.setBackground(activo ? MENU_ACTIVO : AZUL_OSCURO);
            boton.setForeground(activo ? Color.WHITE : new Color(203, 213, 225));
        }
    }

    private JButton crearBotonLateral(String texto) {
        JButton boton = new JButton(texto);
        boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        boton.setAlignmentX(Component.LEFT_ALIGNMENT);
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        boton.setForeground(new Color(203, 213, 225));
        boton.setBackground(AZUL_OSCURO);
        boton.setUI(new BasicButtonUI());
        boton.setBorder(new EmptyBorder(8, 14, 8, 14));
        boton.setFocusPainted(false);
        boton.setOpaque(true);
        boton.setContentAreaFilled(true);
        boton.setBorderPainted(false);
        return boton;
    }

    private JPanel crearContenido() {
        JPanel contenido = new JPanel(new BorderLayout(0, 18));
        contenido.setBackground(FONDO);
        contenido.setBorder(new EmptyBorder(26, 30, 30, 30));

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(FONDO);
        JPanel titulos = new JPanel();
        titulos.setLayout(new BoxLayout(titulos, BoxLayout.Y_AXIS));
        titulos.setBackground(FONDO);
        tituloSeccion.setFont(new Font("Segoe UI", Font.BOLD, 29));
        tituloSeccion.setForeground(AZUL_OSCURO);
        JLabel subtitulo = new JLabel("Consulta y administra la operación de tu negocio");
        subtitulo.setForeground(TEXTO_SUAVE);
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titulos.add(tituloSeccion);
        titulos.add(Box.createVerticalStrut(3));
        titulos.add(subtitulo);
        JButton actualizar = new BotonRedondeado("Actualizar datos");
        actualizar.setUI(new BasicButtonUI());
        actualizar.setBackground(AZUL);
        actualizar.setForeground(Color.WHITE);
        actualizar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        actualizar.setBorder(new EmptyBorder(11, 17, 11, 17));
        actualizar.setFocusPainted(false);
        actualizar.setOpaque(false);
        actualizar.setContentAreaFilled(false);
        actualizar.setBorderPainted(false);
        actualizar.addActionListener(e -> actualizarDatos());
        encabezado.add(titulos, BorderLayout.WEST);
        encabezado.add(actualizar, BorderLayout.EAST);

        tarjetas.setBackground(FONDO);
        tarjetas.add(crearResumen(), "RESUMEN");
        tarjetas.add(crearProductos(), "PRODUCTOS");
        tarjetas.add(crearCategorias(), "CATEGORIAS");
        tarjetas.add(crearInventario(), "INVENTARIO");
        tarjetas.add(crearVentas(), "VENTAS");
        tarjetas.add(crearPagos(), "PAGOS");
        tarjetas.add(crearClientes(), "CLIENTES");
        tarjetas.add(crearAdeudos(), "ADEUDOS");
        tarjetas.add(crearProveedores(), "PROVEEDORES");
        tarjetas.add(crearPersonal(), "PERSONAL");
        contenido.add(encabezado, BorderLayout.NORTH);
        contenido.add(tarjetas, BorderLayout.CENTER);
        return contenido;
    }

    private JPanel crearResumen() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(FONDO);
        JPanel indicadores = new JPanel(new GridLayout(1, 4, 14, 0));
        indicadores.setBackground(FONDO);
        indicadores.add(crearIndicador("Ventas registradas", ventasValor, EstiloVisual.PRIMARIO));
        indicadores.add(crearIndicador("Ingresos", ingresosValor, new Color(22, 163, 74)));
        indicadores.add(crearIndicador("Existencias", stockValor, new Color(217, 119, 6)));
        indicadores.add(crearIndicador("Saldo pendiente", adeudosValor, EstiloVisual.PELIGRO));

        JPanel bienvenida = crearTarjeta();
        bienvenida.setLayout(new BorderLayout(26, 0));
        JPanel introduccion = panelTransparente();
        introduccion.setLayout(new BoxLayout(introduccion, BoxLayout.Y_AXIS));
        JLabel distintivo = new JLabel("  VISTA GENERAL  ");
        distintivo.setOpaque(true);
        distintivo.setBackground(new Color(219, 234, 254));
        distintivo.setForeground(EstiloVisual.PRIMARIO_OSCURO);
        distintivo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        distintivo.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel texto = new JLabel(
                "<html><h2 style='color:#0f172a'>Tu negocio bajo control</h2>"
                        + "<p>Consulta productos, inventario, ventas, clientes y adeudos<br>"
                        + "desde el menú lateral.</p></html>"
        );
        texto.setForeground(TEXTO_SUAVE);
        texto.setAlignmentX(Component.LEFT_ALIGNMENT);
        introduccion.add(distintivo);
        introduccion.add(Box.createVerticalStrut(18));
        introduccion.add(texto);
        introduccion.add(Box.createVerticalGlue());

        JPanel estado = new JPanel();
        estado.setBackground(new Color(248, 250, 252));
        estado.setBorder(new EmptyBorder(20, 22, 20, 22));
        estado.setPreferredSize(new Dimension(340, 0));
        estado.setLayout(new BoxLayout(estado, BoxLayout.Y_AXIS));
        JLabel estadoTitulo = new JLabel("Estado del sistema");
        estadoTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        estadoTitulo.setForeground(EstiloVisual.TEXTO);
        estadoTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        estado.add(estadoTitulo);
        estado.add(Box.createVerticalStrut(17));
        estado.add(lineaEstado("●", "Base de datos conectada", EstiloVisual.EXITO));
        estado.add(Box.createVerticalStrut(13));
        estado.add(lineaEstado("✓", "Catálogo disponible", EstiloVisual.PRIMARIO));
        estado.add(Box.createVerticalStrut(13));
        estado.add(lineaEstado("↻", "Datos listos para actualizar", new Color(217, 119, 6)));

        bienvenida.add(introduccion, BorderLayout.CENTER);
        bienvenida.add(estado, BorderLayout.EAST);
        panel.add(indicadores, BorderLayout.NORTH);
        panel.add(bienvenida, BorderLayout.CENTER);
        return panel;
    }

    private JPanel lineaEstado(String icono, String texto, Color color) {
        JPanel linea = panelTransparente();
        linea.setLayout(new BoxLayout(linea, BoxLayout.X_AXIS));
        linea.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel marca = new JLabel(icono);
        marca.setFont(new Font("Segoe UI", Font.BOLD, 15));
        marca.setForeground(color);
        JLabel descripcion = new JLabel(texto);
        descripcion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descripcion.setForeground(TEXTO_SUAVE);
        linea.add(marca);
        linea.add(Box.createHorizontalStrut(10));
        linea.add(descripcion);
        return linea;
    }

    private JPanel crearIndicador(String titulo, JLabel valor, Color color) {
        JPanel panel = crearTarjeta();
        panel.setLayout(new BorderLayout(12, 6));
        JLabel icono = new JLabel("●");
        icono.setHorizontalAlignment(SwingConstants.CENTER);
        icono.setForeground(color);
        icono.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 25));
        icono.setOpaque(true);
        icono.setFont(new Font("Segoe UI", Font.BOLD, 15));
        icono.setPreferredSize(new Dimension(36, 36));
        JLabel etiqueta = new JLabel(titulo);
        etiqueta.setForeground(TEXTO_SUAVE);
        valor.setForeground(color.darker());
        panel.add(icono, BorderLayout.WEST);
        JPanel datos = panelTransparente();
        datos.setLayout(new BoxLayout(datos, BoxLayout.Y_AXIS));
        datos.add(etiqueta);
        datos.add(Box.createVerticalStrut(6));
        datos.add(valor);
        panel.add(datos, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearProductos() {
        return panelConTabla("Catálogo de productos", productosModelo);
    }

    private JPanel crearInventario() {
        return panelConTabla("Estado del inventario", inventarioModelo);
    }

    private JPanel crearCategorias() {
        return panelConTabla("Categorías del catálogo", categoriasModelo);
    }

    private JPanel crearVentas() {
        return panelConTabla("Pedidos y ventas registrados", pedidosModelo);
    }

    private JPanel crearPagos() {
        return panelConTabla("Pagos recibidos de clientes", pagosModelo);
    }

    private JPanel crearClientes() {
        return panelConTabla("Clientes registrados", clientesModelo);
    }

    private JPanel crearAdeudos() {
        return panelConTabla("Ventas con saldo pendiente", adeudosModelo);
    }

    private JPanel crearProveedores() {
        return panelConTabla("Directorio de proveedores", proveedoresModelo);
    }

    private JPanel crearPersonal() {
        return panelConTabla("Empleados y administradores", personalModelo);
    }

    private JPanel panelConTabla(String descripcion, DefaultTableModel modelo) {
        JPanel panel = crearTarjeta();
        panel.setLayout(new BorderLayout(0, 14));
        JLabel etiqueta = new JLabel(descripcion);
        etiqueta.setFont(new Font("Segoe UI", Font.BOLD, 18));
        etiqueta.setForeground(AZUL_OSCURO);
        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(30);
        tabla.setFillsViewportHeight(true);
        tabla.setShowVerticalLines(false);
        tabla.setGridColor(EstiloVisual.BORDE);
        tabla.setSelectionBackground(new Color(219, 234, 254));
        tabla.setSelectionForeground(EstiloVisual.TEXTO);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.getTableHeader().setPreferredSize(new Dimension(0, 36));
        panel.add(etiqueta, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearTarjeta() {
        JPanel panel = new PanelRedondeado();
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        return panel;
    }

    private JPanel panelTransparente() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        return panel;
    }

    private static class PanelRedondeado extends JPanel {
        PanelRedondeado() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(java.awt.Graphics g) {
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
            g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                    java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(15, 23, 42, 12));
            g2.fillRoundRect(2, 3, getWidth() - 4, getHeight() - 4, 18, 18);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(1, 1, getWidth() - 4, getHeight() - 5, 18, 18);
            g2.setColor(EstiloVisual.BORDE);
            g2.drawRoundRect(1, 1, getWidth() - 4, getHeight() - 5, 18, 18);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class BotonRedondeado extends JButton {
        BotonRedondeado(String texto) {
            super(texto);
            setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(java.awt.Graphics g) {
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
            g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                    java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getModel().isPressed() ? EstiloVisual.PRIMARIO_OSCURO : getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private void actualizarDatos() {
        adeudosModelo.setRowCount(0);
        inventarioModelo.setRowCount(0);
        productosModelo.setRowCount(0);
        clientesModelo.setRowCount(0);
        proveedoresModelo.setRowCount(0);
        categoriasModelo.setRowCount(0);
        pedidosModelo.setRowCount(0);
        pagosModelo.setRowCount(0);
        personalModelo.setRowCount(0);
        int existenciasTotales = 0;

        for (Producto producto : controlador.getProductos()) {
            productosModelo.addRow(new Object[]{
                    producto.getIdProducto(), producto.getCodigoBarras(), producto.getNombre(), producto.getDescripcion(),
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
        for (puntoventa.modelo.Cliente cliente : controlador.getClientes()) {
            clientesModelo.addRow(new Object[]{cliente.getIdCliente(), cliente.getNombre(),
                    cliente.getCorreo(), cliente.getTelefono(), cliente.getDireccion()});
        }
        controlador.getProveedoresResumen().forEach(proveedor -> proveedoresModelo.addRow(new Object[]{
                proveedor.id(), proveedor.nombre(), proveedor.telefono(), proveedor.correo(), proveedor.productos()
        }));
        controlador.getCategoriasResumen().forEach(categoria -> categoriasModelo.addRow(new Object[]{
                categoria.id(), categoria.nombre(), categoria.descripcion(), categoria.productos()
        }));
        var pedidos = controlador.getPedidosResumen();
        pedidos.forEach(pedido -> pedidosModelo.addRow(new Object[]{
                pedido.folio(), pedido.fecha(), pedido.cliente(), pedido.articulos(),
                moneda.format(pedido.total()), pedido.estado()
        }));
        var pagos = controlador.getPagosResumen();
        pagos.forEach(pago -> pagosModelo.addRow(new Object[]{
                pago.id(), pago.fecha(), pago.folio(), pago.cliente(), pago.metodo(), moneda.format(pago.total())
        }));
        controlador.getPersonalResumen().forEach(persona -> personalModelo.addRow(new Object[]{
                persona.idUsuario(), persona.nombre(), persona.correo(), persona.telefono(),
                persona.tipo(), persona.cargo()
        }));
        double ingresos = pagos.stream().mapToDouble(pago -> pago.total()).sum();
        var adeudosPendientes = controlador.getAdeudosPendientes();
        double adeudos = adeudosPendientes.stream().mapToDouble(adeudo -> adeudo.saldoPendiente()).sum();
        adeudosPendientes.forEach(adeudo -> adeudosModelo.addRow(new Object[]{
                adeudo.folio(), adeudo.cliente(), moneda.format(adeudo.montoTotal()),
                moneda.format(adeudo.montoPagado()), moneda.format(adeudo.saldoPendiente()), adeudo.estado()
        }));

        ventasValor.setText(String.valueOf(pedidos.size()));
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
