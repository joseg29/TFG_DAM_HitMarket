package com.example.tarea1firebase.entidades;

import java.io.Serializable;

public class Resena implements Serializable {
    private String texto;

    private String fecha;
    private Usuario autor;
    private int valoracion;

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Resena() {

    }

    public Resena(String texto, Usuario usu, int valoracion, String fecha) {
        this.texto = texto;
        this.autor = usu;
        this.valoracion = valoracion;
        this.fecha = fecha;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Usuario getAutor() {
        return autor;
    }

    public void setAutor(Usuario autor) {
        this.autor = autor;
    }

    public int getValoracion() {
        return valoracion;
    }

    public void setValoracion(int valoracion) {
        this.valoracion = valoracion;
    }
}
