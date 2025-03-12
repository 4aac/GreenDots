package com.asiercl.server.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.asiercl.server.models.Usuario;
import com.asiercl.server.database.DatabaseConnection;

public class UsuarioDAO {

    // Método para insertar un usuario en la base de datos
    public void insertarUsuario(Usuario usuario) {
        String sql = "INSERT INTO Usuarios (nickname, nombreCompleto, email, password, fechaCreacion, admin, fotoPerfil) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, usuario.getNickname());
            pstmt.setString(2, usuario.getNombreCompleto());
            pstmt.setString(3, usuario.getEmail());  // Se agrega la contraseña cifrada
            pstmt.setString(4, usuario.getPassword()); // Se agrega la fecha de creación
            pstmt.setTimestamp(5, usuario.getFechaCreacion()); // Se agrega la fecha de creación
            pstmt.setBoolean(6, usuario.isAdmin()); // Se agrega la fecha de creación
            pstmt.setBytes(7, usuario.getFotoPerfil()); // Se agrega la fecha de creación

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para obtener un usuario por su ID
    public Usuario obtenerUsuarioPorNickname(String nickname) {
        String sql = "SELECT * FROM usuarios WHERE nickname = ?";
        Usuario usuario = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nickname);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Se actualiza para incluir la contraseña y la fecha de creación
                usuario = new Usuario(
                        rs.getString("nickname"),
                        rs.getString("nombreCompleto"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getTimestamp("fechaCreacion"),
                        rs.getBoolean("admin"),
                        rs.getBytes("fotoPerfil"),
                        new ArrayList<>() // Favoritos se manejan por separado
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuario;
    }

    // Método para obtener todos los usuarios
    public List<Usuario> obtenerTodosLosUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // Se actualiza para incluir la contraseña y la fecha de creación
                usuarios.add(new Usuario(
                        rs.getString("nickname"),
                        rs.getString("nombreCompleto"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getTimestamp("fechaCreacion"),
                        rs.getBoolean("admin"),
                        rs.getBytes("fotoPerfil"),
                        new ArrayList<>()
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    // Método para eliminar un usuario por su nickname
    public void eliminarUsuarioPorNickname(String nickname) {
        String sql = "DELETE FROM usuarios WHERE nickname = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nickname);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void actualizarUsuario(Usuario usuario) {
        String sql = "UPDATE usuarios SET nombrecompleto = ?, email = ?, password = ?, admin = ?, fotoperfil = ? WHERE nickname = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, usuario.getNombreCompleto());
            pstmt.setString(2, usuario.getEmail());  // Se agrega la contraseña cifrada
            pstmt.setString(3, usuario.getPassword()); // Se agrega la fecha de creación
            pstmt.setBoolean(4, usuario.isAdmin()); // Se agrega la fecha de creación
            pstmt.setBytes(5, usuario.getFotoPerfil()); // Se agrega la fecha de creación

            pstmt.setString(6, usuario.getNickname());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
