package com.example.tarea1firebase;

import static com.example.tarea1firebase.Registro.COLECCION;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private boolean isFavorite;
    private FirebaseAuth mAuth;
    private String usuarioActualUid;
    private FirebaseFirestore db;
    private List<String> favoritos;

    public AdaptadorUsuariosRecycler(ArrayList<Usuario> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
        isFavorite = false;
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
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        usuarioActualUid = mAuth.getCurrentUser().getUid();

        db.collection(COLECCION).document(usuarioActualUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();

                //Obtenemos el usuario de la base de datos con todos sus campos
                Usuario usuario = document.toObject(Usuario.class);

                favoritos = usuario.getListaFavoritos();
                if (favoritos.contains(listaUsuarios.get(holder.getAdapterPosition()).getId())) {
                    isFavorite = true;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        holder.btnFav.setForeground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.corazon_favoritos_relleno));
                    }
                }
            }
        });

        holder.nombreUsu.setText(listaUsuarios.get(position).getNombre().toUpperCase(Locale.ROOT));

        holder.btnVerPerf.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), PerfilUsuario.class);
            intent.putExtra("UidUsuario", listaUsuarios.get(position).getId());
            v.getContext().startActivity(intent);
        });

        holder.btnFav.setOnClickListener(v -> {
            TransitionDrawable transitionDrawableIda = new TransitionDrawable(new Drawable[]{ContextCompat.getDrawable(v.getContext(), R.drawable.corazon_favoritos_vacio), ContextCompat.getDrawable(v.getContext(), R.drawable.corazon_favoritos_relleno)});
            TransitionDrawable transitionDrawableVuelta = new TransitionDrawable(new Drawable[]{ContextCompat.getDrawable(v.getContext(), R.drawable.corazon_favoritos_relleno), ContextCompat.getDrawable(v.getContext(), R.drawable.corazon_favoritos_vacio)

            });

            if (isFavorite) {
                transitionDrawableVuelta.setCrossFadeEnabled(true);
                transitionDrawableVuelta.startTransition(300);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.btnFav.setForeground(transitionDrawableVuelta);
                }
                db.collection(COLECCION).document(usuarioActualUid).update("listaFavoritos", FieldValue.arrayRemove(listaUsuarios.get(position).getId())).addOnSuccessListener(documentReference -> {
                    isFavorite = false;
                });
            } else {
                transitionDrawableIda.setCrossFadeEnabled(true);
                transitionDrawableIda.startTransition(300);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.btnFav.setForeground(transitionDrawableIda);
                }
                db.collection(COLECCION).document(usuarioActualUid).update("listaFavoritos", FieldValue.arrayUnion(listaUsuarios.get(position).getId())).addOnSuccessListener(documentReference -> {
                    isFavorite = true;
                });
            }
        });
    }


    //será quien devuelva la cantidad de items que se encuentra en la lista
    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }


}
