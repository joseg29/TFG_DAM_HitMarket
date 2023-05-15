/**
 * Este adaptador se encarga de mostrar una lista de reseñas en un RecyclerView.
 * @author
 */
package com.example.tarea1firebase.adaptadores;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tarea1firebase.R;
import com.example.tarea1firebase.entidades.Resena;
import com.example.tarea1firebase.entidades.Usuario;
import com.example.tarea1firebase.gestor.GestorFirestore;

import java.util.List;

public class AdaptadorResenas extends RecyclerView.Adapter<AdaptadorResenas.ViewHolder> {

    private List<Resena> listaResenas;
    /**
     * Constructor de la clase AdaptadorResenas.
     * @param listaResenas La lista de Resenas que se mostrará en el RecyclerView.
     */
    public AdaptadorResenas(List<Resena> listaResenas) {
        this.listaResenas = listaResenas;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nombreUsu, fecha, texto;
        private ImageView imgUsu;
        private TextView estrellas;
        private GestorFirestore gestorFirestore;

        /**
         * Constructor de la clase ViewHolder.
         * @param v La vista que se utilizará para representar cada elemento de la lista.
         */
        public ViewHolder(View v) {
            super(v);
            texto = v.findViewById(R.id.txtContenidoResena);
            nombreUsu = v.findViewById(R.id.txtNombreUsuResena);
            imgUsu = v.findViewById(R.id.imgUsuResena);
            estrellas = v.findViewById(R.id.txtValoracionResena);
            fecha = v.findViewById(R.id.txtFechaResena);
            gestorFirestore = new GestorFirestore();
        }
    }
    /**
     * Método que se ejecuta cuando el RecyclerView necesita crear una nueva vista.
     * @param parent   El ViewGroup en el que se añadirá la nueva vista.
     * @param viewType El tipo de vista que se creará.
     * @return Una nueva instancia de la clase ViewHolder.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_resena, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }
    /**
     * Método que se ejecuta para mostrar los datos de una Resena en una vista de ViewHolder.
     * @param holder   La vista de ViewHolder en la que se mostrarán los datos de la Resena.
     * @param position La posición de la Resena en la lista.
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.gestorFirestore.obtenerUsuarioPorId(listaResenas.get(position).getUidAutor(), new GestorFirestore.Callback<Usuario>() {
            /**
             * Este método se llama cuando se obtiene el autor de la reseña de Firestore.
             * Actualiza la vista del autor de la reseña con la información del autor.
             *
             * @param autor El autor de la reseña.
             */
            @Override
            public void onSuccess(Usuario autor) {
                holder.nombreUsu.setText(autor.getNombre());
                holder.texto.setText(listaResenas.get(position).getTexto());
                holder.estrellas.setText(String.valueOf(listaResenas.get(position).getValoracion()));
                holder.fecha.setText(listaResenas.get(position).getFecha());
                try {
                    Glide.with(holder.itemView.getContext()).load(autor.getFotoPerfil()).into(holder.imgUsu);
                } catch (Exception e) {
                }
            }
        }, Usuario.class);

    }
    /**
     * Método que devuelve el número de elementos en la lista de Resenas.
     * @return El número de elementos en la lista de Resenas.
     */
    @Override
    public int getItemCount() {
        return listaResenas.size();
    }
}
