package puntoventa.modelo;

public class Pago {

    private int idPago;
    private String fecha;
    private String metodo;
    private double total;

    private Pedido pedido;

    public Pago() {
    }

    public Pago(int idPago, String fecha, String metodo,
                double total, Pedido pedido) {

        this.idPago = idPago;
        this.fecha = fecha;
        this.metodo = metodo;
        this.total = total;
        this.pedido = pedido;

    }

    public int getIdPago() {
        return idPago;
    }

    public void setIdPago(int idPago) {
        this.idPago = idPago;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public void procesarPago(){

        System.out.println("Pago procesado correctamente.");

    }

    @Override
    public String toString(){

        return "\n===== PAGO =====" +
                "\nID: " + idPago +
                "\nFecha: " + fecha +
                "\nMétodo: " + metodo +
                "\nTotal: $" + total;

    }

}
