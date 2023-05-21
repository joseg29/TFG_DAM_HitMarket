package com.example.tarea1firebase.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tarea1firebase.R;

import java.util.List;

public class AdaptadorGenerosRecycler extends RecyclerView.Adapter<AdaptadorGenerosRecycler.ViewHolder> {

    private List<String> listaGeneros;

    public AdaptadorGenerosRecycler(List<String> listaGeneros) {
        this.listaGeneros = listaGeneros;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtGenero;
        private ImageView imgGenero;

        public ViewHolder(View v) {
            super(v);
            txtGenero = v.findViewById(R.id.txtGenero);
            imgGenero = v.findViewById(R.id.imgGenero);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_generos, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String genero = listaGeneros.get(position);
        holder.txtGenero.setText(genero);

        // Asignar imagen según el género utilizando Glide u otra biblioteca de carga de imágenes
        // Reemplaza "R.drawable.genero_default" con la imagen predeterminada que deseas mostrar si no se encuentra una imagen específica para el género
        int imagenGenero = obtenerImagenGenero(genero);
        Glide.with(holder.itemView.getContext())
                .load(imagenGenero)
                .placeholder(R.drawable.sin_genero_musical)
                .into(holder.imgGenero);
    }

    @Override
    public int getItemCount() {
        return listaGeneros.size();
    }

    public void addItem(String newItem) {
        listaGeneros.add(newItem);
    }

    private int obtenerImagenGenero(String genero) {

        if (genero.equalsIgnoreCase("#Rap")) {
            return R.drawable.genero_rap;
        } else if (genero.equalsIgnoreCase("#Trap")) {
            return R.drawable.genero_trap;
        } else if (genero.equalsIgnoreCase("#Clasica")) {
            return R.drawable.genero_clasica;
        } else if (genero.equalsIgnoreCase("#Country")) {
            return R.drawable.genero_country;
        } else if (genero.equalsIgnoreCase("#Electro")) {
            return R.drawable.genero_electro;
        } else if (genero.equalsIgnoreCase("#Flamenco")) {
            return R.drawable.genero_flamenco;
        } else if (genero.equalsIgnoreCase("#Folk")) {
            return R.drawable.genero_folk;
        } else if (genero.equalsIgnoreCase("#Jazz")) {
            return R.drawable.genero_jazz;
        } else if (genero.equalsIgnoreCase("#Kpop")) {
            return R.drawable.genero_kpop;
        } else if (genero.equalsIgnoreCase("#Metal")) {
            return R.drawable.genero_metal;
        } else if (genero.equalsIgnoreCase("#Pop")) {
            return R.drawable.genero_pop;
        } else if (genero.equalsIgnoreCase("#Rock")) {
            return R.drawable.genero_rock;
        } else if (genero.equalsIgnoreCase("#Drill")) {
            return R.drawable.genero_drill;
        } else {

            return R.drawable.sin_genero_musical;
        }

    }
}
