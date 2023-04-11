package com.example.tarea1firebase;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorMensajesChat extends RecyclerView.Adapter<AdaptadorMensajesChat.ViewHolder> {
    private List<Mensaje> listaMensajes;
    private FirebaseAuth mAuth;

    public AdaptadorMensajesChat(ArrayList<Mensaje> listaUsuarios) {
        this.listaMensajes = listaUsuarios;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView fechaMensaje, textoMensaje;
        private LinearLayout layoutMensaje, layoutFecha;


        public ViewHolder(View v) {
            super(v);
            fechaMensaje = v.findViewById(R.id.lblMensajeHora);
            textoMensaje = v.findViewById(R.id.lblMensajeTexto);
            layoutMensaje = v.findViewById(R.id.ubicacionMensajeLayout);
            layoutFecha = v.findViewById(R.id.ubicacionFechaLayout);

        }
    }

    //será quien devuelva el ViewHolder con el layout seteado que previamente definimos
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_mensaje, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }


    //será quien se encargue de establecer los objetos en el ViewHolder
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textoMensaje.setText(listaMensajes.get(position).getTexto());
        holder.fechaMensaje.setText(listaMensajes.get(position).getFechaYHora());
        mAuth = FirebaseAuth.getInstance();

        String uidActual = mAuth.getCurrentUser().getUid();

        if (listaMensajes.get(position).getRemitente().equals(uidActual)) {
            holder.textoMensaje.setBackgroundResource(R.drawable.corner_mensaje_enviado);
            holder.layoutMensaje.setGravity(Gravity.RIGHT);
            holder.layoutFecha.setGravity(Gravity.RIGHT);
        } else {
            holder.textoMensaje.setBackgroundResource(R.drawable.corner_mensaje_recibido);
            holder.layoutMensaje.setGravity(Gravity.LEFT);
            holder.layoutFecha.setGravity(Gravity.LEFT);
        }
    }


    //será quien devuelva la cantidad de items que se encuentra en la lista
    @Override
    public int getItemCount() {
        return listaMensajes.size();
    }
}
