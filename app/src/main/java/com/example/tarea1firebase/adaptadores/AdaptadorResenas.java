package com.example.tarea1firebase.adaptadores;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tarea1firebase.R;
import com.example.tarea1firebase.entidades.Mensaje;
import com.example.tarea1firebase.entidades.Resena;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorResenas extends RecyclerView.Adapter<AdaptadorResenas.ViewHolder> {

        private List<Resena> listaResenas;


    public AdaptadorResenas(ArrayList<Resena> listaResenas) {
        this.listaResenas = listaResenas;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nombreUsu,fecha,texto;
        private ImageView imgUsu;
        private TextView estrellas;


        public ViewHolder(View v) {
            super(v);
            texto = v.findViewById(R.id.txtContenidoResena);
            nombreUsu = v.findViewById(R.id.txtNombreUsu);
            imgUsu = v.findViewById(R.id.imgUsuResena);
            estrellas = v.findViewById(R.id.txtValoracionResena);


        }
    }

    @NonNull
    @Override
    public AdaptadorResenas.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_resena, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorResenas.ViewHolder holder, int position) {
        holder.nombreUsu.setText(listaResenas.get(position).getUsu().getNombre());
        holder.texto.setText(listaResenas.get(position).getTexto());



    }

    @Override
    public int getItemCount() {
        return listaResenas.size();
    }
}
