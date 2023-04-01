package com.example.tarea1firebase;

import static com.example.tarea1firebase.Registro.COLECCION;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_explora);
        db = FirebaseFirestore.getInstance();



        recyclerViewUsu = findViewById(R.id.recyclerUsuarios);
        recyclerViewUsu.setHasFixedSize(true);
        recyclerViewUsu.setLayoutManager(new LinearLayoutManager(this));


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
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                db.collection(COLECCION)
                        .whereGreaterThanOrEqualTo("nombre", newText)
                        .whereLessThanOrEqualTo("nombre", newText + "\uf8ff")
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                ArrayList<Usuario> listaUsuarios = new ArrayList<>();
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
                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_noticias:
                System.out.println("polla ogrda");
                return true;
            case R.id.menu_explora:
                // Acción para el elemento "Explora"
                return true;
            case R.id.menu_mensajes:
                // Acción para el elemento "Mensajes"
                return true;
            case R.id.menu_perfil:
                // Acción para el elemento "Tu"
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }





}
