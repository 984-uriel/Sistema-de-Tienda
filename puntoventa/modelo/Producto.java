package puntoventa.modelo;

public class Producto {

    private int idProducto;
    private String nombre;
    private String descripcion;
    private double precio;

    private Categoria categoria;
    private Proveedor proveedor;

    public Producto() {
    }

    public Producto(int idProducto,
                    String nombre,
                    String descripcion,
                    double precio,
                    Categoria categoria,
                    Proveedor proveedor) {

        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.categoria = categoria;
        this.proveedor = proveedor;

    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    @Override
    public String toString() {

        return "\n===== PRODUCTO =====" +
                "\nID: " + idProducto +
                "\nNombre: " + nombre +
                "\nDescripción: " + descripcion +
                "\nPrecio: $" + precio +
                "\nCategoría: " + categoria.getNombre() +
                "\nProveedor: " + proveedor.getNombre();

    }

}
