package com.example.tarea1firebase.adaptadores;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.tarea1firebase.R;
import com.example.tarea1firebase.entidades.Resena;
import com.example.tarea1firebase.entidades.Usuario;
import com.example.tarea1firebase.gestor.GestorFirestore;

import java.util.List;

public class AdaptadorResenas extends RecyclerView.Adapter<AdaptadorResenas.ViewHolder> {

    private List<Resena> listaResenas;

    public AdaptadorResenas(List<Resena> listaResenas) {
        this.listaResenas = listaResenas;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nombreUsu, fecha, texto;
        private ImageView imgUsu;
        private TextView estrellas;
        private GestorFirestore gestorFirestore;


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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_resena, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.gestorFirestore.obtenerUsuarioPorId(listaResenas.get(position).getUidAutor(), new GestorFirestore.Callback<Usuario>() {
            @Override
            public void onSuccess(Usuario autor) {
                holder.nombreUsu.setText(autor.getNombre());
                holder.texto.setText(listaResenas.get(position).getTexto());
                holder.estrellas.setText(String.valueOf(listaResenas.get(position).getValoracion()));
                holder.fecha.setText(listaResenas.get(position).getFecha());
            }
        }, Usuario.class);

    }

    @Override
    public int getItemCount() {
        return listaResenas.size();
    }
}
