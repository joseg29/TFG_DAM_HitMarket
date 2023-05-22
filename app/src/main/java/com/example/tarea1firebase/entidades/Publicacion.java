package com.example.tarea1firebase.entidades;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Publicacion implements Serializable {
    private String autorUid, texto, fecha, urlImagenPublicacion;

    private List<String> nLikes;

    public Publicacion() {
    }

    public Publicacion(String autorUid, String texto, String fecha, String urlImagenPublicacion) {
        this.autorUid = autorUid;
        this.texto = texto;
        this.fecha = fecha;
        this.urlImagenPublicacion = urlImagenPublicacion;
        this.nLikes = Collections.emptyList();
    }

    public String getAutorUid() {
        return autorUid;
    }

    public void setAutorUid(String autorUid) {
        this.autorUid = autorUid;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public List<String> getnLikes() {
        return nLikes;
    }

    public void setnLikes(List<String> nLikes) {
        this.nLikes = nLikes;
    }

    public String getUrlImagenPublicacion() {
        return urlImagenPublicacion;
    }

    public void setUrlImagenPublicacion(String urlImagenPublicacion) {
        this.urlImagenPublicacion = urlImagenPublicacion;
    }

}
