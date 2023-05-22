package com.example.tarea1firebase.fragments;

import android.content.Intent;
import android.os.Bundle;

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
import com.example.tarea1firebase.entidades.Publicacion;
import com.example.tarea1firebase.entidades.Usuario;
import com.example.tarea1firebase.gestor.GestorFirestore;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

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
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inicializarVistas(view);

        btnNuevaPublicacion.setOnClickListener(v -> {
            Intent i = new Intent(this.getActivity(), ActivityNuevaPublicacion.class);
            startActivity(i);
        });

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_muro, container, false);
    }

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

    private void obtenerPublicacionesDeFavoritos() {
        listaPublicaciones = new ArrayList<>();
        listaUsuariosFavoritos = new ArrayList<>();

        gestorFirestore.obtenerUsuarioPorId(mAuth.getCurrentUser().getUid(), new GestorFirestore.Callback<Usuario>() {
            @Override
            public void onSuccess(Usuario result) {
                usuarioActual = result;

                List<String> favoritos;
                favoritos = usuarioActual.getListaFavoritos();

                System.out.println("Favoritos --- " + favoritos);

                for (int i = 0; i < favoritos.size(); i++) {
                    gestorFirestore.obtenerUsuarioPorId(favoritos.get(i), new GestorFirestore.Callback<Usuario>() {
                        @Override
                        public void onSuccess(Usuario usuarioFavorito) {
                            //Obtenemos el usuario de la base de datos con todos sus campos
                            listaUsuariosFavoritos.add(usuarioFavorito);
                            listaPublicaciones.addAll(usuarioFavorito.getListaPublicaciones());
                            adaptadorPublicaciones = new AdaptadorPublicaciones(listaPublicaciones);
                            recyclerPublicaciones.setAdapter(adaptadorPublicaciones);
                        }
                    }, Usuario.class);
                }

            }
        }, Usuario.class);
    }

}