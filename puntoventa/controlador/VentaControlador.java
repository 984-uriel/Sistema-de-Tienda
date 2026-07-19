package puntoventa.controlador;

import puntoventa.modelo.Adeudo;
import puntoventa.modelo.Categoria;
import puntoventa.modelo.Cliente;
import puntoventa.modelo.Conexion;
import puntoventa.modelo.DetallePedido;
import puntoventa.modelo.Pago;
import puntoventa.modelo.Pedido;
import puntoventa.modelo.Producto;
import puntoventa.modelo.Proveedor;
import puntoventa.modelo.Stock;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VentaControlador {

    private final List<Producto> productos = new ArrayList<>();
    private final Map<Integer, Stock> inventario = new LinkedHashMap<>();
    private final List<ResultadoVenta> ventas = new ArrayList<>();
    private final Cliente cliente;
    private Connection conexion;
    private int siguienteFolio = 2;

    public VentaControlador() {
        cliente = new Cliente(
                1, 1, "Juan Pérez", "juan@gmail.com", "123456", "9991111111", "Av. Tulum 120"
        );
        cargarCatalogoInicial();
    }

    public boolean conectar() {
        conexion = Conexion.conectar();
        return conexion != null;
    }

    public List<Producto> getProductos() {
        return Collections.unmodifiableList(productos);
    }

    public List<Stock> getInventario() {
        return Collections.unmodifiableList(new ArrayList<>(inventario.values()));
    }

    public List<ResultadoVenta> getVentas() {
        return Collections.unmodifiableList(ventas);
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Stock getStock(int idProducto) {
        return inventario.get(idProducto);
    }

    public double calcularTotal(Map<Integer, Integer> cantidades) {
        double total = 0;
        for (Map.Entry<Integer, Integer> item : cantidades.entrySet()) {
            Producto producto = buscarProducto(item.getKey());
            if (producto != null) {
                total += producto.getPrecio() * item.getValue();
            }
        }
        return total;
    }

    public ResultadoVenta registrarVenta(Map<Integer, Integer> cantidades,
                                          double montoRecibido,
                                          String metodo) {
        validarVenta(cantidades, montoRecibido, metodo);

        int folio = siguienteFolio++;
        String fecha = LocalDate.now().toString();
        double total = calcularTotal(cantidades);
        double montoAplicado = Math.min(montoRecibido, total);
        double cambio = Math.max(0, montoRecibido - total);
        double saldo = Math.max(0, total - montoRecibido);
        String estado = saldo == 0 ? "Pagado" : "Pendiente";

        Pedido pedido = new Pedido(folio, fecha, estado, total, cliente);
        List<DetallePedido> detalles = new ArrayList<>();
        int totalArticulos = 0;
        int idDetalle = 1;

        for (Map.Entry<Integer, Integer> item : cantidades.entrySet()) {
            Producto producto = buscarProducto(item.getKey());
            int cantidad = item.getValue();
            double subtotal = producto.getPrecio() * cantidad;
            detalles.add(new DetallePedido(
                    idDetalle++, cantidad, producto.getPrecio(), subtotal, pedido, producto
            ));
            Stock stock = inventario.get(producto.getIdProducto());
            stock.actualizarStock(-cantidad);
            stock.setEstado(calcularEstadoStock(stock));
            totalArticulos += cantidad;
        }

        Pago pago = new Pago(folio, fecha, metodo, montoAplicado, pedido);
        Adeudo adeudo = new Adeudo(
                folio, fecha, total, montoAplicado, saldo,
                saldo == 0 ? "Liquidado" : "Pendiente", pago
        );
        ResultadoVenta resultado = new ResultadoVenta(
                folio, fecha, totalArticulos, total, montoRecibido, cambio,
                pago, adeudo, Collections.unmodifiableList(detalles)
        );
        ventas.add(resultado);
        return resultado;
    }

    public void cerrarConexion() {
        if (conexion == null) {
            return;
        }
        try {
            conexion.close();
        } catch (SQLException ignored) {
            // El cierre de la aplicación no depende de que MySQL mantenga abierta la conexión.
        }
    }

    private void validarVenta(Map<Integer, Integer> cantidades, double montoRecibido, String metodo) {
        if (cantidades == null || cantidades.isEmpty()) {
            throw new IllegalArgumentException("Agrega al menos un producto al carrito.");
        }
        for (Map.Entry<Integer, Integer> item : cantidades.entrySet()) {
            Producto producto = buscarProducto(item.getKey());
            Stock stock = inventario.get(item.getKey());
            if (producto == null || stock == null || item.getValue() <= 0) {
                throw new IllegalArgumentException("El carrito contiene un producto no válido.");
            }
            if (item.getValue() > stock.getCantidadActual()) {
                throw new IllegalArgumentException(
                        "No hay suficientes existencias de " + producto.getNombre() + "."
                );
            }
        }
        if (montoRecibido < 0) {
            throw new IllegalArgumentException("El monto recibido no puede ser negativo.");
        }
        if (metodo == null || metodo.isBlank()) {
            throw new IllegalArgumentException("Selecciona un método de pago.");
        }
    }

    private Producto buscarProducto(int idProducto) {
        for (Producto producto : productos) {
            if (producto.getIdProducto() == idProducto) {
                return producto;
            }
        }
        return null;
    }

    private void cargarCatalogoInicial() {
        Proveedor proveedor = new Proveedor(1, "Proveedor general", "9981001001", "ventas@proveedor.com");
        Categoria lacteos = new Categoria(1, "Lácteos", "Productos refrigerados");
        Categoria abarrotes = new Categoria(2, "Abarrotes", "Productos de despensa");
        Categoria bebidas = new Categoria(3, "Bebidas", "Bebidas y refrescos");

        agregarProducto(new Producto(1, "Leche Lala", "Leche Entera 1L", 28.50, lacteos, proveedor), 100);
        agregarProducto(new Producto(2, "Pan de caja", "Pan blanco 620 g", 42.00, abarrotes, proveedor), 55);
        agregarProducto(new Producto(3, "Arroz", "Bolsa de arroz 1 kg", 31.50, abarrotes, proveedor), 70);
        agregarProducto(new Producto(4, "Refresco", "Refresco de cola 600 ml", 20.00, bebidas, proveedor), 85);
        agregarProducto(new Producto(5, "Huevos", "Paquete con 12 piezas", 48.00, abarrotes, proveedor), 40);
    }

    private void agregarProducto(Producto producto, int cantidad) {
        productos.add(producto);
        inventario.put(producto.getIdProducto(), new Stock(
                producto.getIdProducto(), cantidad, 20, 150, "Disponible", producto
        ));
    }

    private String calcularEstadoStock(Stock stock) {
        if (stock.getCantidadActual() == 0) {
            return "Agotado";
        }
        if (stock.getCantidadActual() <= stock.getStockMinimo()) {
            return "Stock bajo";
        }
        return "Disponible";
    }

    public record ResultadoVenta(
            int folio,
            String fecha,
            int cantidad,
            double total,
            double recibido,
            double cambio,
            Pago pago,
            Adeudo adeudo,
            List<DetallePedido> detalles
    ) {
    }
}
