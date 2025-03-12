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

    // Getters
    public int getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public Local getLocal() { return local; }
    public Timestamp getFechaPublicacion() { return fechaPublicacion; }
    public String getResenaTexto() { return resenaTexto; }
    public int getEcosostenible() { return ecosostenible; }
    public int getInclusionSocial() { return inclusionSocial; }
    public int getAccesibilidad() { return accesibilidad; }
    public ArrayList<byte[]> getFotos() { return fotos; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public void setLocal(Local local) { this.local = local; }
    public void setFechaPublicacion(Timestamp fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }
    public void setResenaTexto(String resenaTexto) { this.resenaTexto = resenaTexto; }
    public void setEcosostenible(int ecosostenible) { this.ecosostenible = ecosostenible; }
    public void setInclusionSocial(int inclusionSocial) { this.inclusionSocial = inclusionSocial; }
    public void setAccesibilidad(int accesibilidad) { this.accesibilidad = accesibilidad; }
    public void setFotos(ArrayList<byte[]> fotos) { this.fotos = fotos; }
}
