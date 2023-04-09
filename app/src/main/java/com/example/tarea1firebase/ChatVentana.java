package com.example.tarea1firebase;

import static com.example.tarea1firebase.PerfilUsuario.COLECCION;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.units.qual.A;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class ChatVentana extends AppCompatActivity {
    private Button btnEnviarMensaje;
    private EditText etMensaje;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private RecyclerView recyclerMensajes;
    private AdaptadorMensajesChat adaptadorCanciones;
    private DatabaseReference chatRef;
    private String chatKey;
    private ArrayList<Mensaje> listaMensajes;
    private String usuarioActualUid, usuario2Uid;
    private DatabaseReference mensajesRef, fechaUltimoMensajeRef;
    private FirebaseFirestore db;
    private Usuario otroUsuarioReceptor;
    private TextView lblNombreContacto;
    private ImageView fotoPerfil;
    private Chat chat;
    private Usuario usuario1, usuario2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_ventana);
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();


        recyclerMensajes = findViewById(R.id.recyclerMensajesChat);
        recyclerMensajes.setHasFixedSize(true);

        recyclerMensajes.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<Mensaje> arrayPrueba = new ArrayList<>();

        adaptadorCanciones = new AdaptadorMensajesChat(arrayPrueba);
        recyclerMensajes.setAdapter(adaptadorCanciones);

        btnEnviarMensaje = findViewById(R.id.btnEnviarMensaje);
        etMensaje = findViewById(R.id.etMensaje);
        fotoPerfil = findViewById(R.id.fotoPerfilChat);
        lblNombreContacto = findViewById(R.id.lblNombreContacto);

        usuarioActualUid = getIntent().getStringExtra("UsuarioActual");
        usuario2Uid = getIntent().getStringExtra("UidUsuarioReceptor");
        inicializarUsuariosChat();
        inicializarChat();

        mensajesRef = database.getReference("chats").child(chatRef.getKey()).child("mensajes");
        fechaUltimoMensajeRef = database.getReference("chats").child(chatRef.getKey()).child("fechaUltimoMensaje");

        btnEnviarMensaje.setOnClickListener(v -> {
            crearChat(usuarioActualUid, usuario2Uid);
        });
        obtenerMensajes();
    }

    private void inicializarChat() {

        chatRef = database.getReference("chats");

        // Crea una clave única para el chat a partir de los UIDs de los usuarios
        if (usuarioActualUid.compareTo(usuario2Uid) < 0) {
            chatKey = usuarioActualUid + "_" + usuario2Uid;
        } else {
            chatKey = usuario2Uid + "_" + usuarioActualUid;
        }
        chatRef = chatRef.child(chatKey);

        etMensaje.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etMensaje.getText().length() > 0) {
                    btnEnviarMensaje.setEnabled(true);
                    btnEnviarMensaje.setBackgroundColor(getResources().getColor(R.color.purple_200));
                } else {
                    btnEnviarMensaje.setEnabled(false);
                    btnEnviarMensaje.setBackgroundColor(getResources().getColor(R.color.white));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        db.collection(COLECCION).document(usuario2Uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    otroUsuarioReceptor = documentSnapshot.toObject(Usuario.class);
                    lblNombreContacto.setText(otroUsuarioReceptor.getNombre());
                    if (!otroUsuarioReceptor.getFotoPerfil().equals("")) {
                        try {
                            Glide.with(ChatVentana.this).load(otroUsuarioReceptor.getFotoPerfil()).into(fotoPerfil);
                        } catch (Exception e) {
                        }
                    }
                }
            }
        });
    }

    private void crearChat(String usuario1Uid, String usuario2Uid) {
        // Verifica si el chat ya existe antes de crear uno nuevo
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    chat = snapshot.getValue(Chat.class);
                } else {
                    chat = new Chat(new ArrayList<>(), usuario1, usuario2, "", chatKey);
                    chatRef.setValue(chat);

                    db.collection(Registro.COLECCION).document(usuario1Uid).update("chatsRecientes", FieldValue.arrayUnion(chatKey)).addOnSuccessListener(documentReference -> {
                    });
                    db.collection(Registro.COLECCION).document(usuario2Uid).update("chatsRecientes", FieldValue.arrayUnion(chatKey)).addOnSuccessListener(documentReference -> {
                    });
                }
                enviarMensaje(chatRef.getKey(), usuario1Uid, etMensaje.getText().toString());
                etMensaje.setText("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Maneja el error
            }
        });
    }

    private void enviarMensaje(String chatId, String remitente, String texto) {
        //Obtenemos la fecha y hora del mensaje
        Date date = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            date = Date.from(Instant.now());
        }
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        formatter.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
        String strDate = formatter.format(date);

        Mensaje msj = new Mensaje(remitente, texto, strDate);

        chat.añadirMensaje(msj);
        chat.setFechaUltimoMsj(msj.getFechaYHora());

        chatRef.setValue(chat);
    }

    private void obtenerMensajes() {
        FirebaseDatabase.getInstance().getReference("chats").child(chatKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    chat = snapshot.getValue(Chat.class);
                } else {
                    chat = new Chat(new ArrayList<>(), usuario1, usuario2, "", chatKey);
                }
                listaMensajes = new ArrayList<>();

                // Iterar a través de la lista de mensajes en el chat
                for (DataSnapshot mensajeSnapshot : snapshot.child("listaMensajes").getChildren()) {
                    // Obtener los datos del mensaje
                    Mensaje mensaje = mensajeSnapshot.getValue(Mensaje.class);
                    // Agregar el mensaje al ArrayList
                    listaMensajes.add(mensaje);
                }
                adaptadorCanciones = new AdaptadorMensajesChat(listaMensajes);
                recyclerMensajes.setAdapter(adaptadorCanciones);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Maneja el error
            }
        });
    }

    public void inicializarUsuariosChat() {
        db.collection(COLECCION).document(usuarioActualUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    usuario1 = documentSnapshot.toObject(Usuario.class);
                }
            }
        });
        db.collection(COLECCION).document(usuario2Uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    usuario2 = documentSnapshot.toObject(Usuario.class);
                }
            }
        });
    }
}

