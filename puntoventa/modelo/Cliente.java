package puntoventa.modelo;

public class Cliente extends Usuario {

    private int idCliente;
    private String direccion;

    public Cliente() {
        super();
    }

    public Cliente(int idCliente,
                   int idUsuario,
                   String nombre,
                   String correo,
                   String contrasena,
                   String telefono,
                   String direccion) {

        super(idUsuario, nombre, correo, contrasena, telefono);

        this.idCliente = idCliente;
        this.direccion = direccion;

    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void realizarPedido() {

        System.out.println("Pedido realizado.");

    }

    public void consultarAdeudo() {

        System.out.println("Consultando adeudo.");

    }

    @Override
    public String toString() {

        return super.toString() +

                "\nID Cliente: " + idCliente +

                "\nDirección: " + direccion;

    }

}
