package com.example.tarea1firebase.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tarea1firebase.AdaptadorChatsRecientes;
import com.example.tarea1firebase.Mensaje;
import com.example.tarea1firebase.R;
import com.example.tarea1firebase.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MensajesFragment extends Fragment {
    private RecyclerView recyclerCanciones;
    private AdaptadorChatsRecientes adaptadorCanciones;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db;
    public final static String COLECCION = "Usuarios";
    private ArrayList<Usuario> usuariosChatsRecientes;
    private String chatKey;

    public MensajesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        return inflater.inflate(R.layout.activity_chats_recientes, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        db = FirebaseFirestore.getInstance();
        usuariosChatsRecientes = new ArrayList<>();

        recyclerCanciones = view.findViewById(R.id.recyclerChatsRecientes);
        recyclerCanciones.setHasFixedSize(true);

        recyclerCanciones.setLayoutManager(new LinearLayoutManager(getContext()));
        ArrayList<Usuario> arrayPrueba = new ArrayList<>();

        adaptadorCanciones = new AdaptadorChatsRecientes(arrayPrueba);
        recyclerCanciones.setAdapter(adaptadorCanciones);
// Obtén la referencia de la base de datos
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

// Define la referencia a la lista de chats
        DatabaseReference chatsRef = ref.child("chats");

// Define el ID del usuario para el que quieres obtener los chats
        String userId = mAuth.getCurrentUser().getUid();
        db.collection(COLECCION).document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                Usuario usuarioActual = document.toObject(Usuario.class);
                List<String> chats;
                chats = usuarioActual.getChatsRecientes();

                usuariosChatsRecientes = new ArrayList<>();
                for (int i = 0; i < chats.size(); i++) {
                    if (userId.compareTo(chats.get(i)) < 0) {
                        chatKey = userId + "_" + chats.get(i);
                    } else {
                        chatKey = chats.get(i) + "_" + userId;
                    }
                    FirebaseDatabase.getInstance().getReference("chats").child(chats.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String usuario1 = snapshot.child("usuario1").getValue(String.class);
                            String usuario2 = snapshot.child("usuario2").getValue(String.class);

                            System.out.println(usuario1 + " - " + usuario2);
                            if (usuario1.equals(userId)) {
                                db.collection(COLECCION).document(usuario2).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()) {
                                            Usuario usuario = documentSnapshot.toObject(Usuario.class);
                                            usuariosChatsRecientes.add(usuario);
                                            adaptadorCanciones = new AdaptadorChatsRecientes(usuariosChatsRecientes);
                                            recyclerCanciones.setAdapter(adaptadorCanciones);
                                        }
                                    }
                                });
                            } else if (usuario2.equals(userId)) {
                                db.collection(COLECCION).document(usuario1).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()) {
                                            Usuario usuario = documentSnapshot.toObject(Usuario.class);
                                            usuariosChatsRecientes.add(usuario);
                                            adaptadorCanciones = new AdaptadorChatsRecientes(usuariosChatsRecientes);
                                            recyclerCanciones.setAdapter(adaptadorCanciones);
                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Manejar errores
                        }
                    });

                }
            }
        });
    }
}