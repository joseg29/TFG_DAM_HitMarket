package com.example.tarea1firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class ChatsRecientes extends AppCompatActivity {
    private RecyclerView recyclerCanciones;
    private AdaptadorChatsRecientes adaptadorCanciones;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db;
    public final static String COLECCION = "Usuarios";
    private ArrayList<Usuario> usuariosChatsRecientes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats_recientes);
        db = FirebaseFirestore.getInstance();
        usuariosChatsRecientes = new ArrayList<>();

        recyclerCanciones = findViewById(R.id.recyclerChatsRecientes);
        recyclerCanciones.setHasFixedSize(true);

        recyclerCanciones.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<Usuario> arrayPrueba = new ArrayList<>();

        adaptadorCanciones = new AdaptadorChatsRecientes(arrayPrueba);
        recyclerCanciones.setAdapter(adaptadorCanciones);
// Obtén la referencia de la base de datos
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

// Define la referencia a la lista de chats
        DatabaseReference chatsRef = ref.child("chats");

// Define el ID del usuario para el que quieres obtener los chats
        String userId = mAuth.getCurrentUser().getUid();

// Itera sobre los chats para encontrar aquellos en los que participa el usuario
        chatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
                    // Obtiene la referencia del chat actual
                    DatabaseReference chatRef = chatSnapshot.getRef();

                    // Obtiene los IDs de los usuarios en el chat actual
                    String user1 = chatSnapshot.child("usuario1").getValue(String.class);
                    String user2 = chatSnapshot.child("usuario2").getValue(String.class);
                    // Si el usuario en cuestión participa en el chat, muestra los mensajes del chat
                    if (user1.equals(userId)) {
                        db.collection(COLECCION).document(user2).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    Usuario usuario = documentSnapshot.toObject(Usuario.class);
                                    usuariosChatsRecientes.add(usuario);
                                    System.out.println("Usuario 2 ------------" + usuario);
                                    adaptadorCanciones = new AdaptadorChatsRecientes(usuariosChatsRecientes);
                                    recyclerCanciones.setAdapter(adaptadorCanciones);
                                }
                            }
                        });
                    } else if (user2.equals(userId)) {
                        db.collection(COLECCION).document(user1).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    Usuario usuario = documentSnapshot.toObject(Usuario.class);
                                    System.out.println("Usuario 1 ------------" + usuario);
                                    usuariosChatsRecientes.add(usuario);
                                    adaptadorCanciones = new AdaptadorChatsRecientes(usuariosChatsRecientes);
                                    recyclerCanciones.setAdapter(adaptadorCanciones);
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Error al obtener los chats");
            }
        });

    }
}