package puntoventa;

public class Usuario {

    private int idUsuario;
    private String nombre;
    private String correo;
    private String contrasena;
    private String telefono;

    public Usuario() {
    }

    public Usuario(int idUsuario, String nombre, String correo,
                   String contrasena, String telefono) {

        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
        this.telefono = telefono;

    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public boolean iniciarSesion() {

        System.out.println("Sesión iniciada.");

        return true;

    }

    public void cerrarSesion() {

        System.out.println("Sesión cerrada.");

    }

    @Override
    public String toString() {

        return "\n===== USUARIO =====" +
                "\nID: " + idUsuario +
                "\nNombre: " + nombre +
                "\nCorreo: " + correo +
                "\nTeléfono: " + telefono;

    }

}