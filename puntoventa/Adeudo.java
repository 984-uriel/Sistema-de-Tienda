package puntoventa;

public class Adeudo {

    private int idAdeudo;
    private String fecha;
    private double montoTotal;
    private double montoPagado;
    private double saldoPendiente;
    private String estado;

    private Pago pago;

    public Adeudo() {
    }

    public Adeudo(int idAdeudo, String fecha, double montoTotal,
                  double montoPagado, double saldoPendiente,
                  String estado, Pago pago) {

        this.idAdeudo = idAdeudo;
        this.fecha = fecha;
        this.montoTotal = montoTotal;
        this.montoPagado = montoPagado;
        this.saldoPendiente = saldoPendiente;
        this.estado = estado;
        this.pago = pago;

    }

    public int getIdAdeudo() {
        return idAdeudo;
    }

    public void setIdAdeudo(int idAdeudo) {
        this.idAdeudo = idAdeudo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public double getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(double montoTotal) {
        this.montoTotal = montoTotal;
    }

    public double getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(double montoPagado) {
        this.montoPagado = montoPagado;
    }

    public double getSaldoPendiente() {
        return saldoPendiente;
    }

    public void setSaldoPendiente(double saldoPendiente) {
        this.saldoPendiente = saldoPendiente;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Pago getPago() {
        return pago;
    }

    public void setPago(Pago pago) {
        this.pago = pago;
    }

    public void calcularSaldo() {

        saldoPendiente = montoTotal - montoPagado;

    }

    @Override
    public String toString() {

        return "\n===== ADEUDO =====" +
                "\nID: " + idAdeudo +
                "\nFecha: " + fecha +
                "\nMonto Total: $" + montoTotal +
                "\nMonto Pagado: $" + montoPagado +
                "\nSaldo Pendiente: $" + saldoPendiente +
                "\nEstado: " + estado;

    }

}