package me.davidgarmo.soundseeker.product.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:h2:~/soundseeker-product";
    private static final String USER = "sa";
    private static final String PASSWORD = "sa";

    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static Connection getConnection(String initScript) throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver");
        String urlWithInitScript = URL + initScript;
        return DriverManager.getConnection(urlWithInitScript, USER, PASSWORD);
    }
}
