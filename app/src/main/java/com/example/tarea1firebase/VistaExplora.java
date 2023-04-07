package com.example.tarea1firebase;

import static com.example.tarea1firebase.Registro.COLECCION;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

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
            listaUsuarios = new ArrayList<>();
            for (DocumentSnapshot documentSnapshot : documentSnapshots.getDocuments()) {
                Usuario user = documentSnapshot.toObject(Usuario.class);
                listaUsuarios.add(user);
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
            public boolean onQueryTextChange(String query) {
                adaptadorUsuariosRecycler.filtrar(query);
                return true;
            }
        });


    }
}
