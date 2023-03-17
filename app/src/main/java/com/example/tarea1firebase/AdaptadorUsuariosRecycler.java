package com.example.tarea1firebase;

import static com.example.tarea1firebase.Registro.COLECCION;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorUsuariosRecycler extends RecyclerView.Adapter<AdaptadorUsuariosRecycler.ViewHolder> {
    private List<Usuario> listaUsuarios;

    public AdaptadorUsuariosRecycler(ArrayList<Usuario> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nombreUsu;
        private Button btnVer;


        public ViewHolder(View v) {
            super(v);
            nombreUsu = v.findViewById(R.id.txtNombreUsu);
            btnVer = v.findViewById(R.id.button);
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
        holder.nombreUsu.setText(listaUsuarios.get(position).getNombre());
        holder.btnVer.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), PerfilUsuario.class);
            intent.putExtra("UidUsuario", listaUsuarios.get(position).getId());
            v.getContext().startActivity(intent);
        });
    }

    //será quien devuelva la cantidad de items que se encuentra en la lista
    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }
}
