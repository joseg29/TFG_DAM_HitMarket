package com.example.tarea1firebase.Explora;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.tarea1firebase.AdaptadorCancionesRecycler;
import com.example.tarea1firebase.R;

import java.util.ArrayList;

public class VistaExplora extends AppCompatActivity {
    private RecyclerView recyclerViewUsu;
    private AdaptadorUsuariosRecycler adaptadorUsuariosRecycler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_explora);

        recyclerViewUsu = findViewById(R.id.recyclerUsuarios);
        recyclerViewUsu.setHasFixedSize(true);
        recyclerViewUsu.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<String> arrayPrueba = new ArrayList<>();
        arrayPrueba.add("hola");
        arrayPrueba.add("marico");
        adaptadorUsuariosRecycler = new AdaptadorUsuariosRecycler(arrayPrueba);
        recyclerViewUsu.setAdapter(adaptadorUsuariosRecycler);
    }
}