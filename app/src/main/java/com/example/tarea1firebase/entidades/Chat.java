package com.example.tarea1firebase.entidades;

import java.util.ArrayList;

public class Chat {
    private ArrayList<Mensaje> listaMensajes;
    private String usuario1, usuario2;
    private String fechaUltimoMsj;
    private String chatId;

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }


    public Chat() {

    }

    public Chat(ArrayList<Mensaje> listaMensajes, String usuario1, String usuario2, String fechaUltimoMsj, String chatKey) {
        this.listaMensajes = listaMensajes;
        this.usuario1 = usuario1;
        this.usuario2 = usuario2;
        this.fechaUltimoMsj = fechaUltimoMsj;
        this.chatId = chatKey;
    }

    public ArrayList<Mensaje> getListaMensajes() {
        return listaMensajes;
    }

    public void setListaMensajes(ArrayList<Mensaje> listaMensajes) {
        this.listaMensajes = listaMensajes;
    }

    public String getUsuario1() {
        return usuario1;
    }

    public void setUsuario1(String usuario1) {
        this.usuario1 = usuario1;
    }

    public String getUsuario2() {
        return usuario2;
    }

    public void setUsuario2(String usuario2) {
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
