package com.example.tarea1firebase.adaptadores;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tarea1firebase.PerfilUsuario;
import com.example.tarea1firebase.R;
import com.example.tarea1firebase.entidades.Chat;
import com.example.tarea1firebase.entidades.Publicacion;
import com.example.tarea1firebase.entidades.Usuario;
import com.example.tarea1firebase.gestor.GestorFirestore;

import java.util.List;

/**
 * El adaptador utilizado para mostrar una lista de publicaciones en un RecyclerView.
 */
public class AdaptadorPublicaciones extends RecyclerView.Adapter<AdaptadorPublicaciones.ViewHolder> {
    private List<Publicacion> listaPublicaciones;
    private GestorFirestore gestorFirestore;

    /**
     * Constructor de la clase AdaptadorPublicaciones.
     *
     * @param listaPublicaciones La lista de publicaciones a mostrar.
     */
    public AdaptadorPublicaciones(List<Publicacion> listaPublicaciones) {
        this.listaPublicaciones = listaPublicaciones;
        this.gestorFirestore = new GestorFirestore();
    }

    /**
     * Clase ViewHolder que representa una vista de item en el RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView autor, texto, fecha, likes;
        private ImageView imgPerfil, imgPublicacion;

        /**
         * Constructor de la clase ViewHolder.
         * Se encarga de asignar las referencias de los elementos de la vista a las variables correspondientes.
         *
         * @param v La vista que representa un item en el RecyclerView.
         */
        public ViewHolder(View v) {
            super(v);
            autor = v.findViewById(R.id.autor_publicacion);
            texto = v.findViewById(R.id.txt_publicacion);
            imgPerfil = v.findViewById(R.id.foto_perfil_publicacion);
            imgPublicacion = v.findViewById(R.id.img_publicacion);
            fecha = v.findViewById(R.id.fecha_publicacion);
            likes = v.findViewById(R.id.txtLikes);
        }
    }

    /**
     * Método invocado cuando se necesita crear una nueva instancia de ViewHolder.
     *
     * @param parent   El ViewGroup al que se va a añadir la vista.
     * @param viewType El tipo de vista del nuevo ViewHolder.
     * @return Un nuevo objeto ViewHolder que contiene la vista para cada elemento de la lista.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_publicacion, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    /**
     * Método invocado para mostrar los datos en la posición especificada.
     *
     * @param holder   El ViewHolder que debe ser actualizado para representar el contenido del elemento en la posición dada.
     * @param position La posición de los datos en el conjunto de datos.
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        /*
         * Invoca el método obtenerUsuarioPorId del objeto gestorFirestore para obtener información
         * del usuario correspondiente a la publicación en la posición dada.
         * */
        gestorFirestore.obtenerUsuarioPorId(listaPublicaciones.get(position).getAutorUid(), new GestorFirestore.Callback<Usuario>() {
            /**
             * Método invocado cuando se obtiene exitosamente la información de un usuario.
             * Actualiza el TextView autor del ViewHolder con el nombre del usuario obtenido
             * y carga la foto de perfil del usuario en el ImageView imgPerfil utilizando la biblioteca Glide.
             *
             * @param result El objeto Usuario que contiene la información del usuario obtenido.
             */
            @Override
            public void onSuccess(Usuario result) {
                holder.autor.setText(result.getNombre());
                Glide.with(holder.itemView.getContext()).load(result.getFotoPerfil()).fitCenter().into(holder.imgPerfil);
            }
        }, Usuario.class);
        /*
         * Establece el texto de la publicación en el TextView texto del ViewHolder utilizando el
         * método getTexto() de la publicación en la posición dada.
         * */
        holder.texto.setText(listaPublicaciones.get(position).getTexto());
        /*
         * Establece la fecha de la publicación en el TextView fecha del ViewHolder utilizando el
         * método getFecha() de la publicación en la posición dada.
         * */
        holder.fecha.setText(listaPublicaciones.get(position).getFecha());
        /*
         * Establece el número de likes de la publicación en el TextView likes del ViewHolder
         * utilizando el método size() de la lista de likes de la publicación en la posición dada.
         * */
        holder.likes.setText(String.valueOf(listaPublicaciones.get(position).getnLikes().size()));
        /*
         * Verifica si la publicación en la posición dada tiene una URL de imagen de publicación no
         * vacía.
         */
        if (listaPublicaciones.get(position).getUrlImagenPublicacion() != "") {
            /*
             * Utiliza la biblioteca Glide para cargar y mostrar la imagen de la publicación en el
             * ImageView imgPublicacion del ViewHolder.
             */
            Glide.with(holder.itemView.getContext()).load(listaPublicaciones.get(position).getUrlImagenPublicacion()).fitCenter().into(holder.imgPublicacion);
        } else {
            /*
             * Si la publicación no tiene una URL de imagen de publicación, establece la visibilidad
             * del ImageView imgPublicacion en GONE (no visible).
             */
            holder.imgPublicacion.setVisibility(View.GONE);
        }
        holder.imgPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), PerfilUsuario.class);
            intent.putExtra("UidUsuario", listaPublicaciones.get(position).getAutorUid());
            v.getContext().startActivity(intent);
        });
    }

    /**
     * Devuelve el número total de elementos en el conjunto de datos.
     *
     * @return El número total de elementos en el conjunto de datos.
     */
    @Override
    public int getItemCount() {
        return listaPublicaciones.size();
    }

    /**
     * Método para refrescar datos desde otra clase
     *
     * @param data la nueva lista
     */
    public void setData(List<Publicacion> data) {
        listaPublicaciones.clear();
        listaPublicaciones.addAll(data);
        notifyDataSetChanged();
    }
}
