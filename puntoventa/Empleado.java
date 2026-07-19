package puntoventa;

public class Empleado extends Usuario {

    private int idEmpleado;
    private String puesto;
    private double salario;

    public Empleado() {
        super();
    }

    public Empleado(int idEmpleado,
                    int idUsuario,
                    String nombre,
                    String correo,
                    String contrasena,
                    String telefono,
                    String puesto,
                    double salario) {

        super(idUsuario, nombre, correo, contrasena, telefono);

        this.idEmpleado = idEmpleado;
        this.puesto = puesto;
        this.salario = salario;

    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    public void registrarVenta() {

        System.out.println("Venta registrada.");

    }

    public void actualizarStock() {

        System.out.println("Stock actualizado.");

    }

    @Override
    public String toString() {

        return super.toString() +

                "\nID Empleado: " + idEmpleado +

                "\nPuesto: " + puesto +

                "\nSalario: $" + salario;

    }

}