package puntoventa.modelo;

import java.sql.Connection;
import java.sql.DriverManager;

public class Conexion {

    private static final String URL =
            "jdbc:mysql://localhost:3306/puntoventa";

    private static final String USER = "root";

    private static final String PASSWORD = "";

    public static Connection conectar() {

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conexion = DriverManager.getConnection(
                    URL,
                    USER,
                    PASSWORD
            );

            System.out.println("Conexión Exitosa.");

            return conexion;

        } catch (Exception e) {

            System.out.println("Error de conexión");

            System.out.println(e.getMessage());

            return null;

        }

    }

}
