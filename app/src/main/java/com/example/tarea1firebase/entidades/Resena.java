package com.example.tarea1firebase.entidades;

import java.io.Serializable;

public class Resena implements Serializable {
    private String texto;

    private String fecha;
    private String uidAutor;
    private int valoracion;

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Resena() {

    }

    public Resena(String texto, String uidAutor, int valoracion, String fecha) {
        this.texto = texto;
        this.uidAutor = uidAutor;
        this.valoracion = valoracion;
        this.fecha = fecha;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getUidAutor() {
        return uidAutor;
    }

    public void setUidAutor(String uidAutor) {
        this.uidAutor = uidAutor;
    }

    public int getValoracion() {
        return valoracion;
    }

    public void setValoracion(int valoracion) {
        this.valoracion = valoracion;
    }
}
