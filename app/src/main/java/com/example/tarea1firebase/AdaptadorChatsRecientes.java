package com.example.tarea1firebase;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorChatsRecientes extends RecyclerView.Adapter<AdaptadorChatsRecientes.ViewHolder> {
    private List<Usuario> listaChats;
    private FirebaseAuth mAuth;
    private String usuarioActualUid, otroUsuarioChateando;
    private ArrayList<Mensaje> listaMensajes;

    public AdaptadorChatsRecientes(ArrayList<Usuario> listaUsuarios) {
        this.listaChats = listaUsuarios;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nombreUsuario, ultimoMensaje;
        private CardView cardViewChatReciente;


        public ViewHolder(View v) {
            super(v);
            cardViewChatReciente = v.findViewById(R.id.cardViewChatReciente);
            nombreUsuario = v.findViewById(R.id.lblNombreChat);
            ultimoMensaje = v.findViewById(R.id.lblPreviewUltimoMensajeChat);
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
        otroUsuarioChateando = listaChats.get(position).getId();

        holder.nombreUsuario.setText(listaChats.get(position).getNombre());

        String chatKey;

        if (usuarioActualUid.compareTo(otroUsuarioChateando) < 0) {
            chatKey = usuarioActualUid + "_" + otroUsuarioChateando;
        } else {
            chatKey = otroUsuarioChateando + "_" + usuarioActualUid;
        }

        FirebaseDatabase.getInstance().getReference("chats").child(chatKey).child("mensajes").limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaMensajes = new ArrayList<>();
                // Itera sobre los hijos de la referencia a los mensajes
                for (DataSnapshot mensajeSnapshot : snapshot.getChildren()) {
                    // Obtiene los datos del mensaje y haz lo que necesites con ellos
                    String mensaje = mensajeSnapshot.child("texto").getValue(String.class);
                    String remitenteUid = mensajeSnapshot.child("remitente").getValue(String.class);
                    String timestamp = mensajeSnapshot.child("fechaYHora").getValue(String.class);
                    Mensaje msj = new Mensaje(remitenteUid, mensaje, timestamp);

                    listaMensajes.add(msj);
                }
                holder.ultimoMensaje.setText(listaMensajes.get(0).getTexto());
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Maneja el error
            }
        });
        holder.cardViewChatReciente.setOnClickListener(v ->

        {
            Intent intent = new Intent(holder.itemView.getContext(), ChatVentana.class);
            intent.putExtra("UsuarioActual", mAuth.getCurrentUser().getUid());
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
