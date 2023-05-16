package com.example.tarea1firebase.entidades;

import java.io.Serializable;
import java.util.List;

/**
 * La clase Usuario representa un usuario en el sistema.
 * Implementa la interfaz Serializable para permitir la serialización de objetos.
 */
public class Usuario implements Serializable {
    private String id;
    private String nombre;
    private String descripcion;
    private String ciudad;
    private String genero;
    private String instagram;
    private String tiktTok;
    private String email;
    private String youtube;
    private String spotify;
    private String soundCloud;
    private String fotoPerfil;
    private List<String> chatsRecientes;
    private List<String> arrayCanciones;
    private List<String> listaFavoritos;
    private List<Resena> listaResenas;
    private List<String> visitasAlPerfil;

    /**
     * Constructor sin argumentos para la clase Usuario.
     */
    public Usuario() {
    }

    /**
     * Constructor con argumentos para la clase Usuario.
     *
     * @param id             El identificador del usuario.
     * @param email          El correo electrónico del usuario.
     * @param name           El nombre del usuario.
     * @param descripcion    La descripción del usuario.
     * @param ciudad         La ciudad del usuario.
     * @param genero         El género del usuario.
     * @param canciones      La lista de canciones del usuario.
     * @param instagram      El nombre de usuario de Instagram del usuario.
     * @param tikTok         El nombre de usuario de TikTok del usuario.
     * @param youtube        El enlace al canal de YouTube del usuario.
     * @param spotify        El enlace a la cuenta de Spotify del usuario.
     * @param soundCloud     El enlace a la cuenta de SoundCloud del usuario.
     * @param chatsRecientes La lista de identificadores de los chats recientes del usuario.
     * @param fotoPerfil     El enlace a la foto de perfil del usuario.
     * @param listaFavoritos La lista de identificadores de las canciones favoritas del usuario.
     * @param listaResenas   La lista de reseñas del usuario.
     * @param listaVisitas   La lista de identificadores de los perfiles visitados por el usuario.
     */
    public Usuario(String id, String email, String name, String descripcion, String ciudad, String genero, List<String> canciones, String instagram, String tikTok, String youtube, String spotify, String soundCloud, List<String> chatsRecientes, String fotoPerfil, List<String> listaFavoritos, List<Resena> listaResenas, List<String> listaVisitas) {
        this.id = id;
        this.nombre = name;
        this.descripcion = descripcion;
        this.ciudad = ciudad;
        this.genero = genero;
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
        this.listaResenas = listaResenas;
        this.visitasAlPerfil = listaVisitas;
    }

    /**
     * Obtiene la lista de canciones favoritas del usuario.
     *
     * @return La lista de canciones favoritas del usuario.
     */
    public List<String> getListaFavoritos() {
        return listaFavoritos;
    }

    /**
     * Establece la lista de canciones favoritas del usuario.
     *
     * @param listaFavoritos La lista de canciones favoritas del usuario.
     */
    public void setListaFavoritos(List<String> listaFavoritos) {
        this.listaFavoritos = listaFavoritos;
    }

    /**
     * Obtiene la lista de chats recientes del usuario.
     *
     * @return La lista de identificadores de los chats recientes del usuario.
     */
    public List<String> getChatsRecientes() {
        return chatsRecientes;
    }

    /**
     * Establece la lista de chats recientes del usuario.
     *
     * @param chatsRecientes La lista de identificadores de los chats recientes del usuario.
     */
    public void setChatsRecientes(List<String> chatsRecientes) {
        this.chatsRecientes = chatsRecientes;
    }

    /**
     * Obtiene la lista de perfiles visitados por el usuario.
     *
     * @return La lista de identificadores de los perfiles visitados por el usuario.
     */
    public List<String> getVisitasAlPerfil() {
        return visitasAlPerfil;
    }

    /**
     * Establece la lista de perfiles visitados por el usuario.
     *
     * @param visitasAlPerfil La lista de identificadores de los perfiles visitados por el usuario.
     */
    public void setVisitasAlPerfil(List<String> visitasAlPerfil) {
        this.visitasAlPerfil = visitasAlPerfil;
    }

    /**
     * Obtiene el enlace a la cuenta de Spotify del usuario.
     *
     * @return El enlace a la cuenta de Spotify del usuario.
     */
    public String getSpotify() {
        return spotify;
    }

    /**
     * Establece el enlace a la cuenta de Spotify del usuario.
     *
     * @param spotify El enlace a la cuenta de Spotify del usuario.
     */
    public void setSpotify(String spotify) {
        this.spotify = spotify;
    }

