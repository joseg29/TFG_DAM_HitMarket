/**
 * Clase que representa el adaptador para la lista de chats recientes.
 */
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
import com.example.tarea1firebase.gestor.GestorFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador de RecyclerView para mostrar la lista de chats recientes.
 */
public class AdaptadorChatsRecientes extends RecyclerView.Adapter<AdaptadorChatsRecientes.ViewHolder> {
    private List<Chat> listaChats;
    private FirebaseAuth mAuth;
    private String usuarioActualUid;

    /**
     * Constructor que inicializa la lista de chats recientes.
     *
     * @param listaUsuarios Lista de chats recientes a mostrar.
     */
    public AdaptadorChatsRecientes(ArrayList<Chat> listaUsuarios) {
        this.listaChats = listaUsuarios;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nombreUsuario, ultimoMensaje;
        private CardView cardViewChatReciente;
        private ImageView imgPerfil;
        private Usuario otroUser;
        private String uidOtroUser;
        private Mensaje ultimoMsj;
        private GestorFirestore gestorFirestore;

        /**
         * Constructor que inicializa las views de la vista de chat reciente.
         *
         * @param v Vista de chat reciente.
         */
        public ViewHolder(View v) {
            super(v);
            cardViewChatReciente = v.findViewById(R.id.cardViewChatReciente);
            nombreUsuario = v.findViewById(R.id.lblNombreChat);
            ultimoMensaje = v.findViewById(R.id.lblPreviewUltimoMensajeChat);
            imgPerfil = v.findViewById(R.id.imgPerfilChat);
            gestorFirestore = new GestorFirestore();
        }
    }

    /**
     * Método que se encarga de inflar la vista de chat reciente.
     * @param parent   El ViewGroup al que se añadirá la vista.
     * @param viewType El tipo de vista.
     * @return Un ViewHolder que contiene la vista de chat reciente inflada.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_chat_reciente, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;

    }


    /**
     * Método que se encarga de establecer los datos en un ViewHolder.
     * @param holder   El ViewHolder en el cual se establecerán los datos.
     * @param position La posición del item en la lista de chats recientes.
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mAuth = FirebaseAuth.getInstance();
        usuarioActualUid = mAuth.getCurrentUser().getUid();
        // Obtener el UID del otro usuario con quien se mantiene la conversación
        if (listaChats.get(position).getUsuario1().equals(mAuth.getCurrentUser().getUid())) {
            holder.uidOtroUser = listaChats.get(position).getUsuario2();
        } else if (listaChats.get(position).getUsuario2().equals(mAuth.getCurrentUser().getUid())) {
            holder.uidOtroUser = listaChats.get(position).getUsuario1();
        }

        holder.gestorFirestore.obtenerUsuarioPorId(holder.uidOtroUser, new GestorFirestore.Callback<Usuario>() {
            @Override
            public void onSuccess(Usuario result) {
                holder.otroUser = result;
                holder.nombreUsuario.setText(holder.otroUser.getNombre());
                Glide.with(holder.itemView.getContext()).load(holder.otroUser.getFotoPerfil()).override(100, 100).into(holder.imgPerfil);

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
        }, Usuario.class);
    }


    /**
     * Obtiene el número de elementos en la lista de chats recientes.
     * @return El número de elementos en la lista de chats recientes.
     */
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
