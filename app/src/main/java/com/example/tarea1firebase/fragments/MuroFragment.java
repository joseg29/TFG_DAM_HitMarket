package com.example.tarea1firebase.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.tarea1firebase.ActivityNuevaPublicacion;
import com.example.tarea1firebase.R;
import com.example.tarea1firebase.adaptadores.AdaptadorPublicaciones;
import com.example.tarea1firebase.entidades.Chat;
import com.example.tarea1firebase.entidades.Publicacion;
import com.example.tarea1firebase.entidades.Usuario;
import com.example.tarea1firebase.gestor.GestorFirestore;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Fragmento para mostrar el muro de publicaciones.
 */
public class MuroFragment extends Fragment {
    private RecyclerView recyclerPublicaciones;
    private AdaptadorPublicaciones adaptadorPublicaciones;
    private ArrayList<Publicacion> listaPublicaciones;
    private ImageButton btnNuevaPublicacion;
    private GestorFirestore gestorFirestore;
    private FirebaseAuth mAuth;
    private Usuario usuarioActual;
    private List<Usuario> listaUsuariosFavoritos;

    public MuroFragment() {
        // Constructor público requerido
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inicializarVistas(view);

        btnNuevaPublicacion.setOnClickListener(v -> {
            Intent i = new Intent(this.getActivity(), ActivityNuevaPublicacion.class);
            activityResultNuevaPublicacion.launch(i);
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflar el diseño para este fragmento
        return inflater.inflate(R.layout.fragment_muro, container, false);
    }

    /**
     * Inicializa las vistas y componentes del fragmento.
     *
     * @param view La vista raíz del fragmento.
     */
    private void inicializarVistas(View view) {
        gestorFirestore = new GestorFirestore();
        mAuth = FirebaseAuth.getInstance();

        btnNuevaPublicacion = view.findViewById(R.id.btnNuevaPublicacion);
        recyclerPublicaciones = view.findViewById(R.id.recycler_publicaciones);

        recyclerPublicaciones.setHasFixedSize(true);
        recyclerPublicaciones.setLayoutManager(new LinearLayoutManager(getActivity()));

        listaPublicaciones = new ArrayList<>();

        obtenerPublicacionesDeFavoritos();

        adaptadorPublicaciones = new AdaptadorPublicaciones(listaPublicaciones);
        recyclerPublicaciones.setAdapter(adaptadorPublicaciones);
    }

    /**
     * Obtiene las publicaciones de los usuarios favoritos del usuario actual.
     */
    private void obtenerPublicacionesDeFavoritos() {
        listaPublicaciones = new ArrayList<>();
        listaUsuariosFavoritos = new ArrayList<>();

        gestorFirestore.obtenerUsuarioPorId(mAuth.getCurrentUser().getUid(), new GestorFirestore.Callback<Usuario>() {
            @Override
            public void onSuccess(Usuario result) {
                listaPublicaciones.addAll(result.getListaPublicaciones());
                ordenarPublicaciones();
            }
        }, Usuario.class);

        gestorFirestore.obtenerUsuarioPorId(mAuth.getCurrentUser().getUid(), new GestorFirestore.Callback<Usuario>() {
            @Override
            public void onSuccess(Usuario result) {
                usuarioActual = result;

                List<String> favoritos;
                favoritos = usuarioActual.getListaFavoritos();
                for (int i = 0; i < favoritos.size(); i++) {
                    gestorFirestore.obtenerUsuarioPorId(favoritos.get(i), new GestorFirestore.Callback<Usuario>() {
                        @Override
                        public void onSuccess(Usuario usuarioFavorito) {
                            listaUsuariosFavoritos.add(usuarioFavorito);
                            listaPublicaciones.addAll(usuarioFavorito.getListaPublicaciones());
                            adaptadorPublicaciones = new AdaptadorPublicaciones(listaPublicaciones);
                            recyclerPublicaciones.setAdapter(adaptadorPublicaciones);
                            ordenarPublicaciones();
                        }
                    }, Usuario.class);
                }
            }
        }, Usuario.class);
    }

    ActivityResultLauncher<Intent> activityResultNuevaPublicacion = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                /**
                 * Se ha publicado algo nuevo
                 */
                if (result.getResultCode() == 1) {
                    obtenerPublicacionesDeFavoritos();
                }
            });

    /**
     * Método que ordena los chats recientes según la fecha del último mensaje
     */
    public void ordenarPublicaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Collections.sort(listaPublicaciones, Comparator.comparing(Publicacion::getFecha));
        }
        Collections.reverse(listaPublicaciones);
        adaptadorPublicaciones = new AdaptadorPublicaciones(listaPublicaciones);
        recyclerPublicaciones.setAdapter(adaptadorPublicaciones);
    }
}
