package com.example.tarea1firebase.Fragments;

import static com.example.tarea1firebase.Registro.COLECCION;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.tarea1firebase.AdaptadorUsuariosRecycler;
import com.example.tarea1firebase.R;
import com.example.tarea1firebase.Usuario;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.android.gms.tasks.OnCompleteListener;
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

        db.collection(COLECCION).get().addOnSuccessListener(documentSnapshots -> {
            listaUsuarios = new ArrayList<>();
            System.out.println("paso 1");
            System.out.println(listaUsuarios);
            for (DocumentSnapshot documentSnapshot : documentSnapshots.getDocuments()) {
                Usuario usuario = documentSnapshot.toObject(Usuario.class);
                db.collection(COLECCION).document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();

                        //Obtenemos el usuario de la base de datos con todos sus campos
                        Usuario usuarioActual = document.toObject(Usuario.class);
                        List<String> favoritos;
                        favoritos = usuarioActual.getListaFavoritos();
                        System.out.println(usuarioActual.getId());
                        if (favoritos != null) {
                            for (int i = 0; i < favoritos.size(); i++) {
                                if (favoritos.contains(usuario.getId())) {
                                    System.out.println(usuario.getNombre());
                                    listaUsuarios.add(usuario);
                                }
                            }
                        }
                        adaptadorUsuariosRecycler = new AdaptadorUsuariosRecycler((ArrayList<Usuario>) listaUsuarios);
                        recyclerViewUsu.setAdapter(adaptadorUsuariosRecycler);
                    }
                });
            }
        });


        barraBusqueda = view.findViewById(R.id.barraBusqueda);
        barraBusqueda.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            ArrayList<Usuario> listaUsuarios = new ArrayList<>();

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                listaUsuarios.clear();
                String[] palabras = newText.toLowerCase().split(" ");

                if (palabras.length == 1) {
                    progressBar.setVisibility(View.VISIBLE);
                    // Búsqueda de una sola palabra
                    db.collection(COLECCION)
                            .whereGreaterThanOrEqualTo("nombre", palabras[0])
                            .whereLessThanOrEqualTo("nombre", palabras[0] + "\uf8ff")
                            .get()
                            .addOnCompleteListener(task -> {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                        Usuario usuario = documentSnapshot.toObject(Usuario.class);
                                        listaUsuarios.add(usuario);
                                    }
                                    adaptadorUsuariosRecycler = new AdaptadorUsuariosRecycler(listaUsuarios);
                                    recyclerViewUsu.setAdapter(adaptadorUsuariosRecycler);
                                } else {
                                    Toast.makeText(getActivity(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else if (palabras.length >= 2) {
                    progressBar.setVisibility(View.VISIBLE);
                    // Búsqueda de dos o más palabras
                    db.collection(COLECCION)
                            .whereGreaterThanOrEqualTo("nombre", palabras[0])
                            .whereLessThanOrEqualTo("nombre", palabras[0] + "\uf8ff")
                            .whereGreaterThanOrEqualTo("nombre", palabras[1])
                            .whereLessThanOrEqualTo("nombre", palabras[1] + "\uf8ff")
                            .get()
                            .addOnCompleteListener(task -> {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                        Usuario usuario = documentSnapshot.toObject(Usuario.class);
                                        listaUsuarios.add(usuario);
                                    }
                                    adaptadorUsuariosRecycler = new AdaptadorUsuariosRecycler(listaUsuarios);
                                    recyclerViewUsu.setAdapter(adaptadorUsuariosRecycler);
                                } else {
                                    Toast.makeText(getActivity(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    progressBar.setVisibility(View.GONE);
                    // No hay palabras en la búsqueda, no se hace nada
                    adaptadorUsuariosRecycler = new AdaptadorUsuariosRecycler(listaUsuarios);
                    recyclerViewUsu.setAdapter(adaptadorUsuariosRecycler);
                }
                return false;
            }
        });


    }
}

