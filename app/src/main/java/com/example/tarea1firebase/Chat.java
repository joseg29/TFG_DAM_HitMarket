package com.example.tarea1firebase;

import java.util.ArrayList;

public class Chat {
    private ArrayList<Mensaje> listaMensajes;
    private Usuario usuario1, usuario2;
    private String fechaUltimoMsj;

    public Chat() {

    }

    public Chat(ArrayList<Mensaje> listaMensajes, Usuario usuario1, Usuario usuario2, String fechaUltimoMsj) {
        this.listaMensajes = listaMensajes;
        this.usuario1 = usuario1;
        this.usuario2 = usuario2;
        this.fechaUltimoMsj = fechaUltimoMsj;
    }

    public ArrayList<Mensaje> getListaMensajes() {
        return listaMensajes;
    }

    public void setListaMensajes(ArrayList<Mensaje> listaMensajes) {
        this.listaMensajes = listaMensajes;
    }

    public Usuario getUsuario1() {
        return usuario1;
    }

    public void setUsuario1(Usuario usuario1) {
        this.usuario1 = usuario1;
    }

    public Usuario getUsuario2() {
        return usuario2;
    }

    public void setUsuario2(Usuario usuario2) {
        this.usuario2 = usuario2;
    }

    public String getFechaUltimoMsj() {
        return fechaUltimoMsj;
    }

    public void setFechaUltimoMsj(String fechaUltimoMsj) {
        this.fechaUltimoMsj = fechaUltimoMsj;
    }

    public void añadirMensaje(Mensaje msj) {
        this.listaMensajes.add(msj);
    }
}