    /**
     * Obtiene el enlace a la cuenta de SoundCloud del usuario.
     *
     * @return El enlace a la cuenta de SoundCloud del usuario.
     */
    public String getSoundCloud() {
        return soundCloud;
    }

    /**
     * Establece el enlace a la cuenta de SoundCloud del usuario.
     *
     * @param soundCloud El enlace a la cuenta de SoundCloud del usuario.
     */
    public void setSoundCloud(String soundCloud) {
        this.soundCloud = soundCloud;
    }

    /**
     * Obtiene el enlace al canal de YouTube del usuario.
     *
     * @return El enlace al canal de YouTube del usuario.
     */
    public String getYoutube() {
        return youtube;
    }

    /**
     * Establece el enlace al canal de YouTube del usuario.
     *
     * @param youtube El enlace al canal de YouTube del usuario.
     */
    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    /**
     * Obtiene el correo electrónico del usuario.
     *
     * @return El correo electrónico del usuario.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece el correo electrónico del usuario.
     *
     * @param email El correo electrónico del usuario.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obtiene el nombre de usuario de Instagram del usuario.
     *
     * @return El nombre de usuario de Instagram del usuario.
     */
    public String getInstagram() {
        return instagram;
    }

    /**
     * Establece el nombre de usuario de Instagram del usuario.
     *
     * @param instagram El nombre de usuario de Instagram del usuario.
     */
    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    /**
     * Obtiene la descripción del usuario.
     *
     * @return La descripción del usuario.
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Obtiene la ciudad del usuario.
     *
     * @return La ciudad del usuario.
     */
    public String getCiudad() {
        return ciudad;
    }

    /**
     * Establece la ciudad del usuario.
     *
     * @param ciudad La ciudad del usuario.
     */
    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    /**
     * Obtiene el género del usuario.
     *
     * @return El género del usuario.
     */
    public String getGenero() {
        return genero;
    }

    /**
     * Establece el género del usuario.
     *
     * @param genero El género del usuario.
     */
    public void setGenero(String genero) {
        this.genero = genero;
    }

    /**
     * Establece el nombre del usuario.
     *
     * @param nombre El nombre del usuario.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Establece la descripción del usuario.
     *
     * @param descripcion La descripción del usuario.
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtiene la lista de canciones del usuario.
     *
     * @return La lista de canciones del usuario.
     */
    public List<String> getArrayCanciones() {
        return arrayCanciones;
    }

    /**
     * Establece la lista de canciones del usuario.
     *
     * @param arrayCanciones La lista de canciones del usuario.
     */
    public void setArrayCanciones(List<String> arrayCanciones) {
        this.arrayCanciones = arrayCanciones;
    }

    /**
     * Obtiene el nombre del usuario.
     *
     * @return El nombre del usuario.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Obtiene el identificador del usuario.
     *
     * @return El identificador del usuario.
     */
    public String getId() {
        return id;
    }

    /**
     * Obtiene el nombre de usuario de TikTok del usuario.
     *
     * @return El nombre de usuario de TikTok del usuario.
     */
    public String getTiktTok() {
        return tiktTok;
    }

    /**
     * Establece el nombre de usuario de TikTok del usuario.
     *
     * @param tiktTok El nombre de usuario de TikTok del usuario.
     */
    public void setTiktTok(String tiktTok) {
        this.tiktTok = tiktTok;
    }

    /**
     * Devuelve una representación en forma de cadena de la instancia actual de Usuario.
     *
     * @return Una representación en forma de cadena de la instancia de Usuario.
     */
    @Override
    public String toString() {
        return "Usuario{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", ciudad='" + ciudad + '\'' +
                ", genero='" + genero + '\'' +
                ", arrayCanciones=" + arrayCanciones +
                '}';
    }

    /**
     * Obtiene la URL de la foto de perfil del usuario.
     *
     * @return La URL de la foto de perfil del usuario.
     */
    public String getFotoPerfil() {
        return fotoPerfil;
    }

    /**
     * Establece la URL de la foto de perfil del usuario.
     *
     * @param fotoPerfil La URL de la foto de perfil del usuario.
     */
    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    /**
     * Obtiene la lista de reseñas del usuario.
     *
     * @return La lista de reseñas del usuario.
     */
    public List<Resena> getListaResenas() {
        return listaResenas;
    }

    /**
     * Establece la lista de reseñas del usuario.
     *
     * @param listaResenas La lista de reseñas del usuario.
     */
    public void setListaResenas(List<Resena> listaResenas) {
        this.listaResenas = listaResenas;
    }
}
