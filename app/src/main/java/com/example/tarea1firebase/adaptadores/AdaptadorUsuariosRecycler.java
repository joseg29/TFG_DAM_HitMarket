package com.example.tarea1firebase.adaptadores;

import static com.example.tarea1firebase.Registro.COLECCION;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tarea1firebase.PerfilUsuario;
import com.example.tarea1firebase.R;
import com.example.tarea1firebase.entidades.Usuario;
import com.example.tarea1firebase.gestor.GestorFirestore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdaptadorUsuariosRecycler extends RecyclerView.Adapter<AdaptadorUsuariosRecycler.ViewHolder> {
    private List<Usuario> listaUsuarios;
    private List<Usuario> listaUsuariosFiltrados;
    private FirebaseAuth mAuth;
    private String usuarioActualUid;
    private FirebaseFirestore db;
    private List<String> favoritos;
    private GestorFirestore gestorFirebase;

    public AdaptadorUsuariosRecycler(ArrayList<Usuario> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
        this.listaUsuariosFiltrados = new ArrayList<>();
        listaUsuariosFiltrados.addAll(listaUsuarios);
        gestorFirebase = new GestorFirestore();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView lblNombreUsuario, lblMediaEstrellas;
        private Button btnFav, btnVerPerf;
        private boolean isFavorite;
        private ImageView fotoPerfil;

        public ViewHolder(View v) {
            super(v);
            lblNombreUsuario = v.findViewById(R.id.txtNombreUsu);
            btnFav = v.findViewById(R.id.btnCoraVacio);
            btnVerPerf = v.findViewById(R.id.btnVerPerfil);
            fotoPerfil = v.findViewById(R.id.fotoPerfilExplora);
            lblMediaEstrellas = v.findViewById(R.id.txtValoracion);

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
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        usuarioActualUid = mAuth.getCurrentUser().getUid();
        holder.isFavorite = false;

        db.collection(COLECCION).document(usuarioActualUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();

                //Obtenemos el usuario de la base de datos con todos sus campos
                Usuario usuario = document.toObject(Usuario.class);

                favoritos = usuario.getListaFavoritos();
                if (listaUsuariosFiltrados.size() > position) {
                    if (favoritos.contains(listaUsuariosFiltrados.get(position).getId())) {
                        holder.isFavorite = true;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            holder.btnFav.setForeground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.corazon_favoritos_relleno));
                        }
                    } else {
                        holder.isFavorite = false;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            holder.btnFav.setForeground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.corazon_favoritos_vacio));
                        }
                    }
                }

            }
        });

        gestorFirebase.obtenerMediaResenas(listaUsuariosFiltrados.get(position).getId(), new GestorFirestore.Callback() {
            @Override
            public void onSuccess(Object mediaEstrellas) {
                holder.lblMediaEstrellas.setText(mediaEstrellas.toString());
            }
        });

        holder.lblNombreUsuario.setText(listaUsuariosFiltrados.get(position).getNombre().toUpperCase(Locale.ROOT));

        if (listaUsuariosFiltrados.get(position).getFotoPerfil().equals(holder.itemView.getContext().getString(R.string.urlImagenPerfilPorDefecto))){
            holder.fotoPerfil.setScaleType(ImageView.ScaleType.FIT_CENTER);
            try {
                Glide.with(holder.itemView.getContext()).load(listaUsuariosFiltrados.get(position).getFotoPerfil()).into(holder.fotoPerfil);
            } catch (Exception e) {
            }
        }else{
            try {
                holder.fotoPerfil.setScaleType(ImageView.ScaleType.FIT_XY);
                Glide.with(holder.itemView.getContext()).load(listaUsuariosFiltrados.get(position).getFotoPerfil()).fitCenter().into(holder.fotoPerfil);
            } catch (Exception e) {
            }
        }



        holder.btnVerPerf.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), PerfilUsuario.class);
            intent.putExtra("UidUsuario", listaUsuariosFiltrados.get(position).getId());
            v.getContext().startActivity(intent);
        });

        holder.btnFav.setOnClickListener(v -> {
            TransitionDrawable transitionDrawableIda = new TransitionDrawable(new Drawable[]{ContextCompat.getDrawable(v.getContext(), R.drawable.corazon_favoritos_vacio), ContextCompat.getDrawable(v.getContext(), R.drawable.corazon_favoritos_relleno)});
            TransitionDrawable transitionDrawableVuelta = new TransitionDrawable(new Drawable[]{ContextCompat.getDrawable(v.getContext(), R.drawable.corazon_favoritos_relleno), ContextCompat.getDrawable(v.getContext(), R.drawable.corazon_favoritos_vacio)

            });

            if (holder.isFavorite) {
                transitionDrawableVuelta.setCrossFadeEnabled(true);
                transitionDrawableVuelta.startTransition(300);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.btnFav.setForeground(transitionDrawableVuelta);
                }
                db.collection(COLECCION).document(usuarioActualUid).update("listaFavoritos", FieldValue.arrayRemove(listaUsuariosFiltrados.get(position).getId())).addOnSuccessListener(documentReference -> {
                    holder.isFavorite = false;
                });
            } else {
                transitionDrawableIda.setCrossFadeEnabled(true);
                transitionDrawableIda.startTransition(300);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.btnFav.setForeground(transitionDrawableIda);
                }
                db.collection(COLECCION).document(usuarioActualUid).update("listaFavoritos", FieldValue.arrayUnion(listaUsuariosFiltrados.get(position).getId())).addOnSuccessListener(documentReference -> {
                    holder.isFavorite = true;
                });
            }

        });


    }


    //será quien devuelva la cantidad de items que se encuentra en la lista
    @Override
    public int getItemCount() {
        return listaUsuariosFiltrados.size();
    }

    public void filter(String query, ProgressBar progressBar) {
        listaUsuariosFiltrados.clear();
        progressBar.setVisibility(View.VISIBLE);
        if (query.isEmpty()) {
            listaUsuariosFiltrados.addAll(listaUsuarios);
        } else {
            for (Usuario user : listaUsuarios) {
                if (user.getNombre().toLowerCase().contains(query.toLowerCase())) {
                    listaUsuariosFiltrados.add(user);
                }
            }
        }
        notifyDataSetChanged();
        new Handler().postDelayed(() -> progressBar.setVisibility(View.GONE), 900);
    }

}

