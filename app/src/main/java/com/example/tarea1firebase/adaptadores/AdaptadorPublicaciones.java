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
import com.example.tarea1firebase.entidades.Publicacion;
import com.example.tarea1firebase.entidades.Usuario;
import com.example.tarea1firebase.gestor.GestorFirestore;

import java.util.List;

public class AdaptadorPublicaciones extends RecyclerView.Adapter<AdaptadorPublicaciones.ViewHolder> {
    private List<Publicacion> listaPublicaciones;
    private GestorFirestore gestorFirestore;

    public AdaptadorPublicaciones(List<Publicacion> listaPublicaciones) {
        this.listaPublicaciones = listaPublicaciones;
        this.gestorFirestore = new GestorFirestore();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView autor, texto, fecha, likes;
        private ImageView imgPerfil, imgPublicacion;

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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_publicacion, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        gestorFirestore.obtenerUsuarioPorId(listaPublicaciones.get(position).getAutorUid(), new GestorFirestore.Callback<Usuario>() {
            @Override
            public void onSuccess(Usuario result) {
                holder.autor.setText(result.getNombre());
                Glide.with(holder.itemView.getContext()).load(result.getFotoPerfil()).fitCenter().into(holder.imgPerfil);
            }
        }, Usuario.class);
        holder.texto.setText(listaPublicaciones.get(position).getTexto());
        holder.fecha.setText(listaPublicaciones.get(position).getFecha());
        holder.likes.setText(String.valueOf(listaPublicaciones.get(position).getnLikes().size()));
        if (listaPublicaciones.get(position).getUrlImagenPublicacion() != "") {
            Glide.with(holder.itemView.getContext()).load(listaPublicaciones.get(position).getUrlImagenPublicacion()).fitCenter().into(holder.imgPublicacion);
        } else {
            holder.imgPublicacion.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return listaPublicaciones.size();
    }
}
