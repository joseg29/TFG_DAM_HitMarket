package com.example.tarea1firebase.entidades;

/**
 * La clase Mensaje representa un mensaje enviado en un chat.
 */
public class Mensaje {
    private String remitente, texto, fechaYHora;

    /**
     * Constructor sin argumentos para la clase Mensaje.
     */
    public Mensaje() {
    }

    /**
     * Constructor con argumentos para la clase Mensaje.
     *
     * @param remitente  El remitente del mensaje.
     * @param texto      El contenido del mensaje.
     * @param fechaYHora La fecha y hora en que se envió el mensaje.
     */
    public Mensaje(String remitente, String texto, String fechaYHora) {
        this.remitente = remitente;
        this.texto = texto;
        this.fechaYHora = fechaYHora;
    }

    /**
     * Obtiene el remitente del mensaje.
     *
     * @return El remitente del mensaje.
     */
    public String getRemitente() {
        return remitente;
    }

    /**
     * Establece el remitente del mensaje.
     *
     * @param remitente El remitente del mensaje a establecer.
     */
    public void setRemitente(String remitente) {
        this.remitente = remitente;
    }

    /**
     * Obtiene el contenido del mensaje.
     *
     * @return El contenido del mensaje.
     */
    public String getTexto() {
        return texto;
    }

    /**
     * Establece el contenido del mensaje.
     *
     * @param texto El contenido del mensaje a establecer.
     */
    public void setTexto(String texto) {
        this.texto = texto;
    }

    /**
     * Obtiene la fecha y hora en que se envió el mensaje.
     *
     * @return La fecha y hora del mensaje.
     */
    public String getFechaYHora() {
        return fechaYHora;
    }

    /**
     * Establece la fecha y hora en que se envió el mensaje.
     *
     * @param fechaYHora La fecha y hora del mensaje a establecer.
     */
    public void setFechaYHora(String fechaYHora) {
        this.fechaYHora = fechaYHora;
    }
}
