package puntoventa;

import puntoventa.controlador.VentaControlador;
import puntoventa.vista.PuntoVentaFrame;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PuntoVentaFrame.aplicarEstiloDelSistema();
            VentaControlador controlador = new VentaControlador();
            PuntoVentaFrame vista = new PuntoVentaFrame(controlador);
            vista.setVisible(true);
        });
    }
}
