package puntoventa;

public class Administrador extends Usuario {

    private int idAdministrador;
    private String cargo;

    public Administrador() {
        super();
    }

    public Administrador(int idAdministrador,
                         int idUsuario,
                         String nombre,
                         String correo,
                         String contrasena,
                         String telefono,
                         String cargo) {

        super(idUsuario, nombre, correo, contrasena, telefono);

        this.idAdministrador = idAdministrador;
        this.cargo = cargo;

    }

    public int getIdAdministrador() {
        return idAdministrador;
    }

    public void setIdAdministrador(int idAdministrador) {
        this.idAdministrador = idAdministrador;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public void generarReporte() {

        System.out.println("Reporte generado.");

    }

    public void administrarUsuarios() {

        System.out.println("Administrando usuarios.");

    }

    @Override
    public String toString() {

        return super.toString() +
                "\nID Administrador: " + idAdministrador +
                "\nCargo: " + cargo;

    }

}