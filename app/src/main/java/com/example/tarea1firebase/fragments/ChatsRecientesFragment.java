package com.example.tarea1firebase.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.tarea1firebase.adaptadores.AdaptadorChatsRecientes;
import com.example.tarea1firebase.entidades.Chat;
import com.example.tarea1firebase.R;
import com.example.tarea1firebase.entidades.Usuario;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;
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
    private String userId;
    private List<String> chats;
    private ImageView imgMsgVacios;
    private TextView lblMsgVacios;

    private ProgressBar progressBar;

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

        progressBar = view.findViewById(R.id.spin_kit);
        Sprite doubleBounce = new FadingCircle();
        progressBar.setIndeterminateDrawable(doubleBounce);
        progressBar.setVisibility(View.GONE);

        db = FirebaseFirestore.getInstance();
        listaChatsRecientes = new ArrayList<>();

        recyclerMensajes = view.findViewById(R.id.recyclerChatsRecientes);
        recyclerMensajes.setHasFixedSize(true);

        recyclerMensajes.setLayoutManager(new LinearLayoutManager(getContext()));
        ArrayList<Chat> arrayPrueba = new ArrayList<>();

        adaptadorMensajes = new AdaptadorChatsRecientes(arrayPrueba);
        recyclerMensajes.setAdapter(adaptadorMensajes);

        imgMsgVacios = view.findViewById(R.id.imagenRecyclerMsgVacio);
        lblMsgVacios = view.findViewById(R.id.lblRecyclerVacio2);

        // Obtiene la referencia de la base de datos
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        // Define la referencia a la lista de chats
        DatabaseReference chatsRef = ref.child("chats");

        // Define el ID del usuario para el que quieres obtener los chats
        userId = mAuth.getCurrentUser().getUid();
        obtenerChats();


    }


    // Ordenar la lista por fecha de último mensaje


    /**
     * Método que itera sobre todos los chats recientes del usuario actual para pintarlos en el recycler
     */
    public void obtenerChats() {
        progressBar.setVisibility(View.VISIBLE);

        //Limpia la lista de chats
        listaChatsRecientes = new ArrayList<>();
        listaChatsRecientes.clear();

        //Obtiene
        db.collection(COLECCION).document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                //Obtiene los datos del usuario
                Usuario usuarioActual = document.toObject(Usuario.class);

                //Obtiene la lista de chats
                chats = usuarioActual.getChatsRecientes();
                listaChatsRecientes.clear();

                //Itera sobre todos los chats para pintar cada uno en el recycler view
                for (int i = 0; i < chats.size(); i++) {
                    chatKey = chats.get(i);
                    //Obtiene la referencia a cada chat
                    DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("chats").child(chatKey);

                    chatRef.addValueEventListener(new ValueEventListener() {
                        //Listener que escucha cualquier cambio en la base de datos dentro del chat que hemos especificado
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Chat chat = snapshot.getValue(Chat.class);

                            // Buscamos si ya existe el chat en la lista
                            boolean existeChat = false;
                            for (Chat c : listaChatsRecientes) {
                                if (c.getChatId().equals(chat.getChatId())) {
                                    existeChat = true;
                                    break;
                                }
                            }

                            if (existeChat) {
                                // Si el chat ya existe en la lista, lo reemplazamos
                                for (int i = 0; i < listaChatsRecientes.size(); i++) {
                                    if (listaChatsRecientes.get(i).getChatId().equals(chat.getChatId())) {
                                        listaChatsRecientes.set(i, chat);
                                        break;
                                    }
                                }
                            } else {
                                // Si el chat no existe en la lista, lo añadimos
                                listaChatsRecientes.add(chat);
                            }

                            ordenarChats();

                            //Comprobamos si existen chats recientes
                            if (adaptadorMensajes.getItemCount() > 0) {
                                imgMsgVacios.setVisibility(View.GONE);
                                lblMsgVacios.setVisibility(View.GONE);
                            } else {
                                imgMsgVacios.setVisibility(View.VISIBLE);
                                lblMsgVacios.setVisibility(View.VISIBLE);
                            }
                            progressBar.setVisibility(View.GONE);
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


    /**
     * Método que ordena los chats recientes según la fecha del último mensaje
     */
    public void ordenarChats() {
        Collections.sort(listaChatsRecientes, new Comparator<Chat>() {
            @Override
            public int compare(Chat chat1, Chat chat2) {
                return chat1.getFechaUltimoMsj().compareTo(chat2.getFechaUltimoMsj());
            }
        });
        Collections.reverse(listaChatsRecientes);
        adaptadorMensajes.setData(listaChatsRecientes);
        adaptadorMensajes.notifyDataSetChanged();
    }
}