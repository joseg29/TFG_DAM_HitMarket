package com.example.tarea1firebase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Usuario implements Serializable {
    private String id;
    private String nombre;
    private String descripcion;
    private String instagram;
    private String tiktTok;
    private String email;
    private String youtube;
    private String spotify;
    private String soundCloud;
    private String fotoPerfil;
    private List<Usuario> chatsRecientes;
    private List<String> arrayCanciones;
    private List<String> listaFavoritos;

    public List<String> getListaFavoritos() {
        return listaFavoritos;
    }

    public void setListaFavoritos(List<String> listaFavoritos) {
        this.listaFavoritos = listaFavoritos;
    }


    public List<Usuario> getChatsRecientes() {
        return chatsRecientes;
    }

    public void setChatsRecientes(List<Usuario> chatsRecientes) {
        this.chatsRecientes = chatsRecientes;
    }


    public Usuario(String id, String email, String name, String descripcion, List<String> canciones, String instagram, String tikTok, String youtube, String spotify, String soundCloud, List<Usuario> chatsRecientes, String fotoPerfil, List<String> listaFavoritos) {
        this.id = id;
        this.nombre = name;
        this.descripcion = descripcion;
        this.arrayCanciones = canciones;
        this.instagram = instagram;
        this.email = email;
        this.tiktTok = tikTok;
        this.spotify = spotify;
        this.soundCloud = soundCloud;
        this.youtube = youtube;
        this.chatsRecientes = chatsRecientes;
        this.fotoPerfil = fotoPerfil;
        this.listaFavoritos = listaFavoritos;
    }

    public String getSpotify() {
        return spotify;
    }

    public void setSpotify(String spotify) {
        this.spotify = spotify;
    }

    public String getSoundCloud() {
        return soundCloud;
    }

    public void setSoundCloud(String soundCloud) {
        this.soundCloud = soundCloud;
    }


    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
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

    public Usuario() {
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
        return "Usuario{" + "name='" + nombre + '\'' + ", id='" + id + '\'' + ", descripcion='" + descripcion + '\'' + ", arrayCanciones=" + arrayCanciones + '}';
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

}
