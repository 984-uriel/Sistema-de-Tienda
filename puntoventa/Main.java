package puntoventa;

import java.sql.Connection;

public class Main {

    public static void main(String[] args) {

        Connection conexion = Conexion.conectar();

        if (conexion != null) {

            Categoria categoria =
                    new Categoria(1,
                            "Lácteos",
                            "Productos Refrigerados");

            Proveedor proveedor =
                    new Proveedor(1,
                            "Sigma",
                            "9981001001",
                            "sigma@gmail.com");

            Producto producto =
                    new Producto(
                            1,
                            "Leche Lala",
                            "Leche Entera 1L",
                            28.50,
                            categoria,
                            proveedor
                    );

            Stock stock =
                    new Stock(
                            1,
                            100,
                            20,
                            150,
                            "Disponible",
                            producto
                    );

            Cliente cliente =
                    new Cliente(
                            1,
                            1,
                            "Juan Pérez",
                            "juan@gmail.com",
                            "123456",
                            "9991111111",
                            "Av. Tulum 120"
                    );

            Pedido pedido =
                    new Pedido(
                            1,
                            "2025-07-17",
                            "Pagado",
                            57.00,
                            cliente
                    );

            Pago pago =
                    new Pago(
                            1,
                            "2025-07-17",
                            "Efectivo",
                            57.00,
                            pedido
                    );

            Adeudo adeudo =
                    new Adeudo(
                            1,
                            "2025-07-17",
                            57.00,
                            57.00,
                            0.00,
                            "Liquidado",
                            pago
                    );

            System.out.println(cliente);
            System.out.println(producto);
            System.out.println(stock);
            System.out.println(pedido);
            System.out.println(pago);
            System.out.println(adeudo);

        }

    }

}