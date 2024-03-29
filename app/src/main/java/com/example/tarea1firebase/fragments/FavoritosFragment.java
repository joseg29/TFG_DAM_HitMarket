package com.example.tarea1firebase.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.tarea1firebase.adaptadores.AdaptadorUsuariosRecycler;
import com.example.tarea1firebase.R;
import com.example.tarea1firebase.adaptadores.AdaptadorUsuariosFavoritos;
import com.example.tarea1firebase.entidades.Usuario;
import com.example.tarea1firebase.gestor.GestorFirestore;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragmento para mostrar la lista de usuarios favoritos.
 */
public class FavoritosFragment extends Fragment {

    private RecyclerView recyclerViewUsu;
    private AdaptadorUsuariosFavoritos adaptadorUsuariosFavoritos;
    private FirebaseFirestore db;
    private Usuario user;
    private ArrayList<Usuario> listaUsuarios;
    private SearchView barraBusqueda;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private GestorFirestore gestorFirebase;
    private ImageView imgFavsVacios;
    private TextView lblFavsVacios;

    public FavoritosFragment() {
        // Constructor público requerido
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el diseño para este fragmento
        return inflater.inflate(R.layout.explora_fragment_favoritos, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gestorFirebase = new GestorFirestore();

        inicializarVistas(view);
        incializarUsuarioActual();
        setListenerBarraBusqueda();
    }

    /**
     * Establece el listener para la barra de búsqueda.
     */
    private void setListenerBarraBusqueda() {
        barraBusqueda.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adaptadorUsuariosFavoritos.filter(newText, progressBar);
                return false;
            }
        });
    }

    /**
     * Inicializa el usuario actual obteniendo su información de la base de datos.
     */
    private void incializarUsuarioActual() {
        gestorFirebase.obtenerUsuarioPorId(mAuth.getCurrentUser().getUid(), new GestorFirestore.Callback<Usuario>() {
            @Override
            public void onSuccess(Usuario result) {
                user = result;
                List<String> favoritos;
                favoritos = user.getListaFavoritos();
                listaUsuarios = new ArrayList<>();
                for (int i = 0; i < favoritos.size(); i++) {
                    gestorFirebase.obtenerUsuarioPorId(favoritos.get(i), new GestorFirestore.Callback<Usuario>() {
                        @Override
                        public void onSuccess(Usuario result) {
                            Usuario usuario = result;
                            if (favoritos != null) {
                                if (favoritos.contains(usuario.getId())) {
                                    listaUsuarios.add(usuario);
                                }
                                adaptadorUsuariosFavoritos = new AdaptadorUsuariosFavoritos((ArrayList<Usuario>) listaUsuarios);
                                recyclerViewUsu.setAdapter(adaptadorUsuariosFavoritos);

                                if (adaptadorUsuariosFavoritos.getItemCount() > 0) {
                                    imgFavsVacios.setVisibility(View.GONE);
                                    lblFavsVacios.setVisibility(View.GONE);
                                } else {
                                    imgFavsVacios.setVisibility(View.VISIBLE);
                                    lblFavsVacios.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }, Usuario.class);
                }
            }
        }, Usuario.class);
    }

    /**
     * Inicializa las vistas y componentes del fragmento.
     * @param view La vista raíz del fragmento.
     */
    private void inicializarVistas(View view) {
        recyclerViewUsu = view.findViewById(R.id.recyclerUsuarios);
        recyclerViewUsu.setHasFixedSize(true);
        recyclerViewUsu.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAuth = FirebaseAuth.getInstance();

        progressBar = view.findViewById(R.id.spin_kit);
        Sprite doubleBounce = new FadingCircle();
        progressBar.setIndeterminateDrawable(doubleBounce);
        progressBar.setVisibility(View.GONE);

        listaUsuarios = new ArrayList<>();
        adaptadorUsuariosFavoritos = new AdaptadorUsuariosFavoritos((ArrayList<Usuario>) listaUsuarios);
        recyclerViewUsu.setAdapter(adaptadorUsuariosFavoritos);

        barraBusqueda = view.findViewById(R.id.barraBusqueda);

        imgFavsVacios = view.findViewById(R.id.imagenRecyclerVacioFavsVacio);
        lblFavsVacios = view.findViewById(R.id.lblRecyclerVacioFavsVacio);
    }
}
