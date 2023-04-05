package com.example.tarea1firebase;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdaptadorUsuariosRecycler extends RecyclerView.Adapter<AdaptadorUsuariosRecycler.ViewHolder> {
    private List<Usuario> listaUsuarios;
    private List<Usuario> listaUsuariosFiltrados;
    private boolean isFavorite;

    public AdaptadorUsuariosRecycler(ArrayList<Usuario> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
        this.listaUsuariosFiltrados = new ArrayList<>(listaUsuarios);
        isFavorite = false;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filtrar(String query) {
        listaUsuarios.clear();
        for (Usuario usuario : listaUsuariosFiltrados) {
            if (usuario.getNombre().toLowerCase().contains(query.toLowerCase()) ||
                    usuario.getNombre().toLowerCase().startsWith(query.toLowerCase())) {
                listaUsuarios.add(usuario);
            }
        }
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nombreUsu;
        private Button btnFav, btnVerPerf;


        public ViewHolder(View v) {
            super(v);
            nombreUsu = v.findViewById(R.id.txtNombreUsu);
            btnFav = v.findViewById(R.id.btnCoraVacio);
            btnVerPerf = v.findViewById(R.id.btnVerPerfil);

        }
    }

    //será quien devuelva el ViewHolder con el layout seteado que previamente definimos
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_usuarios_explora, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }


    //será quien se encargue de establecer los objetos en el ViewHolder
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.nombreUsu.setText(listaUsuarios.get(position).getNombre().toUpperCase(Locale.ROOT));

        holder.btnVerPerf.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), PerfilUsuario.class);
            intent.putExtra("UidUsuario", listaUsuarios.get(position).getId());
            v.getContext().startActivity(intent);
        });

        holder.btnFav.setOnClickListener(v -> {
            TransitionDrawable transitionDrawableIda = new TransitionDrawable(new Drawable[]{
                    ContextCompat.getDrawable(v.getContext(), R.drawable.corazon_favoritos_vacio),
                    ContextCompat.getDrawable(v.getContext(), R.drawable.corazon_favoritos_relleno)
            });
            TransitionDrawable transitionDrawableVuelta = new TransitionDrawable(new Drawable[]{
                    ContextCompat.getDrawable(v.getContext(), R.drawable.corazon_favoritos_relleno),
                    ContextCompat.getDrawable(v.getContext(), R.drawable.corazon_favoritos_vacio)

            });

            if (isFavorite) {
                transitionDrawableVuelta.setCrossFadeEnabled(true);
                transitionDrawableVuelta.startTransition(300);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.btnFav.setForeground(transitionDrawableVuelta);
                }
                isFavorite = false;
            } else {
                transitionDrawableIda.setCrossFadeEnabled(true);
                transitionDrawableIda.startTransition(300);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.btnFav.setForeground(transitionDrawableIda);
                }
                isFavorite = true;
            }

        });
    }


    //será quien devuelva la cantidad de items que se encuentra en la lista
    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }


}
