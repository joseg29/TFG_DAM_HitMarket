/**
 * Esta clase es un adaptador para un RecyclerView que se utiliza para mostrar una lista de canciones.
 * El adaptador tiene una lista de URL de las canciones, y se encarga de reproducirlas y controlar su reproducción.
 * La clase tiene un ViewHolder que contiene un botón de reproducción, un reproductor de medios y un SeekBar que muestra el progreso de la canción.
 * El adaptador tiene tres métodos: onCreateViewHolder, onBindViewHolder y getItemCount, que son necesarios para implementar un RecyclerView.Adapter.
 * Además, tiene un constructor que toma una lista de URLs de las canciones.
 */
package com.example.tarea1firebase.adaptadores;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tarea1firebase.R;

import java.io.IOException;
import java.util.List;

public class AdaptadorCancionesRecycler extends RecyclerView.Adapter<AdaptadorCancionesRecycler.ViewHolder> {
    private List<String> listaUrls;

    /**
     * Constructor para el adaptador. Recibe una lista de URLs de las canciones.
     * @param listaUsuarios Lista de URLs de las canciones.
     */
    public AdaptadorCancionesRecycler(List<String> listaUsuarios) {
        this.listaUrls = listaUsuarios;
    }

    /**
     * ViewHolder para cada elemento del RecyclerView. Contiene un botón de reproducción,
     * un reproductor de medios y un SeekBar que muestra el progreso de la canción.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageButton btnPlay;
        private MediaPlayer mediaPlayer;
        private SeekBar seekBar;

        /**
         * Constructor del ViewHolder. Toma una vista y busca los elementos necesarios para reproducir la canción.
         * @param v Vista del elemento del RecyclerView.
         */
        public ViewHolder(View v) {
            super(v);
            btnPlay = v.findViewById(R.id.btnPlay);
            seekBar = v.findViewById(R.id.seekBar);
            mediaPlayer = new MediaPlayer();

        }
    }

    /**
     * Crea y devuelve un ViewHolder con el layout seteado que previamente definimos.
     * @param parent ViewGroup en el que se va a agregar la vista creada.
     * @param viewType Tipo de la vista.
     * @return ViewHolder con el layout seteado que previamente definimos.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_canciones, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    /**
     * Establece los objetos en el ViewHolder. Se encarga de reproducir la canción, establecer el progreso en el SeekBar y controlar la pausa y reproducción.
     * @param holder ViewHolder que se está estableciendo.
     * @param position Posición del elemento en el RecyclerView.
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.btnPlay.setOnClickListener(view -> {
            if (!holder.mediaPlayer.isPlaying()) {
                holder.btnPlay.setImageResource(R.drawable.iconopausa);
                holder.mediaPlayer.start();
            } else {
                holder.btnPlay.setImageResource(R.drawable.play);
                holder.mediaPlayer.pause();
            }
            Drawable playingDrawable = ContextCompat.getDrawable(holder.seekBar.getContext(), R.drawable.custom_seekbar_progress_2);
            Drawable thumb = ContextCompat.getDrawable(holder.seekBar.getContext(), R.drawable.custom_thumb);
            holder.seekBar.setProgressDrawable(playingDrawable);
            holder.seekBar.setThumb(thumb);

            Handler mHandler = new Handler();
            ((Activity) holder.itemView.getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (holder.mediaPlayer != null) {
                        holder.seekBar.setMax(holder.mediaPlayer.getDuration());
                        int mCurrentPosition = holder.mediaPlayer.getCurrentPosition();
                        holder.seekBar.setProgress(mCurrentPosition);
                        holder.seekBar.refreshDrawableState();
                    }
                    mHandler.postDelayed(this, 100);
                }
            });
        });
        // Configura el MediaPlayer y el SeekBar.
        try {
            holder.mediaPlayer.reset();
            holder.mediaPlayer.setDataSource(listaUrls.get(position));
            holder.mediaPlayer.prepare();

            holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                //Listener para mover manualmente el seekBar
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        holder.mediaPlayer.seekTo(progress);
                        holder.seekBar.setProgress(progress);
                        holder.seekBar.refreshDrawableState();
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    Drawable playingDrawable = ContextCompat.getDrawable(holder.seekBar.getContext(), R.drawable.custom_seekbar_progress_2);
                    Drawable thumb = ContextCompat.getDrawable(holder.seekBar.getContext(), R.drawable.custom_thumb);
                    holder.seekBar.setProgressDrawable(playingDrawable);
                    holder.seekBar.setThumb(thumb);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });


        } catch (IOException e) {
            e.printStackTrace();
        }

        //Listener que detecta cuando se termina la canción
        holder.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                holder.btnPlay.setImageResource(R.drawable.play);
                mp.seekTo(0);
                holder.seekBar.setProgress(0);
                Drawable playingDrawable = ContextCompat.getDrawable(holder.seekBar.getContext(), R.drawable.custom_seekbar_progress);
                Drawable thumb = ContextCompat.getDrawable(holder.seekBar.getContext(), R.drawable.custom_thumb);
                holder.seekBar.setProgressDrawable(playingDrawable);
                holder.seekBar.setThumb(thumb);
            }

        });
    }

    /**
     Devuelve la cantidad de elementos en la lista.
     @return La cantidad de elementos en la lista.
     */
    @Override
    public int getItemCount() {
        return listaUrls.size();
    }


}
