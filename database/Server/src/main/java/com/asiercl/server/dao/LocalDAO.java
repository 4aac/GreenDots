package com.asiercl.server.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.asiercl.server.models.Local;
import com.asiercl.server.database.DatabaseConnection;
import java.time.LocalDate;

public class LocalDAO {

    // Método para insertar un local en la base de datos
    public void insertarLocal(Local local) {
        String sql = "INSERT INTO locales (nombre, categoria, fechaAdmision, ubicacion, descripcionTextual, ecosostenible, inclusionSocial, accesibilidad) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, local.getNombre());
            pstmt.setString(2, local.getCategoria());
            pstmt.setDate(3, Date.valueOf(local.getFechaAdmision()));
            pstmt.setString(4, local.getUbicacion());
            pstmt.setString(5, local.getDescripcionTextual());
            pstmt.setInt(6, local.getEcosostenible());
            pstmt.setInt(7, local.getInclusionSocial());
            pstmt.setInt(8, local.getAccesibilidad());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para obtener un local por su ID
    public Local obtenerLocalPorId(int id) {
        String sql = "SELECT * FROM locales WHERE id = ?";
        Local local = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                local = new Local(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("categoria"),
                        rs.getDate("fechaAdmision").toLocalDate(),
                        rs.getString("ubicacion"),
                        rs.getString("descripcionTextual"),
                        rs.getInt("ecosostenible"),
                        rs.getInt("inclusionSocial"),
                        rs.getInt("accesibilidad")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return local;
    }

    // Método para obtener todos los locales
    public List<Local> obtenerTodosLosLocales() {
        List<Local> locales = new ArrayList<>();
        String sql = "SELECT * FROM locales";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                locales.add(new Local(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("categoria"),
                        rs.getDate("fechaAdmision").toLocalDate(),
                        rs.getString("ubicacion"),
                        rs.getString("descripcionTextual"),
                        rs.getInt("ecosostenible"),
                        rs.getInt("inclusionSocial"),
                        rs.getInt("accesibilidad")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return locales;
    }

    // Método para eliminar un local por su ID
    public void eliminarLocalPorId(int id) {
        String sql = "DELETE FROM locales WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
