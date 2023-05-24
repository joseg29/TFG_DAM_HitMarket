package com.example.tarea1firebase.entidades;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
/**
 * Clase que representa una publicación.
 */
public class Publicacion implements Serializable {
    private String autorUid, texto, fecha, urlImagenPublicacion;

    private List<String> nLikes;
    /**
     * Constructor vacío de la clase Publicacion.
     */
    public Publicacion() {
    }
    /**
     * Constructor de la clase Publicacion.
     *
     * @param autorUid             El UID del autor de la publicación.
     * @param texto                El texto de la publicación.
     * @param fecha                La fecha de la publicación.
     * @param urlImagenPublicacion La URL de la imagen de la publicación.
     */
    public Publicacion(String autorUid, String texto, String fecha, String urlImagenPublicacion) {
        this.autorUid = autorUid;
        this.texto = texto;
        this.fecha = fecha;
        this.urlImagenPublicacion = urlImagenPublicacion;
        this.nLikes = Collections.emptyList();
    }
    /**
     * Obtiene el UID del autor de la publicación.
     *
     * @return El UID del autor de la publicación.
     */
    public String getAutorUid() {
        return autorUid;
    }
    /**
     * Establece el UID del autor de la publicación.
     *
     * @param autorUid El UID del autor de la publicación.
     */
    public void setAutorUid(String autorUid) {
        this.autorUid = autorUid;
    }
    /**
     * Obtiene el texto de la publicación.
     *
     * @return El texto de la publicación.
     */
    public String getTexto() {
        return texto;
    }
    /**
     * Establece el texto de la publicación.
     *
     * @param texto El texto de la publicación.
     */
    public void setTexto(String texto) {
        this.texto = texto;
    }
    /**
     * Obtiene la fecha de la publicación.
     *
     * @return La fecha de la publicación.
     */
    public String getFecha() {
        return fecha;
    }
    /**
     * Establece la fecha de la publicación.
     *
     * @param fecha La fecha de la publicación.
     */
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
    /**
     * Obtiene la lista de likes de la publicación.
     *
     * @return La lista de likes de la publicación.
     */
    public List<String> getnLikes() {
        return nLikes;
    }
    /**
     * Establece la lista de likes de la publicación.
     *
     * @param nLikes La lista de likes de la publicación.
     */
    public void setnLikes(List<String> nLikes) {
        this.nLikes = nLikes;
    }
    /**
     * Obtiene la URL de la imagen de la publicación.
     *
     * @return La URL de la imagen de la publicación.
     */
    public String getUrlImagenPublicacion() {
        return urlImagenPublicacion;
    }
    /**
     * Establece la URL de la imagen de la publicación.
     *
     * @param urlImagenPublicacion La URL de la imagen de la publicación.
     */
    public void setUrlImagenPublicacion(String urlImagenPublicacion) {
        this.urlImagenPublicacion = urlImagenPublicacion;
    }

}
