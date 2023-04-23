package com.example.tarea1firebase;

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
import com.example.tarea1firebase.adaptadores.AdaptadorMensajesChat;
import com.example.tarea1firebase.entidades.Chat;
import com.example.tarea1firebase.entidades.Mensaje;
import com.example.tarea1firebase.entidades.Usuario;
import com.example.tarea1firebase.gestor.GestorFirestore;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class ChatVentana extends AppCompatActivity {
    private Button btnEnviarMensaje;
    private EditText etMensaje;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private RecyclerView recyclerMensajes;
    private AdaptadorMensajesChat adaptadorCanciones;
    private DatabaseReference chatsRef;
    private String idChat;
    private ArrayList<Mensaje> listaMensajes;
    private String usuarioActualUid, usuario2Uid;
    private DatabaseReference mensajesRef, fechaUltimoMensajeRef;
    private Usuario otroUsuarioReceptor;
    private TextView lblNombreContacto;
    private ImageView fotoPerfil;
    private Chat chat;
    private Usuario usuario1, usuario2;
    private GestorFirestore gestorFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_ventana);
        gestorFirebase = new GestorFirestore();

        inicializarVistas();
        inicializarReferenciasYFirebase();

        inicializarListenerBotones();

        inicializarUsuariosChat();
        inicializarChat();


        obtenerMensajes();
    }

    private void inicializarReferenciasYFirebase() {
        usuarioActualUid = getIntent().getStringExtra("UsuarioActual");
        usuario2Uid = getIntent().getStringExtra("UidUsuarioReceptor");
        chatsRef = database.getReference("chats");
        mensajesRef = chatsRef.child(chatsRef.getKey()).child("mensajes");
        fechaUltimoMensajeRef = chatsRef.child(chatsRef.getKey()).child("fechaUltimoMensaje");
    }

    private void inicializarListenerBotones() {
        btnEnviarMensaje.setOnClickListener(v -> {
            crearChat(usuarioActualUid, usuario2Uid);
        });
    }

    private void inicializarVistas() {
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
    }

    private void inicializarChat() {
        // Crea una clave única para el chat a partir de los UIDs de los usuarios
        if (usuarioActualUid.compareTo(usuario2Uid) < 0) {
            idChat = usuarioActualUid + "_" + usuario2Uid;
        } else {
            idChat = usuario2Uid + "_" + usuarioActualUid;
        }
        chatsRef = chatsRef.child(idChat);

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

        gestorFirebase.obtenerUsuarioPorId(usuario2Uid, new GestorFirestore.Callback<Usuario>() {
            @Override
            public void onSuccess(Usuario result) {
                otroUsuarioReceptor = result;
                lblNombreContacto.setText(otroUsuarioReceptor.getNombre());
                if (!otroUsuarioReceptor.getFotoPerfil().equals("")) {
                    try {
                        Glide.with(ChatVentana.this).load(otroUsuarioReceptor.getFotoPerfil()).into(fotoPerfil);
                    } catch (Exception e) {
                    }
                }
            }
        }, Usuario.class);
    }


    private void crearChat(String usuario1Uid, String usuario2Uid) {
        // Verifica si el chat ya existe antes de crear uno nuevo
        chatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    chat = snapshot.getValue(Chat.class);
                } else {
                    chat = new Chat(new ArrayList<>(), usuario1, usuario2, "", idChat);
                    chatsRef.setValue(chat);

                    gestorFirebase.actualiazarCampoUsuario(usuario1Uid, "chatsRecientes", idChat, new GestorFirestore.Callback<String>() {
                        @Override
                        public void onSuccess(String result) {

                        }
                    });
                    gestorFirebase.actualiazarCampoUsuario(usuario2Uid, "chatsRecientes", idChat, new GestorFirestore.Callback<String>() {
                        @Override
                        public void onSuccess(String result) {

                        }
                    });
                }
                enviarMensaje(chatsRef.getKey(), usuario1Uid, etMensaje.getText().toString());
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

        chatsRef.setValue(chat);
    }

    private void obtenerMensajes() {
        FirebaseDatabase.getInstance().getReference("chats").child(idChat).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    chat = snapshot.getValue(Chat.class);
                } else {
                    chat = new Chat(new ArrayList<>(), usuario1, usuario2, "", idChat);
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
        gestorFirebase.obtenerUsuarioPorId(usuarioActualUid, new GestorFirestore.Callback<Usuario>() {
            @Override
            public void onSuccess(Usuario result) {
                usuario1 = result;
            }
        }, Usuario.class);

        gestorFirebase.obtenerUsuarioPorId(usuario2Uid, new GestorFirestore.Callback<Usuario>() {
            @Override
            public void onSuccess(Usuario result) {
                usuario2 = result;
            }
        }, Usuario.class);

    }
}

