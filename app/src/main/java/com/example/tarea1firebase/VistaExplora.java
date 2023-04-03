package com.example.tarea1firebase;

import static com.example.tarea1firebase.Registro.COLECCION;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VistaExplora extends AppCompatActivity {


    private RecyclerView recyclerViewUsu;
    private AdaptadorUsuariosRecycler adaptadorUsuariosRecycler;
    private FirebaseFirestore db;
    private Usuario user;
    private ArrayList<Usuario> listaUsuarios;
    private SearchView barraBusqueda;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_explora);
        db = FirebaseFirestore.getInstance();


        recyclerViewUsu = findViewById(R.id.recyclerUsuarios);
        recyclerViewUsu.setHasFixedSize(true);
        recyclerViewUsu.setLayoutManager(new LinearLayoutManager(this));

        progressBar = findViewById(R.id.spin_kit);
        Sprite doubleBounce = new FadingCircle();
        progressBar.setIndeterminateDrawable(doubleBounce);
        progressBar.setVisibility(View.GONE);

        db.collection(COLECCION).get().addOnSuccessListener(documentSnapshots -> {
            List<Usuario> listaUsuarios = new ArrayList<>();
            for (DocumentSnapshot documentSnapshot : documentSnapshots.getDocuments()) {
                Usuario saludo = documentSnapshot.toObject(Usuario.class);
                listaUsuarios.add(saludo);
            }

            adaptadorUsuariosRecycler = new AdaptadorUsuariosRecycler((ArrayList<Usuario>) listaUsuarios);
            recyclerViewUsu.setAdapter(adaptadorUsuariosRecycler);
        }).addOnFailureListener(e -> {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        });

        barraBusqueda = findViewById(R.id.barraBusqueda);
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
                                    Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
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

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.menu_noticias:
                        System.out.println("polla ogrda");
                        return true;
                    case R.id.menu_explora:
                        System.out.println("polla gogrda");
                        return true;
                    case R.id.menu_mensajes:
                        System.out.println("as ogrda");
                        return true;
                    case R.id.menu_perfil:
                        System.out.println("assaas ogrda");
                        return true;
                    default:
                }
                return onOptionsItemSelected(item);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_navigation, menu);
        return true;
    }

}
