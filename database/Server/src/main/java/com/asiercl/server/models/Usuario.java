package com.asiercl.server.models;

import java.time.LocalDateTime;
import java.sql.Timestamp;
import java.util.ArrayList;

public class Usuario {
    private String nickname;
    private String nombreCompleto;
    private String email;
    private String password;
    private Timestamp fechaCreacion;
    private boolean admin;
    private byte[] fotoPerfil;
    private ArrayList<Local> favoritos;

    public Usuario(String nickname, String nombreCompleto, String email, String password, Timestamp fechaCreacion, boolean admin, byte[] fotoPerfil, ArrayList<Local> favoritos) {
        this.nickname = nickname;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.password = password;
        this.fechaCreacion = fechaCreacion;
        this.admin = admin;
        this.fotoPerfil = fotoPerfil;
        this.favoritos = favoritos;
    }

    public String getNickname() { return nickname; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Timestamp getFechaCreacion() { return fechaCreacion; }
    public boolean isAdmin() { return admin; }
    public ArrayList<Local> getFavoritos() { return favoritos; }
    public byte[] getFotoPerfil() { return fotoPerfil; }
    public void setFotoPerfil(byte[] fotoPerfil) { this.fotoPerfil = fotoPerfil; }

    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public void agregarFavorito(Local local) {
        if (!favoritos.contains(local)) {
            favoritos.add(local);
        }
    }

    public void eliminarFavorito(Local local) {
        favoritos.remove(local);
    }
}
