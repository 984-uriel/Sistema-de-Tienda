package puntoventa.vista;

import puntoventa.modelo.Adeudo;
import puntoventa.modelo.Cliente;
import puntoventa.modelo.Pago;
import puntoventa.modelo.Pedido;
import puntoventa.modelo.Producto;
import puntoventa.modelo.Stock;

import java.util.Scanner;

public class PuntoVentaVista {

    private final Scanner scanner = new Scanner(System.in);

    public int mostrarMenu() {
        System.out.println("\n===== PUNTO DE VENTA =====");
        System.out.println("1. Mostrar información actual");
        System.out.println("2. Registrar o cambiar cliente");
        System.out.println("3. Registrar o cambiar producto");
        System.out.println("4. Actualizar existencias");
        System.out.println("5. Registrar venta");
        System.out.println("0. Salir");
        return leerEntero("Selecciona una opción: ");
    }

    public String leerTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String valor = scanner.nextLine().trim();
            if (!valor.isEmpty()) {
                return valor;
            }
            mostrarMensaje("El dato no puede quedar vacío.");
        }
    }

    public int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String valor = scanner.nextLine().trim();
            try {
                return Integer.parseInt(valor);
            } catch (NumberFormatException e) {
                mostrarMensaje("Escribe un número entero válido.");
            }
        }
    }

    public double leerDecimal(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String valor = scanner.nextLine().trim().replace(',', '.');
            try {
                return Double.parseDouble(valor);
            } catch (NumberFormatException e) {
                mostrarMensaje("Escribe una cantidad válida.");
            }
        }
    }

    public void mostrarDatos(Cliente cliente,
                             Producto producto,
                             Stock stock,
                             Pedido pedido,
                             Pago pago,
                             Adeudo adeudo) {
        System.out.println(cliente);
        System.out.println(producto);
        System.out.println(stock);
        System.out.println(pedido);
        System.out.println(pago);
        System.out.println(adeudo);
    }

    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }

    public void mostrarErrorConexion() {
        mostrarMensaje("No fue posible iniciar el sistema porque no hay conexión con la base de datos.");
    }
}
