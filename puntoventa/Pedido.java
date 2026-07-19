package puntoventa;

public class Pedido {

    private int idPedido;
    private String fecha;
    private String estado;
    private double total;

    private Cliente cliente;

    public Pedido() {
    }

    public Pedido(int idPedido, String fecha, String estado,
                  double total, Cliente cliente) {

        this.idPedido = idPedido;
        this.fecha = fecha;
        this.estado = estado;
        this.total = total;
        this.cliente = cliente;

    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public void calcularTotal(){

        System.out.println("Total: $" + total);

    }

    @Override
    public String toString(){

        return "\n===== PEDIDO =====" +
                "\nID: " + idPedido +
                "\nFecha: " + fecha +
                "\nEstado: " + estado +
                "\nTotal: $" + total +
                "\nCliente: " + cliente.getNombre();

    }

}