package puntoventa;

import puntoventa.controlador.VentaControlador;
import puntoventa.vista.LoginFrame;
import puntoventa.vista.EstiloVisual;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EstiloVisual.aplicar();
            VentaControlador controlador = new VentaControlador();
            new LoginFrame(controlador).setVisible(true);
        });
    }
}
