package com.example.tarea1firebase.entidades;

import java.io.Serializable;

/**
 * La clase Resena representa una reseña o comentario sobre algo.
 * Implementa la interfaz Serializable para permitir la serialización de objetos.
 */
public class Resena implements Serializable {
    private String texto;

    private String fecha;
    private String uidAutor;
    private int valoracion;

    /**
     * Obtiene la fecha de la reseña.
     *
     * @return La fecha de la reseña.
     */
    public String getFecha() {
        return fecha;
    }

    /**
     * Establece la fecha de la reseña.
     *
     * @param fecha La fecha de la reseña a establecer.
     */
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    /**
     * Constructor sin argumentos para la clase Resena.
     */
    public Resena() {

    }

    /**
     * Constructor con argumentos para la clase Resena.
     *
     * @param texto      El texto de la reseña.
     * @param uidAutor   El identificador del autor de la reseña.
     * @param valoracion La valoración de la reseña.
     * @param fecha      La fecha de la reseña.
     */
    public Resena(String texto, String uidAutor, int valoracion, String fecha) {
        this.texto = texto;
        this.uidAutor = uidAutor;
        this.valoracion = valoracion;
        this.fecha = fecha;
    }

    /**
     * Obtiene el texto de la reseña.
     *
     * @return El texto de la reseña.
     */
    public String getTexto() {
        return texto;
    }

    /**
     * Establece el texto de la reseña.
     *
     * @param texto El texto de la reseña a establecer.
     */
    public void setTexto(String texto) {
        this.texto = texto;
    }

    /**
     * Obtiene el identificador del autor de la reseña.
     *
     * @return El identificador del autor de la reseña.
     */
    public String getUidAutor() {
        return uidAutor;
    }

    /**
     * Establece el identificador del autor de la reseña.
     *
     * @param uidAutor El identificador del autor de la reseña a establecer.
     */
    public void setUidAutor(String uidAutor) {
        this.uidAutor = uidAutor;
    }

    /**
     * Obtiene la valoración de la reseña.
     *
     * @return La valoración de la reseña.
     */
    public int getValoracion() {
        return valoracion;
    }

    /**
     * Establece la valoración de la reseña.
     *
     * @param valoracion La valoración de la reseña a establecer.
     */
    public void setValoracion(int valoracion) {
        this.valoracion = valoracion;
    }
}
