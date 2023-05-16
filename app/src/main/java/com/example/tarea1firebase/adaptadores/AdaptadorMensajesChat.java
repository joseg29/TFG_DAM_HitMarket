/**
 * Adaptador encargado de manejar la lista de mensajes en el chat y mostrarlos en la interfaz de usuario.
 * @author
 */
package com.example.tarea1firebase.adaptadores;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.tarea1firebase.entidades.Mensaje;
import com.example.tarea1firebase.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorMensajesChat extends RecyclerView.Adapter<AdaptadorMensajesChat.ViewHolder> {
    private List<Mensaje> listaMensajes;
    private FirebaseAuth mAuth;

    /**
     * Constructor de la clase AdaptadorMensajesChat.
     * @param listaUsuarios Lista de mensajes a mostrar en el chat.
     */
    public AdaptadorMensajesChat(ArrayList<Mensaje> listaUsuarios) {
        this.listaMensajes = listaUsuarios;
    }

    /**
     * Clase interna encargada de manejar los elementos de la vista de cada mensaje en el chat.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView fechaMensaje, textoMensaje;
        private LinearLayout layoutMensaje, layoutFecha;

        /**
         * Constructor de la clase ViewHolder.
         * @param v Vista que contiene los elementos de la vista de cada mensaje en el chat.
         */
        public ViewHolder(View v) {
            super(v);
            fechaMensaje = v.findViewById(R.id.lblMensajeHora);
            textoMensaje = v.findViewById(R.id.lblMensajeTexto);
            layoutMensaje = v.findViewById(R.id.ubicacionMensajeLayout);
            layoutFecha = v.findViewById(R.id.ubicacionFechaLayout);

        }
    }

    /**
     * Método encargado de crear la vista de cada mensaje en el chat.
     * @param parent   Grupo al que se va a agregar la vista del mensaje.
     * @param viewType Tipo de vista que se está creando.
     * @return Retorna el ViewHolder con el layout seteado que previamente definimos.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_mensaje, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }


    /**
     * Configura la apariencia de los mensajes dentro del adaptador de RecyclerView según si el
     * remitente es el usuario actual o no.
     * @param holder   ViewHolder que contiene los elementos de la vista de cada mensaje en el chat.
     * @param position La posición del mensaje dentro del adaptador.
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        /*
         * Establece el texto del mensaje en el campo de texto correspondiente en el ViewHolder.
         * El texto se obtiene de la lista de mensajes en la posición indicada.
         */
        holder.textoMensaje.setText(listaMensajes.get(position).getTexto());
        /*
         * Establece la fecha y hora del mensaje en el campo de texto correspondiente en el ViewHolder.
         * La fecha y hora se obtienen de la lista de mensajes en la posición indicada.
         */
        holder.fechaMensaje.setText(listaMensajes.get(position).getFechaYHora());
        /*
         * Obtiene una instancia de FirebaseAuth para autenticar al usuario actual.
         */
        mAuth = FirebaseAuth.getInstance();
        /*
         * Obtiene el UID del usuario actual autenticado.
         */
        String uidActual = mAuth.getCurrentUser().getUid();
        /*
         * Si el remitente del mensaje es igual al UID del usuario actual,
         * se considera que el mensaje fue enviado por el usuario actual.
         * Ajusta la apariencia del mensaje enviado en el ViewHolder.
         */
        if (listaMensajes.get(position).getRemitente().equals(uidActual)) {
            /*
             * Establece el fondo del campo de texto del mensaje enviado en el ViewHolder.
             */
            holder.textoMensaje.setBackgroundResource(R.drawable.corner_mensaje_enviado);
            /*
             * Alinea el texto del mensaje enviado a la derecha en el ViewHolder.
             */
            holder.textoMensaje.setGravity(Gravity.RIGHT);
            /*
             * Establece el relleno (padding) del campo de texto del mensaje enviado en el ViewHolder.
             */
            holder.textoMensaje.setPadding(0, 12, 25, 0);
            /*
             * Alinea el contenedor del mensaje enviado a la derecha en el ViewHolder.
             */
            holder.layoutMensaje.setGravity(Gravity.RIGHT);
            /*
             * Alinea el contenedor de la fecha del mensaje enviado a la derecha en el ViewHolder.
             */
            holder.layoutFecha.setGravity(Gravity.RIGHT);
            /*
             * Alinea el texto de la fecha del mensaje enviado a la derecha en el ViewHolder.
             */
            holder.fechaMensaje.setGravity(Gravity.RIGHT);
            /*
             * Si el remitente del mensaje no coincide con el UID del usuario actual,
             * se considera que el mensaje fue recibido.
             * Ajusta la apariencia del mensaje recibido en el ViewHolder.
             */
        } else {
            /*
             * Establece el fondo del campo de texto del mensaje recibido en el ViewHolder.
             */
            holder.textoMensaje.setBackgroundResource(R.drawable.corner_mensaje_recibido);
            /*
             * Establece el relleno (padding) del campo de texto del mensaje recibido en el ViewHolder.
             */
            holder.textoMensaje.setPadding(25, 12, 0, 0);
            /*
             * Alinea el contenedor del mensaje recibido a la izquierda en el ViewHolder.
             */
            holder.layoutMensaje.setGravity(Gravity.LEFT);
            /*
             * Alinea el contenedor de la fecha del mensaje recibido a la izquierda en el ViewHolder.
             */
            holder.layoutFecha.setGravity(Gravity.LEFT);
            /*
             * Alinea el texto de la fecha del mensaje recibido a la izquierda en el ViewHolder.
             */
            holder.fechaMensaje.setGravity(Gravity.LEFT);
        }
    }


    /**
     * Método que devuelve la cantidad de elementos en la lista de mensajes.
     * @return La cantidad de mensajes en la lista.
     */
    @Override
    public int getItemCount() {
        return listaMensajes.size();
    }
}
