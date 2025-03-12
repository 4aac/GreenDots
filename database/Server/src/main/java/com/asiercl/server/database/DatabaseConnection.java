package com.asiercl.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/DBAPP";
    private static final String USUARIO = "user";
    private static final String CONTRASENA = "12345678";

    public static Connection getConnection() throws SQLException {
        try {
            // Registro explícito del driver
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("No se pudo cargar el driver de PostgreSQL", e);
        }

        return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
    }
}
