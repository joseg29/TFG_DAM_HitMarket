package com.example.tarea1firebase.fragments;

import static com.example.tarea1firebase.Registro.COLECCION;

import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NoticiasFragment extends Fragment {

    private RecyclerView recyclerViewUsu;
    private AdaptadorUsuariosRecycler adaptadorUsuariosRecycler;
    private FirebaseFirestore db;
    private Usuario user;
    private ArrayList<Usuario> listaUsuarios;
    private SearchView barraBusqueda;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private GestorFirestore gestorFirebase;

    public NoticiasFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
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

        gestorFirebase = new GestorFirestore();

        inicializarVistas(view);
        incializarUsuarioActual();

        setListenerBarraBusqueda();


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
                            //Obtenemos el usuario de la base de datos con todos sus campos
                            if (favoritos != null) {
                                if (favoritos.contains(usuario.getId())) {
                                    listaUsuarios.add(usuario);
                                }
                                adaptadorUsuariosRecycler = new AdaptadorUsuariosRecycler((ArrayList<Usuario>) listaUsuarios);
                                recyclerViewUsu.setAdapter(adaptadorUsuariosRecycler);
                            }
                        }
                    }, Usuario.class);
                }
            }
        }, Usuario.class);
    }

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
        adaptadorUsuariosRecycler = new AdaptadorUsuariosRecycler((ArrayList<Usuario>) listaUsuarios);
        recyclerViewUsu.setAdapter(adaptadorUsuariosRecycler);

        barraBusqueda = view.findViewById(R.id.barraBusqueda);


    }
}

