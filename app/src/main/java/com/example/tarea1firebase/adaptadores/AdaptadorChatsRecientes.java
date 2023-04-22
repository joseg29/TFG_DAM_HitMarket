package com.example.tarea1firebase.adaptadores;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tarea1firebase.entidades.Chat;
import com.example.tarea1firebase.ChatVentana;
import com.example.tarea1firebase.entidades.Mensaje;
import com.example.tarea1firebase.R;
import com.example.tarea1firebase.entidades.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorChatsRecientes extends RecyclerView.Adapter<AdaptadorChatsRecientes.ViewHolder> {
    private List<Chat> listaChats;
    private FirebaseAuth mAuth;
    private String usuarioActualUid;


    public AdaptadorChatsRecientes(ArrayList<Chat> listaUsuarios) {
        this.listaChats = listaUsuarios;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nombreUsuario, ultimoMensaje;
        private CardView cardViewChatReciente;
        private ImageView imgPerfil;
        private Usuario otroUser;
        private Mensaje ultimoMsj;


        public ViewHolder(View v) {
            super(v);
            cardViewChatReciente = v.findViewById(R.id.cardViewChatReciente);
            nombreUsuario = v.findViewById(R.id.lblNombreChat);
            ultimoMensaje = v.findViewById(R.id.lblPreviewUltimoMensajeChat);
            imgPerfil = v.findViewById(R.id.imgPerfilChat);
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
        usuarioActualUid = mAuth.getCurrentUser().getUid();
        if (listaChats.get(position).getUsuario1().getId().equals(mAuth.getCurrentUser().getUid())) {
            holder.otroUser = listaChats.get(position).getUsuario2();
            System.out.println(holder.otroUser.getNombre() + " 1 -- " + position);
        } else if (listaChats.get(position).getUsuario2().getId().equals(mAuth.getCurrentUser().getUid())) {
            holder.otroUser = listaChats.get(position).getUsuario1();
            System.out.println(holder.otroUser.getNombre() + " 2 " + position);
        }


        holder.nombreUsuario.setText(holder.otroUser.getNombre());
        if (!holder.otroUser.getFotoPerfil().equals("")) {
            try {
                Glide.with(holder.itemView.getContext()).load(holder.otroUser.getFotoPerfil()).into(holder.imgPerfil);
            } catch (Exception e) {
            }
        }
        String chatKey;

        if (usuarioActualUid.compareTo(holder.otroUser.getId()) < 0) {
            chatKey = usuarioActualUid + "_" + holder.otroUser.getId();
        } else {
            chatKey = holder.otroUser.getId() + "_" + usuarioActualUid;
        }

        FirebaseDatabase.getInstance().getReference("chats").child(chatKey).child("listaMensajes").limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Itera sobre los hijos de la referencia a los mensajes
                for (DataSnapshot mensajeSnapshot : snapshot.getChildren()) {
                    holder.ultimoMsj = mensajeSnapshot.getValue(Mensaje.class);
                }
                holder.ultimoMensaje.setText(holder.ultimoMsj.getTexto());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Maneja el error
            }
        });

        holder.cardViewChatReciente.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), ChatVentana.class);
            intent.putExtra("UsuarioActual", mAuth.getCurrentUser().getUid());
            intent.putExtra("UidUsuarioReceptor", holder.otroUser.getId());
            holder.itemView.getContext().startActivity(intent);
        });
    }


    //será quien devuelva la cantidad de items que se encuentra en la lista
    @Override
    public int getItemCount() {
        return listaChats.size();
    }

    public void setData(List<Chat> data) {
        listaChats.clear();
        listaChats.addAll(data);
        notifyDataSetChanged();
    }
}
