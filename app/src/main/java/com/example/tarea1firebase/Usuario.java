package com.example.tarea1firebase;

import java.io.Serializable;
import java.util.List;

public class Usuario implements Serializable {
    private String id;
    private String nombre;
    private String descripcion;
    private String instagram;
    private String tiktTok;
    private String email;
    private String contrasena;
    private String youtube;

    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }


    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getDescripcion() {
        return descripcion;
    }


    private List<String> arrayCanciones;

    public Usuario() {
    }

    public Usuario(String id, String email, String name, String descripcion, List<String> canciones, String contrasena, String instagram, String tikTok, String youtube) {
        this.id = id;
        this.nombre = name;
        this.descripcion = descripcion;
        this.arrayCanciones = canciones;
        this.instagram = instagram;
        this.email = email;
        this.contrasena = contrasena;
        this.tiktTok = tikTok;
        this.youtube = youtube;
    }


    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<String> getArrayCanciones() {
        return arrayCanciones;
    }

    public void setArrayCanciones(List<String> arrayCanciones) {
        this.arrayCanciones = arrayCanciones;
    }

    public String getNombre() {
        return nombre;
    }

    public String getId() {
        return id;
    }


    public String getTiktTok() {
        return tiktTok;
    }

    public void setTiktTok(String tiktTok) {
        this.tiktTok = tiktTok;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "name='" + nombre + '\'' +
                ", id='" + id + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", arrayCanciones=" + arrayCanciones +
                '}';
    }

}
