/**
 * Esta clase es un adaptador para un RecyclerView que se utiliza para mostrar una lista de canciones.
 * El adaptador tiene una lista de URL de las canciones, y se encarga de reproducirlas y controlar su reproducción.
 * La clase tiene un ViewHolder que contiene un botón de reproducción, un reproductor de medios y un SeekBar que muestra el progreso de la canción.
 * El adaptador tiene tres métodos: onCreateViewHolder, onBindViewHolder y getItemCount, que son necesarios para implementar un RecyclerView.Adapter.
 * Además, tiene un constructor que toma una lista de URLs de las canciones.
 *
 * @author Jose Gregorio
 */
package com.example.tarea1firebase.adaptadores;

import static com.example.tarea1firebase.Registro.COLECCION;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tarea1firebase.R;
import com.example.tarea1firebase.gestor.GestorFirestore;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.List;

public class AdaptadorCancionesRecycler extends RecyclerView.Adapter<AdaptadorCancionesRecycler.ViewHolder> {
    private List<String> listaUrls;
    private FirebaseStorage storage;
    private GestorFirestore gestorFirebase;
    private FirebaseAuth mAuth;

    /**
     * Constructor para el adaptador. Recibe una lista de URLs de las canciones.
     *
     * @param listaUsuarios Lista de URLs de las canciones.
     */
    public AdaptadorCancionesRecycler(List<String> listaUsuarios) {
        this.listaUrls = listaUsuarios;
        this.storage = FirebaseStorage.getInstance();
        this.gestorFirebase = new GestorFirestore();
        this.mAuth = FirebaseAuth.getInstance();
    }

