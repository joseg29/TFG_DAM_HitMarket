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

import java.text.SimpleDateFormat;
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
    private DatabaseReference mensajesRef;
    private FirebaseFirestore db;
    private Usuario otroUsuarioReceptor;
    private TextView lblNombreContacto;
    private ImageView fotoPerfil;

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
        inicializarChat();

        mensajesRef = database.getReference("chats").child(chatRef.getKey()).child("mensajes");

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
                if (!snapshot.exists()) {
                    // Crea un mapa con la información del chat
                    Map<String, Object> chatInfo = new HashMap<>();
                    chatInfo.put("usuario1", usuario1Uid);
                    chatInfo.put("usuario2", usuario2Uid);

                    // Crea la entrada en la base de datos para el chat
                    chatRef.setValue(chatInfo);
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
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        formatter.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
        String strDate = formatter.format(date);

        // Crea un mapa con la información del mensaje
        Mensaje msj = new Mensaje(remitente, texto, strDate);
        // Crea la entrada en la base de datos para el mensaje
        mensajesRef.push().setValue(msj);
    }

    private void obtenerMensajes() {
        FirebaseDatabase.getInstance().getReference("chats").child(chatKey).child("mensajes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaMensajes = new ArrayList<>();
                // Itera sobre los hijos de la referencia a los mensajes
                for (DataSnapshot mensajeSnapshot : snapshot.getChildren()) {
                    // Obtiene los datos del mensaje y haz lo que necesites con ellos
                    String mensaje = mensajeSnapshot.child("texto").getValue(String.class);
                    String remitenteUid = mensajeSnapshot.child("remitente").getValue(String.class);
                    String timestamp = mensajeSnapshot.child("fechaYHora").getValue(String.class);
                    Mensaje msj = new Mensaje(remitenteUid, mensaje, timestamp);
                    listaMensajes.add(msj);
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
}

