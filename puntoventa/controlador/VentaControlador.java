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
import puntoventa.modelo.Usuario;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    private final List<Cliente> clientes = new ArrayList<>();
    private Connection conexion;
    private int siguienteFolio = 2;

    public VentaControlador() {
    }

    public boolean conectar() {
        conexion = Conexion.conectar();
        if (conexion == null) return false;
        try {
            cargarProductosDesdeBaseDeDatos();
            cargarClientesDesdeBaseDeDatos();
            return true;
        } catch (SQLException e) {
            System.out.println("No se pudieron cargar los productos: " + e.getMessage());
            cerrarConexion();
            conexion = null;
            return false;
        }
    }

    public boolean estaConectado() {
        try {
            return conexion != null && !conexion.isClosed();
        } catch (SQLException e) {
            return false;
        }
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

    public List<Cliente> getClientes() {
        return Collections.unmodifiableList(clientes);
    }

    public List<ProveedorResumen> getProveedoresResumen() {
        String sql = "SELECT pr.idProveedor, pr.nombre, pr.telefono, pr.correo, "
                + "COUNT(p.idProducto) productos FROM Proveedor pr "
                + "LEFT JOIN Producto p ON p.idProveedor = pr.idProveedor "
                + "GROUP BY pr.idProveedor, pr.nombre, pr.telefono, pr.correo ORDER BY pr.nombre";
        List<ProveedorResumen> datos = new ArrayList<>();
        try (PreparedStatement consulta = conexion.prepareStatement(sql);
             ResultSet resultado = consulta.executeQuery()) {
            while (resultado.next()) {
                datos.add(new ProveedorResumen(resultado.getInt("idProveedor"),
                        resultado.getString("nombre"), resultado.getString("telefono"),
                        resultado.getString("correo"), resultado.getInt("productos")));
            }
            return datos;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudieron cargar los proveedores: " + e.getMessage(), e);
        }
    }

    public List<CategoriaResumen> getCategoriasResumen() {
        String sql = "SELECT c.idCategoria, c.nombre, c.descripcion, COUNT(p.idProducto) productos "
                + "FROM Categoria c LEFT JOIN Producto p ON p.idCategoria = c.idCategoria "
                + "GROUP BY c.idCategoria, c.nombre, c.descripcion ORDER BY c.nombre";
        List<CategoriaResumen> datos = new ArrayList<>();
        try (PreparedStatement consulta = conexion.prepareStatement(sql);
             ResultSet resultado = consulta.executeQuery()) {
            while (resultado.next()) {
                datos.add(new CategoriaResumen(resultado.getInt("idCategoria"),
                        resultado.getString("nombre"), resultado.getString("descripcion"),
                        resultado.getInt("productos")));
            }
            return datos;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudieron cargar las categorías: " + e.getMessage(), e);
        }
    }

    public List<PedidoResumen> getPedidosResumen() {
        String sql = "SELECT pe.idPedido, pe.fecha, u.nombre cliente, pe.estado, pe.total, "
                + "COALESCE(SUM(d.cantidad), 0) articulos FROM Pedido pe "
                + "INNER JOIN Cliente c ON c.idCliente = pe.idCliente "
                + "INNER JOIN Usuario u ON u.idUsuario = c.idUsuario "
                + "LEFT JOIN Detalle_Pedido d ON d.idPedido = pe.idPedido "
                + "GROUP BY pe.idPedido, pe.fecha, u.nombre, pe.estado, pe.total "
                + "ORDER BY pe.fecha DESC, pe.idPedido DESC";
        List<PedidoResumen> datos = new ArrayList<>();
        try (PreparedStatement consulta = conexion.prepareStatement(sql);
             ResultSet resultado = consulta.executeQuery()) {
            while (resultado.next()) {
                datos.add(new PedidoResumen(resultado.getInt("idPedido"), resultado.getString("fecha"),
                        resultado.getString("cliente"), resultado.getInt("articulos"),
                        resultado.getDouble("total"), resultado.getString("estado")));
            }
            return datos;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudieron cargar los pedidos: " + e.getMessage(), e);
        }
    }

    public List<PagoResumen> getPagosResumen() {
        String sql = "SELECT pa.idPago, pa.fecha, pa.idPedido, u.nombre cliente, pa.metodo, pa.total "
                + "FROM Pago pa INNER JOIN Pedido pe ON pe.idPedido = pa.idPedido "
                + "INNER JOIN Cliente c ON c.idCliente = pe.idCliente "
                + "INNER JOIN Usuario u ON u.idUsuario = c.idUsuario "
                + "ORDER BY pa.fecha DESC, pa.idPago DESC";
        List<PagoResumen> datos = new ArrayList<>();
        try (PreparedStatement consulta = conexion.prepareStatement(sql);
             ResultSet resultado = consulta.executeQuery()) {
            while (resultado.next()) {
                datos.add(new PagoResumen(resultado.getInt("idPago"), resultado.getString("fecha"),
                        resultado.getInt("idPedido"), resultado.getString("cliente"),
                        resultado.getString("metodo"), resultado.getDouble("total")));
            }
            return datos;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudieron cargar los pagos: " + e.getMessage(), e);
        }
    }

    public List<PersonalResumen> getPersonalResumen() {
        String sql = "SELECT u.idUsuario, u.nombre, u.correo, u.telefono, 'Empleado' tipo, e.puesto cargo "
                + "FROM Empleado e INNER JOIN Usuario u ON u.idUsuario = e.idUsuario "
                + "UNION ALL SELECT u.idUsuario, u.nombre, u.correo, u.telefono, "
                + "'Administrador' tipo, a.cargo FROM Administrador a "
                + "INNER JOIN Usuario u ON u.idUsuario = a.idUsuario ORDER BY nombre";
        List<PersonalResumen> datos = new ArrayList<>();
        try (PreparedStatement consulta = conexion.prepareStatement(sql);
             ResultSet resultado = consulta.executeQuery()) {
            while (resultado.next()) {
                datos.add(new PersonalResumen(resultado.getInt("idUsuario"), resultado.getString("nombre"),
                        resultado.getString("correo"), resultado.getString("telefono"),
                        resultado.getString("tipo"), resultado.getString("cargo")));
            }
            return datos;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo cargar el personal: " + e.getMessage(), e);
        }
    }

    public Stock getStock(int idProducto) {
        return inventario.get(idProducto);
    }

    public Usuario autenticar(String correo, char[] contrasena) {
        if (conexion == null) throw new IllegalStateException("No hay conexión con la base de datos.");
        String usuario = correo == null ? "" : correo.trim();
        String clave = contrasena == null ? "" : new String(contrasena);
        if (usuario.isEmpty() || clave.isEmpty()) {
            throw new IllegalArgumentException("Escribe el usuario y la contraseña.");
        }
        String sql = "SELECT idUsuario, nombre, correo, contrasena, telefono FROM Usuario "
                + "WHERE (correo = ? OR nombre = ?) AND contrasena = ? LIMIT 1";
        try (PreparedStatement consulta = conexion.prepareStatement(sql)) {
            consulta.setString(1, usuario);
            consulta.setString(2, usuario);
            consulta.setString(3, clave);
            try (ResultSet resultado = consulta.executeQuery()) {
                if (!resultado.next()) return null;
                return new Usuario(resultado.getInt("idUsuario"), resultado.getString("nombre"),
                        resultado.getString("correo"), resultado.getString("contrasena"),
                        resultado.getString("telefono"));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo validar el usuario: " + e.getMessage(), e);
        }
    }

    public Producto buscarPorCodigoBarras(String codigo) {
        String limpio = codigo == null ? "" : codigo.trim();
        for (Producto producto : productos) {
            if (limpio.equals(producto.getCodigoBarras())) return producto;
        }
        return null;
    }

    public List<AdeudoPendiente> getAdeudosPendientes() {
        if (!estaConectado()) return Collections.emptyList();
        String sql = "SELECT a.idAdeudo, pe.idPedido, u.nombre cliente, a.fecha, "
                + "a.montoTotal, a.montoPagado, a.saldoPendiente, a.estado "
                + "FROM Adeudo a INNER JOIN Pago pa ON pa.idPago = a.idPago "
                + "INNER JOIN Pedido pe ON pe.idPedido = pa.idPedido "
                + "INNER JOIN Cliente c ON c.idCliente = pe.idCliente "
                + "INNER JOIN Usuario u ON u.idUsuario = c.idUsuario "
                + "WHERE a.saldoPendiente > 0 ORDER BY a.fecha, u.nombre";
        List<AdeudoPendiente> adeudos = new ArrayList<>();
        try (PreparedStatement consulta = conexion.prepareStatement(sql);
             ResultSet resultado = consulta.executeQuery()) {
            while (resultado.next()) {
                adeudos.add(new AdeudoPendiente(resultado.getInt("idAdeudo"),
                        resultado.getInt("idPedido"), resultado.getString("cliente"),
                        resultado.getString("fecha"), resultado.getDouble("montoTotal"),
                        resultado.getDouble("montoPagado"), resultado.getDouble("saldoPendiente"),
                        resultado.getString("estado")));
            }
            for (ResultadoVenta venta : ventas) {
                if (venta.adeudo().getSaldoPendiente() > 0 && venta.cliente() != null) {
                    adeudos.add(new AdeudoPendiente(-venta.folio(), venta.folio(),
                            venta.cliente().getNombre(), venta.fecha(), venta.total(),
                            venta.adeudo().getMontoPagado(), venta.adeudo().getSaldoPendiente(),
                            venta.adeudo().getEstado()));
                }
            }
            return adeudos;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudieron cargar los adeudos: " + e.getMessage(), e);
        }
    }

    public void registrarAbono(int idAdeudo, double abono) {
        if (!estaConectado()) throw new IllegalStateException("No hay conexión con la base de datos.");
        if (abono <= 0) throw new IllegalArgumentException("El abono debe ser mayor que cero.");
        if (idAdeudo < 0) {
            registrarAbonoDeSesion(-idAdeudo, abono);
            return;
        }
        try {
            conexion.setAutoCommit(false);
            double pagado;
            double saldo;
            int idPago;
            try (PreparedStatement consulta = conexion.prepareStatement(
                    "SELECT montoPagado, saldoPendiente, idPago FROM Adeudo WHERE idAdeudo = ? FOR UPDATE")) {
                consulta.setInt(1, idAdeudo);
                try (ResultSet resultado = consulta.executeQuery()) {
                    if (!resultado.next()) throw new IllegalArgumentException("El adeudo ya no existe.");
                    pagado = resultado.getDouble("montoPagado");
                    saldo = resultado.getDouble("saldoPendiente");
                    idPago = resultado.getInt("idPago");
                }
            }
            if (saldo <= 0) throw new IllegalArgumentException("Este adeudo ya está liquidado.");
            if (abono > saldo + 0.001) throw new IllegalArgumentException("El abono no puede superar el saldo pendiente.");
            double nuevoPagado = pagado + abono;
            double nuevoSaldo = Math.max(0, saldo - abono);
            String estado = nuevoSaldo == 0 ? "Liquidado" : "Pendiente";
            try (PreparedStatement actualizar = conexion.prepareStatement(
                    "UPDATE Adeudo SET montoPagado = ?, saldoPendiente = ?, estado = ? WHERE idAdeudo = ?")) {
                actualizar.setDouble(1, nuevoPagado);
                actualizar.setDouble(2, nuevoSaldo);
                actualizar.setString(3, estado);
                actualizar.setInt(4, idAdeudo);
                actualizar.executeUpdate();
            }
            try (PreparedStatement actualizarPago = conexion.prepareStatement(
                    "UPDATE Pago SET total = ? WHERE idPago = ?")) {
                actualizarPago.setDouble(1, nuevoPagado);
                actualizarPago.setInt(2, idPago);
                actualizarPago.executeUpdate();
            }
            if (nuevoSaldo == 0) {
                try (PreparedStatement actualizarPedido = conexion.prepareStatement(
                        "UPDATE Pedido pe INNER JOIN Pago pa ON pa.idPedido = pe.idPedido "
                                + "SET pe.estado = 'Pagado' WHERE pa.idPago = ?")) {
                    actualizarPedido.setInt(1, idPago);
                    actualizarPedido.executeUpdate();
                }
            }
            conexion.commit();
        } catch (SQLException e) {
            try { conexion.rollback(); } catch (SQLException ignored) { }
            throw new IllegalStateException("No se pudo registrar el abono: " + e.getMessage(), e);
        } finally {
            try { conexion.setAutoCommit(true); } catch (SQLException ignored) { }
        }
    }

    private void registrarAbonoDeSesion(int folio, double abono) {
        for (ResultadoVenta venta : ventas) {
            if (venta.folio() != folio) continue;
            Adeudo adeudo = venta.adeudo();
            if (adeudo.getSaldoPendiente() <= 0) {
                throw new IllegalArgumentException("Este adeudo ya está liquidado.");
            }
            if (abono > adeudo.getSaldoPendiente() + 0.001) {
                throw new IllegalArgumentException("El abono no puede superar el saldo pendiente.");
            }
            adeudo.setMontoPagado(adeudo.getMontoPagado() + abono);
            adeudo.setSaldoPendiente(Math.max(0, adeudo.getSaldoPendiente() - abono));
            adeudo.setEstado(adeudo.getSaldoPendiente() == 0 ? "Liquidado" : "Pendiente");
            venta.pago().setTotal(adeudo.getMontoPagado());
            if (adeudo.getSaldoPendiente() == 0) venta.pago().getPedido().setEstado("Pagado");
            return;
        }
        throw new IllegalArgumentException("El adeudo ya no existe.");
    }

    public Cliente registrarCliente(String nombre, String correo, String telefono, String direccion) {
        if (conexion == null) {
            throw new IllegalStateException("No hay conexión con la base de datos.");
        }
        nombre = validarTexto(nombre, "nombre");
        correo = validarTexto(correo, "correo");
        telefono = validarTexto(telefono, "teléfono");
        direccion = validarTexto(direccion, "dirección");

        try {
            conexion.setAutoCommit(false);
            int idUsuario;
            try (PreparedStatement sentencia = conexion.prepareStatement(
                    "INSERT INTO Usuario(nombre, correo, contrasena, telefono) VALUES (?, ?, ?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                sentencia.setString(1, nombre);
                sentencia.setString(2, correo);
                sentencia.setString(3, "");
                sentencia.setString(4, telefono);
                sentencia.executeUpdate();
                try (ResultSet claves = sentencia.getGeneratedKeys()) {
                    if (!claves.next()) throw new SQLException("No se obtuvo el ID del usuario.");
                    idUsuario = claves.getInt(1);
                }
            }
            int idCliente;
            try (PreparedStatement sentencia = conexion.prepareStatement(
                    "INSERT INTO Cliente(direccion, idUsuario) VALUES (?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                sentencia.setString(1, direccion);
                sentencia.setInt(2, idUsuario);
                sentencia.executeUpdate();
                try (ResultSet claves = sentencia.getGeneratedKeys()) {
                    if (!claves.next()) throw new SQLException("No se obtuvo el ID del cliente.");
                    idCliente = claves.getInt(1);
                }
            }
            conexion.commit();
            Cliente cliente = new Cliente(idCliente, idUsuario, nombre, correo, "", telefono, direccion);
            clientes.add(cliente);
            return cliente;
        } catch (SQLException e) {
            try { conexion.rollback(); } catch (SQLException ignored) { }
            throw new IllegalStateException("No se pudo registrar el cliente: " + e.getMessage(), e);
        } finally {
            try { conexion.setAutoCommit(true); } catch (SQLException ignored) { }
        }
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
                                          String metodo,
                                          Cliente cliente) {
        validarVenta(cantidades, montoRecibido, metodo, cliente);

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
                cliente, pago, adeudo, Collections.unmodifiableList(detalles)
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

    private void validarVenta(Map<Integer, Integer> cantidades, double montoRecibido, String metodo,
                              Cliente cliente) {
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
        if ((montoRecibido < calcularTotal(cantidades) || "Crédito".equalsIgnoreCase(metodo))
                && cliente == null) {
            throw new IllegalArgumentException(
                    "Para dejar saldo pendiente debes seleccionar un cliente registrado."
            );
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

    private void cargarProductosDesdeBaseDeDatos() throws SQLException {
        String columnaCodigo = existeColumnaCodigoBarras()
                ? "p.codigoBarras"
                : "CAST(p.idProducto AS CHAR) AS codigoBarras";
        String sql = "SELECT p.idProducto, p.nombre, p.descripcion, p.precio, " + columnaCodigo + ", "
                + "c.idCategoria, c.nombre categoriaNombre, c.descripcion categoriaDescripcion, "
                + "pr.idProveedor, pr.nombre proveedorNombre, pr.telefono, pr.correo, "
                + "s.idStock, s.cantidadActual, s.stockMinimo, s.stockMaximo, s.estado "
                + "FROM Producto p "
                + "INNER JOIN Categoria c ON c.idCategoria = p.idCategoria "
                + "INNER JOIN Proveedor pr ON pr.idProveedor = p.idProveedor "
                + "LEFT JOIN Stock s ON s.idProducto = p.idProducto ORDER BY p.nombre";

        productos.clear();
        inventario.clear();
        try (PreparedStatement consulta = conexion.prepareStatement(sql);
             ResultSet resultado = consulta.executeQuery()) {
            while (resultado.next()) {
                Categoria categoria = new Categoria(resultado.getInt("idCategoria"),
                        resultado.getString("categoriaNombre"), resultado.getString("categoriaDescripcion"));
                Proveedor proveedor = new Proveedor(resultado.getInt("idProveedor"),
                        resultado.getString("proveedorNombre"), resultado.getString("telefono"),
                        resultado.getString("correo"));
                Producto producto = new Producto(resultado.getInt("idProducto"),
                        resultado.getString("nombre"), resultado.getString("descripcion"),
                        resultado.getDouble("precio"), resultado.getString("codigoBarras"), categoria, proveedor);
                int idStock = resultado.getInt("idStock");
                Stock stock = new Stock(idStock, resultado.getInt("cantidadActual"),
                        resultado.getInt("stockMinimo"), resultado.getInt("stockMaximo"),
                        idStock == 0 ? "Sin stock registrado" : resultado.getString("estado"), producto);
                productos.add(producto);
                inventario.put(producto.getIdProducto(), stock);
            }
        }
    }

    private boolean existeColumnaCodigoBarras() throws SQLException {
        DatabaseMetaData metadata = conexion.getMetaData();
        try (ResultSet columnas = metadata.getColumns(conexion.getCatalog(), null, "%", "%")) {
            while (columnas.next()) {
                if ("Producto".equalsIgnoreCase(columnas.getString("TABLE_NAME"))
                        && "codigoBarras".equalsIgnoreCase(columnas.getString("COLUMN_NAME"))) {
                    return true;
                }
            }
        }
        return false;
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

    private void cargarClientesDesdeBaseDeDatos() throws SQLException {
        String sql = "SELECT c.idCliente, c.direccion, u.idUsuario, u.nombre, u.correo, "
                + "u.contrasena, u.telefono FROM Cliente c "
                + "INNER JOIN Usuario u ON u.idUsuario = c.idUsuario ORDER BY u.nombre";
        clientes.clear();
        try (PreparedStatement consulta = conexion.prepareStatement(sql);
             ResultSet resultado = consulta.executeQuery()) {
            while (resultado.next()) {
                clientes.add(new Cliente(resultado.getInt("idCliente"), resultado.getInt("idUsuario"),
                        resultado.getString("nombre"), resultado.getString("correo"),
                        resultado.getString("contrasena"), resultado.getString("telefono"),
                        resultado.getString("direccion")));
            }
        }
    }

    private String validarTexto(String valor, String campo) {
        String limpio = valor == null ? "" : valor.trim();
        if (limpio.isEmpty()) {
            throw new IllegalArgumentException("Escribe el " + campo + " del cliente.");
        }
        return limpio;
    }

    public record ResultadoVenta(
            int folio,
            String fecha,
            int cantidad,
            double total,
            double recibido,
            double cambio,
            Cliente cliente,
            Pago pago,
            Adeudo adeudo,
            List<DetallePedido> detalles
    ) {
    }

    public record AdeudoPendiente(int idAdeudo, int folio, String cliente, String fecha,
                                  double montoTotal, double montoPagado, double saldoPendiente,
                                  String estado) {
    }

    public record ProveedorResumen(int id, String nombre, String telefono, String correo,
                                   int productos) { }

    public record CategoriaResumen(int id, String nombre, String descripcion, int productos) { }

    public record PedidoResumen(int folio, String fecha, String cliente, int articulos,
                                double total, String estado) { }

    public record PagoResumen(int id, String fecha, int folio, String cliente, String metodo,
                              double total) { }

    public record PersonalResumen(int idUsuario, String nombre, String correo, String telefono,
                                  String tipo, String cargo) { }
}
