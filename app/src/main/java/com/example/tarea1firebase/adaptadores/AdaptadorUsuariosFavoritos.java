/**
 * Esta clase representa el adaptador para el RecyclerView que muestra la lista de usuarios
 * explorables por el usuario actual.
 * El adaptador se encarga de asociar cada elemento de la lista con su respectiva vista en el
 * RecyclerView y gestionar los eventos de interacción de los elementos de la lista, como la
 * selección de favoritos o la visualización del perfil de un usuario.
 *
 * @author Samuel Ortega Botias
 */
package com.example.tarea1firebase.adaptadores;

import static com.example.tarea1firebase.Registro.COLECCION;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tarea1firebase.PerfilUsuario;
import com.example.tarea1firebase.R;
import com.example.tarea1firebase.entidades.Usuario;
import com.example.tarea1firebase.gestor.GestorFirestore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdaptadorUsuariosFavoritos extends RecyclerView.Adapter<AdaptadorUsuariosFavoritos.ViewHolder> {
    private List<Usuario> listaUsuarios;
    private List<Usuario> listaUsuariosFiltrados;
    private FirebaseAuth mAuth;
    private String usuarioActualUid;
    private FirebaseFirestore db;
    private List<String> favoritos;
    private GestorFirestore gestorFirebase;

    /**
     * Constructor para el adaptador de usuarios.
     *
     * @param listaUsuarios la lista de usuarios que se mostrarán en el RecyclerView
     */
    public AdaptadorUsuariosFavoritos(ArrayList<Usuario> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
        this.listaUsuariosFiltrados = new ArrayList<>();
        listaUsuariosFiltrados.addAll(listaUsuarios);
        gestorFirebase = new GestorFirestore();
    }

    /**
     * Clase estática que representa el ViewHolder de un elemento en el RecyclerView.
     * Contiene referencias a los elementos de la vista que se mostrarán en el ViewHolder.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView lblNombreUsuario, lblMediaEstrellas, lblUbicacion, lblView3;
        private Button btnFav, btnVerPerf;
        private boolean isFavorite;
        private ImageView fotoPerfil;

        /**
         * Constructor para la clase ViewHolder.
         *
         * @param v la vista que se asociará con el ViewHolder
         */
        public ViewHolder(View v) {
            super(v);
            lblNombreUsuario = v.findViewById(R.id.txtNombreUsu);
            lblUbicacion = v.findViewById(R.id.txtUbicacion);
            lblView3 = v.findViewById(R.id.textView3);
            btnFav = v.findViewById(R.id.btnCoraVacio);
            btnVerPerf = v.findViewById(R.id.btnVerPerfil);
            fotoPerfil = v.findViewById(R.id.fotoPerfilExplora);
            lblMediaEstrellas = v.findViewById(R.id.txtValoracion);

        }
    }

    /**
     * Este método es llamado por el RecyclerView para crear una nueva vista para un elemento de la lista.
     *
     * @param parent   el ViewGroup en el que se añadirá la vista.
     * @param viewType el tipo de vista del elemento
     * @return una nueva instancia de la vista del elemento
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adaptador_usuarios_favoritos, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    /**
     * Método que establece los objetos en el ViewHolder
     *
     * @param holder   ViewHolder donde se establecerán los objetos
     * @param position posición del elemento en la lista
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        /*
         * Obtiene una instancia de la base de datos Firestore.
         */
        db = FirebaseFirestore.getInstance();
        /*
         * Obtiene una instancia de la autenticación Firebase.
         */
        mAuth = FirebaseAuth.getInstance();
        /*
         * Obtiene una instancia de la autenticación Firebase.
         */
        usuarioActualUid = mAuth.getCurrentUser().getUid();
        /*
         * Establece la bandera isFavorite en falso.
         */
        holder.isFavorite = false;
        /*
         * Obtiene el documento correspondiente al usuario actual de la colección especificada en COLECCION.
         * Agrega un listener para ser notificado cuando se complete la operación.
         */
        db.collection(COLECCION).document(usuarioActualUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            /**
             * Método que se llama cuando se completa una tarea para obtener un DocumentSnapshot en Firebase Firestore.
             * Se encarga de procesar el DocumentSnapshot y actualizar el estado y la apariencia del botón de favoritos en el ViewHolder.
             *
             * @param task La tarea completada que contiene el DocumentSnapshot.
             */
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                /*
                 * Obtiene el DocumentSnapshot de la tarea completada.
                 */
                DocumentSnapshot document = task.getResult();
                /*
                 * Convierte el DocumentSnapshot en un objeto de la clase Usuario.
                 */
                Usuario usuario = document.toObject(Usuario.class);
                /*
                 * Obtiene la lista de favoritos del usuario.
                 */
                favoritos = usuario.getListaFavoritos();
                /*
                 * Verifica si la posición actual es válida en la lista de usuarios filtrados.
                 */
                if (listaUsuariosFiltrados.size() > position) {
                    /*
                     * Verifica si el usuario actual se encuentra en la lista de favoritos.
                     */
                    if (favoritos.contains(listaUsuariosFiltrados.get(position).getId())) {
                        /*
                         * El usuario actual es un favorito.
                         * Establece la bandera isFavorite en verdadero.
                         */
                        holder.isFavorite = true;
                        /*
                         * Actualiza la apariencia del botón de favoritos con el drawable de corazón relleno.
                         */
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            holder.btnFav.setForeground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.corazon_favoritos_relleno));
                        }
                    } else {
                        /*
                         * El usuario actual no es un favorito.
                         * Establece la bandera isFavorite en falso.
                         */
                        holder.isFavorite = false;
                        /*
                         * Actualiza la apariencia del botón de favoritos con el drawable de corazón vacío.
                         */
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            holder.btnFav.setForeground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.corazon_favoritos_vacio));
                        }
                    }
                }

            }
        });
        /*
         * Llamada al método obtenerMediaResenas() del objeto gestorFirebase.
         * Se pasa como argumento el ID del usuario en la posición actual de la
         * listaUsuariosFiltrados.
         * Se crea una nueva instancia de la interfaz GestorFirestore.Callback y se
         * proporciona una implementación anónima.
         */
        gestorFirebase.obtenerMediaResenas(listaUsuariosFiltrados.get(position).getId(), new GestorFirestore.Callback() {
            /**
             * Se llama cuando se recibe un resultado exitoso en la obtención de la media de estrellas.
             * Actualiza el texto del lblMediaEstrellas con el valor de mediaEstrellas convertido a una cadena.
             *
             * @param mediaEstrellas El valor de la media de estrellas obtenido.
             */
            @Override
            public void onSuccess(Object mediaEstrellas) {
                holder.lblMediaEstrellas.setText(mediaEstrellas.toString());
            }
        });
        /* Establece el texto del lblNombreUsuario con el nombre del usuario en la posición
         * actual de listaUsuariosFiltrados
         * Convierte el nombre a mayúsculas utilizando el Locale.ROOT para la configuración
         * regional predeterminada.
         */
        holder.lblNombreUsuario.setText(listaUsuariosFiltrados.get(position).getNombre().toUpperCase(Locale.ROOT));
        /* Establece el texto del lblUbicacion con la ciudad del usuario en la posición actual de listaUsuariosFiltrados.
         * Convierte la ciudad a mayúsculas utilizando el Locale.ROOT para la configuración regional predeterminada.
         */
        holder.lblUbicacion.setText(listaUsuariosFiltrados.get(position).getCiudad().toUpperCase(Locale.ROOT));
        /* Si lblView3 no es nulo, establece su texto con el género del usuario en la posición actual de listaUsuariosFiltrados.
         * Convierte el género a mayúsculas utilizando el Locale.ROOT para la configuración regional predeterminada.
         */
        if (holder.lblView3 != null) {
            holder.lblView3.setText(listaUsuariosFiltrados.get(position).getListaGeneros().toString().toUpperCase(Locale.ROOT));
        }

        /* Verifica si la foto de perfil del usuario en la posición actual de listaUsuariosFiltrados
         * es igual a la URL de la imagen de perfil por defecto.
         */
        if (listaUsuariosFiltrados.get(position).getFotoPerfil().equals(holder.itemView.getContext().getString(R.string.urlImagenPerfilPorDefecto))) {
            /*
            La foto de perfil es igual a la URL de la imagen de perfil por defecto.
             */
            holder.fotoPerfil.setScaleType(ImageView.ScaleType.FIT_CENTER);
            try {
                /*
                 *Carga la imagen de perfil utilizando Glide y la muestra en el ImageView holder.fotoPerfil.
                 */
                Glide.with(holder.itemView.getContext()).load(listaUsuariosFiltrados.get(position).getFotoPerfil()).into(holder.fotoPerfil);
            } catch (Exception e) {
                /*
                 * Manejo de excepciones en caso de error al cargar la imagen.
                 * */
            }
        } else {
            /*
             * La foto de perfil no es igual a la URL de la imagen de perfil por defecto.
             */
            try {
                /*
                 * Ajusta la escala de la foto de perfil para llenar el ImageView holder.fotoPerfil.
                 */
                holder.fotoPerfil.setScaleType(ImageView.ScaleType.FIT_XY);
                /*
                 * Carga la imagen de perfil utilizando Glide y la ajusta al centro dentro del
                 * ImageView holder.fotoPerfil.
                 */
                Glide.with(holder.itemView.getContext()).load(listaUsuariosFiltrados.get(position).getFotoPerfil()).fitCenter().into(holder.fotoPerfil);
            } catch (Exception e) {
                /*
                 * Manejo de excepciones en caso de error al cargar la imagen.
                 */
            }
        }

        /*
         * Define un listener para el botón btnVerPerf. Se ejecutará cuando se haga clic en el botón.
         */
        holder.btnVerPerf.setOnClickListener(v -> {
            /*
             * Crea un Intent para abrir la actividad PerfilUsuario.
             */
            Intent intent = new Intent(v.getContext(), PerfilUsuario.class);
            /*
             * Agrega un extra al Intent con el UidUsuario obtenido del usuario en la posición
             * actual de listaUsuariosFiltrados.
             */
            intent.putExtra("UidUsuario", listaUsuariosFiltrados.get(position).getId());
            /*
             * Inicia la actividad PerfilUsuario utilizando el contexto de la vista actual.
             */
            v.getContext().startActivity(intent);
        });
        /*
         * Define un listener para el botón btnFav. Se ejecutará cuando se haga clic en el botón.
         */
        holder.btnFav.setOnClickListener(v -> {
            /*
             * Crea un objeto TransitionDrawable para realizar una transición entre dos drawables.
             * El primer drawable es el corazón vacío y el segundo drawable es el corazón relleno.
             */
            TransitionDrawable transitionDrawableIda = new TransitionDrawable(new Drawable[]{
                    ContextCompat.getDrawable(v.getContext(), R.drawable.corazon_favoritos_vacio),
                    ContextCompat.getDrawable(v.getContext(), R.drawable.corazon_favoritos_relleno)
            });
            /*
             * Crea otro objeto TransitionDrawable para realizar la transición en sentido inverso.
             * El primer drawable es el corazón relleno y el segundo drawable es el corazón vacío.
             */
            TransitionDrawable transitionDrawableVuelta = new TransitionDrawable(new Drawable[]{
                    ContextCompat.getDrawable(v.getContext(), R.drawable.corazon_favoritos_relleno),
                    ContextCompat.getDrawable(v.getContext(), R.drawable.corazon_favoritos_vacio)

            });
            /*
             * Verifica si el elemento en la posición actual es un favorito.
             */
            if (holder.isFavorite) {
                /*
                 * Configura la transición del drawable en sentido inverso.
                 */
                transitionDrawableVuelta.setCrossFadeEnabled(true);
                transitionDrawableVuelta.startTransition(300);
                /*
                 * Establece el drawable de transición en el botón favorito.
                 */
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.btnFav.setForeground(transitionDrawableVuelta);
                }
                /*
                 * Actualiza la lista de favoritos en Firestore eliminando el ID del usuario actual de la lista.
                 */
                gestorFirebase.borrarValorArray(usuarioActualUid, "listaFavoritos", listaUsuariosFiltrados.get(position).getId(), new GestorFirestore.Callback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        holder.isFavorite = false;
                    }
                });

            } else {
                /*
                 * Configura la transición del drawable en sentido directo.
                 */
                transitionDrawableIda.setCrossFadeEnabled(true);
                transitionDrawableIda.startTransition(300);
                /*
                 * Establece el drawable de transición en el botón favorito.
                 */
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.btnFav.setForeground(transitionDrawableIda);
                }
                /*
                 * Actualiza la lista de favoritos en Firestore agregando el ID del usuario actual a la lista.
                 */
                db.collection(COLECCION).document(usuarioActualUid).update("listaFavoritos", FieldValue.arrayUnion(listaUsuariosFiltrados.get(position).getId())).addOnSuccessListener(documentReference -> {
                    /*
                     * Establece la bandera isFavorite en verdadero para indicar que el usuario es un favorito.
                     */
                    holder.isFavorite = true;
                });
            }

        });


    }

    /**
     * Devuelve el número de elementos en la lista de usuarios.
     *
     * @return cantidad de items en la lista
     */
    @Override
    public int getItemCount() {
        return listaUsuariosFiltrados.size();
    }

    /**
     * Método que filtra la lista de usuarios
     *
     * @param query       cadena de texto para filtrar la lista de usuarios
     * @param progressBar barra de progreso que se mostrará durante el filtrado
     */
    public void filter(String query, ProgressBar progressBar) {
        listaUsuariosFiltrados.clear();
        progressBar.setVisibility(View.VISIBLE);
        if (query.isEmpty()) {
            listaUsuariosFiltrados.addAll(listaUsuarios);
        } else {
            String lowercaseQuery = query.toLowerCase();
            for (Usuario user : listaUsuarios) {
                if (user.getNombre().toLowerCase().contains(lowercaseQuery)
                        || (user.getCiudad() != null && user.getCiudad().toLowerCase().contains(lowercaseQuery))
                        || (user.getListaGeneros() != null && user.getListaGeneros().toString().toLowerCase().contains(lowercaseQuery))) {
                    listaUsuariosFiltrados.add(user);
                }
            }
        }
        notifyDataSetChanged();
        new Handler().postDelayed(() -> progressBar.setVisibility(View.GONE), 900);
    }


}

