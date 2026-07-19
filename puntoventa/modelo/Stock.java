package puntoventa.modelo;

public class Stock {

    private int idStock;
    private int cantidadActual;
    private int stockMinimo;
    private int stockMaximo;
    private String estado;

    private Producto producto;

    public Stock() {
    }

    public Stock(int idStock, int cantidadActual, int stockMinimo,
                 int stockMaximo, String estado, Producto producto) {

        this.idStock = idStock;
        this.cantidadActual = cantidadActual;
        this.stockMinimo = stockMinimo;
        this.stockMaximo = stockMaximo;
        this.estado = estado;
        this.producto = producto;

    }

    public int getIdStock() {
        return idStock;
    }

    public void setIdStock(int idStock) {
        this.idStock = idStock;
    }

    public int getCantidadActual() {
        return cantidadActual;
    }

    public void setCantidadActual(int cantidadActual) {
        this.cantidadActual = cantidadActual;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public int getStockMaximo() {
        return stockMaximo;
    }

    public void setStockMaximo(int stockMaximo) {
        this.stockMaximo = stockMaximo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public void actualizarStock(int cantidad){

        this.cantidadActual += cantidad;

    }

    @Override
    public String toString() {

        return "\n===== STOCK =====" +
                "\nProducto: " + producto.getNombre() +
                "\nCantidad: " + cantidadActual +
                "\nMínimo: " + stockMinimo +
                "\nMáximo: " + stockMaximo +
                "\nEstado: " + estado;

    }

}
