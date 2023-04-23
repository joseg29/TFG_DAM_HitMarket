package com.example.tarea1firebase.entidades;

import android.widget.TextView;

public class Resena {
    private String texto,fecha;
    private Usuario usu;
    private int valoracion;

    public Resena(){

    }

    public Resena(String texto, Usuario usu, int valoracion) {
        this.texto = texto;
        this.usu = usu;
        this.valoracion = valoracion;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Usuario getUsu() {
        return usu;
    }

    public void setUsu(Usuario usu) {
        this.usu = usu;
    }

    public int getValoracion() {
        return valoracion;
    }

    public void setValoracion(int valoracion) {
        this.valoracion = valoracion;
    }
}