    /**
     * ViewHolder para cada elemento del RecyclerView. Contiene un botón de reproducción,
     * un reproductor de medios y un SeekBar que muestra el progreso de la canción.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageButton btnPlay, btnBorrarCancion;
        private MediaPlayer mediaPlayer;
        private SeekBar seekBar;

        /**
         * Constructor del ViewHolder. Toma una vista y busca los elementos necesarios para reproducir la canción.
         *
         * @param v Vista del elemento del RecyclerView.
         */
        public ViewHolder(View v) {
            super(v);
            btnPlay = v.findViewById(R.id.btnPlay);
            seekBar = v.findViewById(R.id.seekBar);
            mediaPlayer = new MediaPlayer();
            btnBorrarCancion = v.findViewById(R.id.btnBorrarCancion);
        }
    }

    /**
     * Crea y devuelve un ViewHolder con el layout seteado que previamente definimos.
     *
     * @param parent   ViewGroup en el que se va a agregar la vista creada.
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
     *
     * @param holder   ViewHolder que se está estableciendo.
     * @param position Posición del elemento en el RecyclerView.
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        /**
         * Establece un listener de clics para el botón, que alterna entre reproducir y pausar el audio.
         * Si el audio no está reproduciéndose, el botón cambia su icono a un icono de pausa, y la reproducción de audio comienza.
         * Si el audio ya está reproduciéndose, el botón cambia su icono a un icono de reproducción, y la reproducción de audio se pausa.
         * La barra de progreso se actualiza para mostrar el progreso de la reproducción de audio.
         *
         * @param listener El listener de clics que se invocará cuando se haga clic en el botón.
         *                 Debe contener la lógica para alternar entre la reproducción y la pausa del audio.
         */
        holder.btnPlay.setOnClickListener(view -> {
            /*
             * Verifica si el reproductor de medios no está reproduciendo actualmente.
             */
            if (!holder.mediaPlayer.isPlaying()) {
                /*
                 * Cambia la imagen del botón a "iconopausa".
                 */
                holder.btnPlay.setImageResource(R.drawable.iconopausa);
                /*
                 * Inicia la reproducción del reproductor de medios.
                 */
                holder.mediaPlayer.start();
            } else {
                /*
                 * Cambia la imagen del botón a play.
                 */
                holder.btnPlay.setImageResource(R.drawable.play);
                /*
                 * Pausa la reproducción del reproductor de medios.
                 */
                holder.mediaPlayer.pause();
            }
            /*
             *Configura el drawable y el thumb de la barra de progreso cuando se empieza a mover manualmente.
             */
            Drawable playingDrawable = ContextCompat.getDrawable(holder.seekBar.getContext(), R.drawable.custom_seekbar_progress_2);
            Drawable thumb = ContextCompat.getDrawable(holder.seekBar.getContext(), R.drawable.custom_thumb);
            holder.seekBar.setProgressDrawable(playingDrawable);
            holder.seekBar.setThumb(thumb);

            /*
             * Ejecuta el código de forma asíncrona en el hilo principal de la actividad.
             * Actualiza el progreso de la canción en el SeekBar de forma continua.
             */
            Handler mHandler = new Handler();
            ((Activity) holder.itemView.getContext()).runOnUiThread(new Runnable() {
                /**
                 Implementación del método run de la interfaz Runnable, que se ejecuta periódicamente para actualizar la posición actual
                 de reproducción del audio en el SeekBar.
                 Si el objeto MediaPlayer no es nulo, actualiza el valor máximo del SeekBar con la duración total del audio y establece
                 el progreso del SeekBar en la posición actual de reproducción. Después de la actualización, se llama a la función
                 refreshDrawableState() para actualizar la vista del SeekBar.
                 El método utiliza un objeto Handler para programar la actualización del progreso cada 100 milisegundos, llamando a
                 postDelayed() para agregar un objeto Runnable a la cola de mensajes de la interfaz de usuario.
                 */
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
            /*
             * Restablece el reproductor de medios a su estado inicial.
             */
            holder.mediaPlayer.reset();
            /*
             * Establece la fuente de datos para el reproductor de medios.
             */
            holder.mediaPlayer.setDataSource(listaUrls.get(position));
            /*
             * Prepara el reproductor de medios para la reproducción.
             */
            holder.mediaPlayer.prepare();
            /*
             *Listener para manejar los cambios en una barra de progreso.
             */
            holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                /**
                 * Método que se llama cuando el usuario cambia manualmente el progreso de la barra de progreso.
                 * Actualiza la posición actual del MediaPlayer y la barra de progreso para reflejar el cambio hecho por el usuario.
                 *
                 * @param seekBar la barra de progreso que se está moviendo
                 * @param progress el nuevo progreso de la barra de progreso
                 * @param fromUser si el cambio de progreso fue hecho por el usuario
                 */
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        holder.mediaPlayer.seekTo(progress);
                        holder.seekBar.setProgress(progress);
                        holder.seekBar.refreshDrawableState();
                    }
                }

                /**
                 * Método que se llama cuando el usuario comienza a mover manualmente la barra de progreso.
                 * Establece el drawable y el thumb de la barra de progreso para dar retroalimentación visual al usuario de que se está moviendo manualmente.
                 *
                 * @param seekBar la barra de progreso que se está moviendo
                 */
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    Drawable playingDrawable = ContextCompat.getDrawable(holder.seekBar.getContext(), R.drawable.custom_seekbar_progress_2);
                    Drawable thumb = ContextCompat.getDrawable(holder.seekBar.getContext(), R.drawable.custom_thumb);
                    holder.seekBar.setProgressDrawable(playingDrawable);
                    holder.seekBar.setThumb(thumb);
                }

                /**
                 Método que se ejecuta cuando se detiene el movimiento manual del seekBar.
                 No se requiere ninguna acción en este caso, por lo que el método está vacío.
                 @param seekBar el objeto SeekBar que se está manipulando
                 */
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            /*
             * si hay algún problema al configurar el MediaPlayer o el SeekBar.
             * */
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
         * Listener que detecta cuando se ha completado la reproducción del audio.
         */
        holder.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            /**
             * Método que se llama cuando la canción ha terminado de reproducirse.
             * <p>
             * Restablece el botón de reproducción a su estado inicial y la barra de progreso a 0.
             * Establece el drawable y el thumb de la barra de progreso para reflejar el estado de reproducción pausado.
             *
             * @param mp el MediaPlayer que ha completado la reproducción de la canción
             */
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
        /*
        Click listener del botón de borrar canciones que elimina la canción de la lista de canciones del usuario y del bucket de storage
         */
        holder.btnBorrarCancion.setOnClickListener(v -> {
            String url = listaUrls.get(position);
            crearDialogoConfirmacion(holder, url);
        });
    }

    /**
     * Método que abre un diálogo para solicitar comprobación antes de eliminar la cacnión.
     * <p>
     * Elimina la canción de "arrayCanciones" del usuario en FirebaseFirestore
     * Elimina la canción del bucket de storage de firebase
     * Actualiza el recycler
     *
     * @param holder la referencia del ViewHolder
     * @param url    la url de la canción a eliminar
     */
    public void crearDialogoConfirmacion(ViewHolder holder, String url) {
        /*
        Creamos la referencia a la canción a partir de una url de descarga obtenida de la lista pasada anteriormente al adapter.
         */
        StorageReference referenciaCancion = storage.getReferenceFromUrl(url);

        /*
        Creamos el builder del diálogo
         */
        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
        builder.setMessage("¿Está seguro que desea eliminar esta canción?");
        builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                /*
                Cuando se presione el botón de confirmar, se llama al bucket de FirebaseStorage para eliminar la canción
                 */
                referenciaCancion.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        /**
                         * Una vez eliminada la canción, se borra la url del arrayCanciones del usuario
                         * @see gestorFirebase
                         */

                        gestorFirebase.borrarValorArray(mAuth.getCurrentUser().getUid(), "arrayCanciones", url, new GestorFirestore.Callback<String>() {
                            @Override
                            public void onSuccess(String result) {
                                listaUrls.remove(url);
                                notifyDataSetChanged();
                            }
                        });
                    }
                });
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Devuelve la cantidad de elementos en la lista.
     *
     * @return La cantidad de elementos en la lista.
     */
    @Override
    public int getItemCount() {
        return listaUrls.size();
    }


}
