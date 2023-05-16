package com.example.tarea1firebase.entidades;

import java.util.ArrayList;

/**
 * La clase Chat representa una conversación entre dos usuarios.
 */
public class Chat {
    private ArrayList<Mensaje> listaMensajes;
    private String usuario1, usuario2;
    private String fechaUltimoMsj;
    private String chatId;

    /**
     * Obtiene el identificador del chat.
     *
     * @return El identificador del chat.
     */
    public String getChatId() {
        return chatId;
    }

    /**
     * Establece el identificador del chat.
     *
     * @param chatId El identificador del chat a establecer.
     */
    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    /**
     * Constructor sin argumentos para la clase Chat.
     */
    public Chat() {

    }

    /**
     * Constructor con argumentos para la clase Chat.
     *
     * @param listaMensajes  La lista de mensajes en el chat.
     * @param usuario1       El nombre del primer usuario en el chat.
     * @param usuario2       El nombre del segundo usuario en el chat.
     * @param fechaUltimoMsj La fecha del último mensaje en el chat.
     * @param chatKey        El identificador único del chat.
     */
    public Chat(ArrayList<Mensaje> listaMensajes, String usuario1, String usuario2, String fechaUltimoMsj, String chatKey) {
        this.listaMensajes = listaMensajes;
        this.usuario1 = usuario1;
        this.usuario2 = usuario2;
        this.fechaUltimoMsj = fechaUltimoMsj;
        this.chatId = chatKey;
    }

    /**
     * Obtiene el identificador del chat.
     *
     * @return El identificador del chat.
     */
    public ArrayList<Mensaje> getListaMensajes() {
        return listaMensajes;
    }

    /**
     * Establece la lista de mensajes en el chat.
     *
     * @param listaMensajes La lista de mensajes a establecer en el chat.
     */
    public void setListaMensajes(ArrayList<Mensaje> listaMensajes) {
        this.listaMensajes = listaMensajes;
    }

    /**
     * Obtiene el nombre del primer usuario en el chat.
     *
     * @return El nombre del primer usuario en el chat.
     */
    public String getUsuario1() {
        return usuario1;
    }

    /**
     * Establece el nombre del primer usuario en el chat.
     *
     * @param usuario1 El nombre del primer usuario a establecer en el chat.
     */
    public void setUsuario1(String usuario1) {
        this.usuario1 = usuario1;
    }

    /**
     * Obtiene el nombre del segundo usuario en el chat.
     *
     * @return El nombre del segundo usuario en el chat.
     */
    public String getUsuario2() {
        return usuario2;
    }

    /**
     * Establece el nombre del segundo usuario en el chat.
     *
     * @param usuario2 El nombre del segundo usuario a establecer en el chat.
     */
    public void setUsuario2(String usuario2) {
        this.usuario2 = usuario2;
    }

    /**
     * Obtiene la fecha del último mensaje en el chat.
     *
     * @return La fecha del último mensaje en el chat.
     */
    public String getFechaUltimoMsj() {
        return fechaUltimoMsj;
    }

    /**
     * Establece la fecha del último mensaje en el chat.
     *
     * @param fechaUltimoMsj La fecha del último mensaje a establecer en el chat.
     */
    public void setFechaUltimoMsj(String fechaUltimoMsj) {
        this.fechaUltimoMsj = fechaUltimoMsj;
    }

    /**
     * Añade un mensaje a la lista de mensajes en el chat.
     *
     * @param msj El mensaje a añadir al chat.
     */
    public void añadirMensaje(Mensaje msj) {
        this.listaMensajes.add(msj);
    }
}
