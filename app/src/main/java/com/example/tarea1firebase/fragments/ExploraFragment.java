package com.example.tarea1firebase.fragments;

import static com.example.tarea1firebase.Registro.COLECCION;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.tarea1firebase.adaptadores.AdaptadorUsuariosRecycler;
import com.example.tarea1firebase.R;
import com.example.tarea1firebase.entidades.Usuario;
import com.example.tarea1firebase.gestor.GestorFirestore;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ExploraFragment extends Fragment {

    private RecyclerView recyclerViewUsu;
    private AdaptadorUsuariosRecycler adaptadorUsuariosRecycler;
    private Usuario user;
    private ArrayList<Usuario> listaUsuarios;
    private SearchView barraBusqueda;
    private ProgressBar progressBar;
    private GestorFirestore gestorFirebase;

    public ExploraFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_vista_explora, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inicializarVistas(view);
        gestorFirebase = new GestorFirestore();
        setListenerBarraBusqueda();

        gestorFirebase.obtenerTodosLosUsuarios(new GestorFirestore.Callback<ArrayList<Usuario>>() {
            @Override
            public void onSuccess(ArrayList<Usuario> result) {
                adaptadorUsuariosRecycler = new AdaptadorUsuariosRecycler(result);
                recyclerViewUsu.setAdapter(adaptadorUsuariosRecycler);
            }
        });


    }

    private void inicializarVistas(View view) {
        recyclerViewUsu = view.findViewById(R.id.recyclerUsuarios);
        recyclerViewUsu.setHasFixedSize(true);
        recyclerViewUsu.setLayoutManager(new LinearLayoutManager(getActivity()));

        progressBar = view.findViewById(R.id.spin_kit);
        Sprite doubleBounce = new FadingCircle();
        progressBar.setIndeterminateDrawable(doubleBounce);
        progressBar.setVisibility(View.GONE);
        barraBusqueda = view.findViewById(R.id.barraBusqueda);
    }


    private void setListenerBarraBusqueda() {
        barraBusqueda.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adaptadorUsuariosRecycler.filter(newText, progressBar);
                return false;
            }
        });
    }
}

