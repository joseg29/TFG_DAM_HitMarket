/**
 * Clase que representa el adaptador de RecyclerView para mostrar la lista de chats recientes.
 *
 * @author Jose Gregorio
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

    /**
     * Esta clase define un ViewHolder utilizado por un RecyclerView para mostrar la vista de un chat reciente.
     * Contiene un conjunto de vistas que muestran el nombre del usuario, la imagen de perfil, el
     * último mensaje y el gestor de Firestore para almacenar y recuperar información de la base de datos.
     */
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
     *
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
     *
     * @param holder   El ViewHolder en el cual se establecerán los datos.
     * @param position La posición del item en la lista de chats recientes.
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mAuth = FirebaseAuth.getInstance();
        usuarioActualUid = mAuth.getCurrentUser().getUid();
        /*
         * Verifica si el usuario actual está en la posición del Usuario1 en la listaChats.
         */
        if (listaChats.get(position).getUsuario1().equals(mAuth.getCurrentUser().getUid())) {
            /*
             * Si es así, establece el uidOtroUser como Usuario2 en la listaChats.
             */
            holder.uidOtroUser = listaChats.get(position).getUsuario2();
        } else if (listaChats.get(position).getUsuario2().equals(mAuth.getCurrentUser().getUid())) {
            /*
             * De lo contrario, establece el uidOtroUser como Usuario1 en la listaChats.
             */
            holder.uidOtroUser = listaChats.get(position).getUsuario1();
        }
        /*
         * Realiza una consulta al gestorFirestore para obtener el usuario correspondiente al uidOtroUser.
         * utilizando el método obtenerUsuarioPorId que fue creado previamente.
         * Se proporciona un objeto de tipo GestorFirestore.Callback<Usuario> para manejar el resultado de la consulta.
         */
        holder.gestorFirestore.obtenerUsuarioPorId(holder.uidOtroUser, new GestorFirestore.Callback<Usuario>() {
            @Override
            public void onSuccess(Usuario result) {
                /*
                 * Al recibir el usuario con éxito, actualiza los datos del holder y muestra la información en la interfaz.
                 */
                holder.otroUser = result;
                holder.nombreUsuario.setText(holder.otroUser.getNombre());
                if (!result.getFotoPerfil().equals("")) {
                    Glide.with(holder.itemView.getContext()).load(holder.otroUser.getFotoPerfil()).override(100, 100).into(holder.imgPerfil);
                } else {
                    Glide.with(holder.itemView.getContext()).load(holder.itemView.getContext().getString(R.string.urlImagenPerfilPorDefecto)).override(100, 100).into(holder.imgPerfil);
                }
                /*
                 * Crea una clave única para el chat combinando los ids de usuario en orden alfabético.
                 */
                String chatKey;

                if (usuarioActualUid.compareTo(holder.otroUser.getId()) < 0) {
                    chatKey = usuarioActualUid + "_" + holder.otroUser.getId();
                } else {
                    chatKey = holder.otroUser.getId() + "_" + usuarioActualUid;
                }
                /*
                 * Consulta la base de datos para obtener el último mensaje en el chat utilizando la clave del chat.
                 */
                FirebaseDatabase.getInstance().getReference("chats").child(chatKey).child("listaMensajes").limitToLast(1).addValueEventListener(new ValueEventListener() {
                    /**
                     * Método que se invoca cuando los datos de la referencia de la base de datos han cambiado.
                     * Itera sobre los hijos de la referencia a los mensajes y asigna el último mensaje obtenido al campo 'ultimoMsj' del holder.
                     * Luego, establece el texto del campo 'ultimoMensaje' con el texto del último mensaje obtenido.
                     *
                     * @param snapshot El objeto DataSnapshot que contiene los datos de la referencia de la base de datos.
                     */
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        /*
                         * Itera sobre los hijos de la referencia a los mensajes.
                         */
                        for (DataSnapshot mensajeSnapshot : snapshot.getChildren()) {
                            holder.ultimoMsj = mensajeSnapshot.getValue(Mensaje.class);
                        }
                        holder.ultimoMensaje.setText(holder.ultimoMsj.getTexto());
                    }

                    /**
                     * Método que se invoca cuando se cancela la operación de consulta a la base de datos.
                     * Maneja el error producido durante la consulta.
                     *
                     * @param error El objeto DatabaseError que contiene la información sobre el error ocurrido.
                     */
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        /*
                         * Maneja el error si la consulta es cancelada.
                         */
                    }
                });
                /*
                 * Configurar un OnClickListener para el cardViewChatReciente.
                 * Cuando se hace clic en el card view, se creará un nuevo Intent para abrir la actividad ChatVentana.
                 * Se agregan datos extra al Intent, como el ID del usuario actual y el ID del usuario receptor.
                 * Luego, se inicia la actividad utilizando el Intent.
                 */
                holder.cardViewChatReciente.setOnClickListener(v -> {
                    /*
                     * Crear una nueva instancia de Intent para abrir la actividad ChatVentana.
                     */
                    Intent intent = new Intent(holder.itemView.getContext(), ChatVentana.class);
                    /*
                     * Agregar datos extra al Intent, como el ID del usuario actual y el ID del usuario receptor.
                     */
                    intent.putExtra("UsuarioActual", mAuth.getCurrentUser().getUid());
                    intent.putExtra("UidUsuarioReceptor", holder.otroUser.getId());
                    /*
                     * Iniciar la actividad utilizando el Intent creado.
                     */
                    holder.itemView.getContext().startActivity(intent);
                });
            }
            /*
             * Se espera obtener un objeto de la clase Usuario al llamar al método obtenerUsuarioPorId.
             * El argumento Usuario.class indica el tipo de resultado esperado.
             */
        }, Usuario.class);
    }


    /**
     * Obtiene el número de elementos en la lista de chats recientes.
     *
     * @return El número de elementos en la lista de chats recientes.
     */
    @Override
    public int getItemCount() {
        return listaChats.size();
    }

    /**
     * Método que establece la nueva lista de chats y actualiza el adapter.
     * Borra la lista actual de chats, agrega los nuevos datos y notifica al adapter para que actualice la vista.
     *
     * @param data Nueva lista de chats a establecer en el adapter.
     */
    public void setData(List<Chat> data) {
        listaChats.clear();
        listaChats.addAll(data);
        notifyDataSetChanged();
    }
}
