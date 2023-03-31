package com.example.tarea1firebase;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorChatsRecientes extends RecyclerView.Adapter<AdaptadorChatsRecientes.ViewHolder> {
    private List<Usuario> listaChats;
    private FirebaseAuth mAuth;

    public AdaptadorChatsRecientes(ArrayList<Usuario> listaUsuarios) {
        this.listaChats = listaUsuarios;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nombreUsuario, textoMensaje;
        private CardView cardViewChatReciente;

        public ViewHolder(View v) {
            super(v);
            cardViewChatReciente = v.findViewById(R.id.cardViewChatReciente);
            nombreUsuario = v.findViewById(R.id.lblNombreChat);
            textoMensaje = v.findViewById(R.id.lblPreviewUltimoMensajeChat);
        }
    }

    //será quien devuelva el ViewHolder con el layout seteado que previamente definimos
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_chat_reciente, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }


    //será quien se encargue de establecer los objetos en el ViewHolder
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mAuth = FirebaseAuth.getInstance();
        holder.nombreUsuario.setText(listaChats.get(position).getNombre());
        holder.textoMensaje.setText(listaChats.get(position).getEmail());

        holder.cardViewChatReciente.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), ChatVentana.class);
            intent.putExtra("UidUsuarioEmisor", mAuth.getCurrentUser().getUid());
            intent.putExtra("UidUsuarioReceptor", listaChats.get(position).getId());
            holder.itemView.getContext().startActivity(intent);
        });
    }


    //será quien devuelva la cantidad de items que se encuentra en la lista
    @Override
    public int getItemCount() {
        return listaChats.size();
    }
}
