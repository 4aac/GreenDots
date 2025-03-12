package com.asiercl.server.models;

import java.time.LocalDate;
import java.util.ArrayList;

public class Local {
    private int id;
    private String nombre;
    private String categoria;
    private LocalDate fechaAdmision;
    private String ubicacion;
    private String descripcionTextual;
    private int ecosostenible;
    private int inclusionSocial;
    private int accesibilidad;
    private ArrayList<Opinion> opiniones;
    private ArrayList<byte[]> fotos;

    public Local(int id, String nombre, String categoria,LocalDate fechaAdmision, String ubicacion, String descripcionTextual,
                 int ecosostenible, int inclusionSocial, int accesibilidad) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.fechaAdmision = fechaAdmision;
        this.ubicacion = ubicacion;
        this.descripcionTextual = descripcionTextual;
        this.ecosostenible = ecosostenible;
        this.inclusionSocial = inclusionSocial;
        this.accesibilidad = accesibilidad;
        this.opiniones = new ArrayList<>();
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCategoria() { return categoria; }
    public LocalDate getFechaAdmision() { return fechaAdmision; }
    public String getUbicacion() { return ubicacion; }
    public String getDescripcionTextual() { return descripcionTextual; }
    public int getEcosostenible() { return ecosostenible; }
    public int getInclusionSocial() { return inclusionSocial; }
    public int getAccesibilidad() { return accesibilidad; }
    public ArrayList<Opinion> getOpiniones() { return opiniones; }

    public void agregarOpinion(Opinion opinion) {
        opiniones.add(opinion);
    }
}