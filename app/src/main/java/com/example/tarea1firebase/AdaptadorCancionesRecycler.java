package com.example.tarea1firebase;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;

import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;

public class AdaptadorCancionesRecycler extends RecyclerView.Adapter<AdaptadorCancionesRecycler.ViewHolder> {
    private List<String> listaUrls;
    private ProgressDialog dialogoCargando;

    public AdaptadorCancionesRecycler(List<String> listaUsuarios) {
        this.listaUrls = listaUsuarios;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageButton btnPlay;
        private MediaPlayer mediaPlayer;
        private SeekBar seekBar;

        public ViewHolder(View v) {
            super(v);
            btnPlay = v.findViewById(R.id.btnPlay);
            seekBar = v.findViewById(R.id.seekBar);
            mediaPlayer = new MediaPlayer();

        }
    }

    //será quien devuelva el ViewHolder con el layout seteado que previamente definimos
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_canciones, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    //será quien se encargue de establecer los objetos en el ViewHolder
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
            }

        });
    }

    //será quien devuelva la cantidad de items que se encuentra en la lista
    @Override
    public int getItemCount() {
        return listaUrls.size();
    }


}
