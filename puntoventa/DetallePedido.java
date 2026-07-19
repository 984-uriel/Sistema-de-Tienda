package puntoventa;

public class DetallePedido {

    private int idDetalle;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;

    private Pedido pedido;
    private Producto producto;

    public DetallePedido() {
    }

    public DetallePedido(int idDetalle, int cantidad,
                         double precioUnitario,
                         double subtotal,
                         Pedido pedido,
                         Producto producto) {

        this.idDetalle = idDetalle;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
        this.pedido = pedido;
        this.producto = producto;

    }

    public int getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public void calcularSubtotal(){

        subtotal = cantidad * precioUnitario;

    }

    @Override
    public String toString(){

        return "\n===== DETALLE PEDIDO =====" +
                "\nProducto: " + producto.getNombre() +
                "\nCantidad: " + cantidad +
                "\nPrecio: $" + precioUnitario +
                "\nSubtotal: $" + subtotal;

    }

}