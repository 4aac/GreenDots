package com.asiercl.server.models;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Opinion {
    private int id;
    private Usuario usuario;
    private Local local;
    private Timestamp fechaPublicacion;
    private String resenaTexto;
    private int ecosostenible;
    private int inclusionSocial;
    private int accesibilidad;
    private ArrayList<byte[]> fotos;

    public Opinion(int id, Usuario usuario, Local local, Timestamp fechaPublicacion, String resenaTexto, int ecosostenible, int inclusionSocial, int accesibilidad, ArrayList<byte[]> fotos) {
        this.id = id;
        this.usuario = usuario;
        this.local = local;
        this.fechaPublicacion = fechaPublicacion;
        this.resenaTexto = resenaTexto;
        this.ecosostenible = ecosostenible;
        this.inclusionSocial = inclusionSocial;
        this.accesibilidad = accesibilidad;
        this.fotos = fotos;
    }

    // Getters y Setters
}