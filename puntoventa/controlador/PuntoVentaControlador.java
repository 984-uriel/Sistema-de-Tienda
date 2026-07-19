package puntoventa.controlador;

import puntoventa.modelo.Adeudo;
import puntoventa.modelo.Categoria;
import puntoventa.modelo.Cliente;
import puntoventa.modelo.Conexion;
import puntoventa.modelo.Pago;
import puntoventa.modelo.Pedido;
import puntoventa.modelo.Producto;
import puntoventa.modelo.Proveedor;
import puntoventa.modelo.Stock;
import puntoventa.vista.PuntoVentaVista;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

public class PuntoVentaControlador {

    private final PuntoVentaVista vista;
    private Cliente cliente;
    private Producto producto;
    private Stock stock;
    private Pedido pedido;
    private Pago pago;
    private Adeudo adeudo;

    public PuntoVentaControlador(PuntoVentaVista vista) {
        this.vista = vista;
        cargarDatosIniciales();
    }

    public void iniciar() {
        Connection conexion = Conexion.conectar();

        if (conexion == null) {
            vista.mostrarErrorConexion();
            return;
        }

        int opcion;
        do {
            opcion = vista.mostrarMenu();
            procesarOpcion(opcion);
        } while (opcion != 0);

        cerrarConexion(conexion);
    }

    private void procesarOpcion(int opcion) {
        switch (opcion) {
            case 1 -> vista.mostrarDatos(cliente, producto, stock, pedido, pago, adeudo);
            case 2 -> registrarCliente();
            case 3 -> registrarProducto();
            case 4 -> actualizarExistencias();
            case 5 -> registrarVenta();
            case 0 -> vista.mostrarMensaje("Sistema finalizado.");
            default -> vista.mostrarMensaje("Opción no válida. Intenta nuevamente.");
        }
    }

    private void registrarCliente() {
        int idCliente = vista.leerEntero("ID del cliente: ");
        int idUsuario = vista.leerEntero("ID del usuario: ");
        String nombre = vista.leerTexto("Nombre: ");
        String correo = vista.leerTexto("Correo: ");
        String contrasena = vista.leerTexto("Contraseña: ");
        String telefono = vista.leerTexto("Teléfono: ");
        String direccion = vista.leerTexto("Dirección: ");

        cliente = new Cliente(
                idCliente, idUsuario, nombre, correo, contrasena, telefono, direccion
        );
        vista.mostrarMensaje("Cliente registrado correctamente.");
    }

    private void registrarProducto() {
        int idProducto = vista.leerEntero("ID del producto: ");
        String nombre = vista.leerTexto("Nombre: ");
        String descripcion = vista.leerTexto("Descripción: ");
        double precio = vista.leerDecimal("Precio: $");

        if (precio < 0) {
            vista.mostrarMensaje("El precio no puede ser negativo.");
            return;
        }

        Categoria categoria = new Categoria(
                vista.leerEntero("ID de categoría: "),
                vista.leerTexto("Nombre de categoría: "),
                vista.leerTexto("Descripción de categoría: ")
        );

        Proveedor proveedor = new Proveedor(
                vista.leerEntero("ID del proveedor: "),
                vista.leerTexto("Nombre del proveedor: "),
                vista.leerTexto("Teléfono del proveedor: "),
                vista.leerTexto("Correo del proveedor: ")
        );

        producto = new Producto(idProducto, nombre, descripcion, precio, categoria, proveedor);
        int cantidad = vista.leerEntero("Cantidad inicial: ");
        stock = new Stock(idProducto, cantidad, 20, 150, calcularEstado(cantidad), producto);
        vista.mostrarMensaje("Producto registrado correctamente.");
    }

    private void actualizarExistencias() {
        int cantidad = vista.leerEntero("Cantidad a agregar (usa negativo para retirar): ");
        int nuevaCantidad = stock.getCantidadActual() + cantidad;

        if (nuevaCantidad < 0) {
            vista.mostrarMensaje("No hay existencias suficientes para realizar el retiro.");
            return;
        }

        stock.actualizarStock(cantidad);
        stock.setEstado(calcularEstado(stock.getCantidadActual()));
        vista.mostrarMensaje("Existencias actualizadas. Cantidad actual: " + stock.getCantidadActual());
    }

    private void registrarVenta() {
        int cantidad = vista.leerEntero("Cantidad a vender: ");
        if (cantidad <= 0 || cantidad > stock.getCantidadActual()) {
            vista.mostrarMensaje("La cantidad solicitada no es válida o supera las existencias.");
            return;
        }

        double total = cantidad * producto.getPrecio();
        double montoPagado = vista.leerDecimal("Total $" + total + ". Monto pagado: $");
        if (montoPagado < 0) {
            vista.mostrarMensaje("El monto pagado no puede ser negativo.");
            return;
        }

        int idPedido = vista.leerEntero("ID del pedido: ");
        String fecha = LocalDate.now().toString();
        String estadoPedido = montoPagado >= total ? "Pagado" : "Pendiente";
        pedido = new Pedido(idPedido, fecha, estadoPedido, total, cliente);
        pago = new Pago(idPedido, fecha, vista.leerTexto("Método de pago: "), montoPagado, pedido);

        double saldo = Math.max(0, total - montoPagado);
        String estadoAdeudo = saldo == 0 ? "Liquidado" : "Pendiente";
        adeudo = new Adeudo(idPedido, fecha, total, montoPagado, saldo, estadoAdeudo, pago);

        stock.actualizarStock(-cantidad);
        stock.setEstado(calcularEstado(stock.getCantidadActual()));
        vista.mostrarMensaje("Venta registrada. Total: $" + total + ", saldo pendiente: $" + saldo);
    }

    private String calcularEstado(int cantidad) {
        if (cantidad <= 0) {
            return "Agotado";
        }
        if (cantidad <= 20) {
            return "Stock bajo";
        }
        return "Disponible";
    }

    private void cargarDatosIniciales() {
        Categoria categoria = new Categoria(1, "Lácteos", "Productos Refrigerados");
        Proveedor proveedor = new Proveedor(1, "Sigma", "9981001001", "sigma@gmail.com");
        producto = new Producto(1, "Leche Lala", "Leche Entera 1L", 28.50, categoria, proveedor);
        stock = new Stock(1, 100, 20, 150, "Disponible", producto);
        cliente = new Cliente(
                1, 1, "Juan Pérez", "juan@gmail.com", "123456", "9991111111", "Av. Tulum 120"
        );
        pedido = new Pedido(1, "2025-07-17", "Pagado", 57.00, cliente);
        pago = new Pago(1, "2025-07-17", "Efectivo", 57.00, pedido);
        adeudo = new Adeudo(1, "2025-07-17", 57.00, 57.00, 0.00, "Liquidado", pago);
    }

    private void cerrarConexion(Connection conexion) {
        try {
            conexion.close();
        } catch (SQLException e) {
            vista.mostrarMensaje("No se pudo cerrar la conexión: " + e.getMessage());
        }
    }
}
