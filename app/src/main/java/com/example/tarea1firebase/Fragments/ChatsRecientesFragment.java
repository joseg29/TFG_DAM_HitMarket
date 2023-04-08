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
import com.example.tarea1firebase.Chat;
import com.example.tarea1firebase.R;
import com.example.tarea1firebase.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ChatsRecientesFragment extends Fragment {
    private RecyclerView recyclerMensajes;
    private AdaptadorChatsRecientes adaptadorMensajes;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db;
    public final static String COLECCION = "Usuarios";
    private ArrayList<Chat> listaChatsRecientes;
    private String chatKey;
    private Chat chat;

    public ChatsRecientesFragment() {
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
        listaChatsRecientes = new ArrayList<>();

        recyclerMensajes = view.findViewById(R.id.recyclerChatsRecientes);
        recyclerMensajes.setHasFixedSize(true);

        recyclerMensajes.setLayoutManager(new LinearLayoutManager(getContext()));
        ArrayList<Chat> arrayPrueba = new ArrayList<>();

        adaptadorMensajes = new AdaptadorChatsRecientes(arrayPrueba);
        recyclerMensajes.setAdapter(adaptadorMensajes);
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

                listaChatsRecientes = new ArrayList<>();
                for (int i = 0; i < chats.size(); i++) {
                    if (userId.compareTo(chats.get(i)) < 0) {
                        chatKey = userId + "_" + chats.get(i);
                    } else {
                        chatKey = chats.get(i) + "_" + userId;
                    }
                    FirebaseDatabase.getInstance().getReference("chats").child(chats.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            chat = snapshot.getValue(Chat.class);
                            listaChatsRecientes.add(chat);

                            adaptadorMensajes = new AdaptadorChatsRecientes(listaChatsRecientes);
                            recyclerMensajes.setAdapter(adaptadorMensajes);
                            ordenarChats();
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

    // Ordenar la lista por fecha de último mensaje
    public void ordenarChats() {
        Collections.sort(listaChatsRecientes, new Comparator<Chat>() {
            @Override
            public int compare(Chat chat1, Chat chat2) {
                return chat1.getFechaUltimoMsj().compareTo(chat2.getFechaUltimoMsj());
            }
        });
        Collections.reverse(listaChatsRecientes);
        adaptadorMensajes = new AdaptadorChatsRecientes(listaChatsRecientes);
        recyclerMensajes.setAdapter(adaptadorMensajes);
    }
}