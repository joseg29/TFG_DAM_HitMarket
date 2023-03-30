package com.example.tarea1firebase;

import java.util.Date;

public class Mensaje {
    private String remitente, texto, fechaYHora;

    public Mensaje() {
    }

    public Mensaje(String remitente, String texto, String fechaYHora) {
        this.remitente = remitente;
        this.texto = texto;
        this.fechaYHora = fechaYHora;
    }

    public String getRemitente() {
        return remitente;
    }

    public void setRemitente(String remitente) {
        this.remitente = remitente;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getFechaYHora() {
        return fechaYHora;
    }

    public void setFechaYHora(String fechaYHora) {
        this.fechaYHora = fechaYHora;
    }
}
