package com.example.tarea1firebase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
    private Usuario otroUsuarioReceptor;
    private TextView lblNombreContacto;
    private ImageView fotoPerfil;
    private ImageButton btnVolverAtras;
    private Chat chat;
    private GestorFirestore gestorFirebase;
    private RelativeLayout layoutFotoYNombre;

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
        DatabaseReference mensajesRef = chatsRef.child(chatsRef.getKey()).child("mensajes");
        DatabaseReference fechaUltimoMensajeRef = chatsRef.child(chatsRef.getKey()).child("fechaUltimoMensaje");
    }

    private void inicializarListenerBotones() {
        layoutFotoYNombre.setOnClickListener(v -> {
            Intent intent = new Intent(this, PerfilUsuario.class);
            intent.putExtra("UidUsuario", usuario2Uid);
            intent.putExtra("vieneDeChat", true);
            startActivity(intent);
        });
        btnEnviarMensaje.setOnClickListener(v -> {
            crearChat(usuarioActualUid, usuario2Uid);
        });

        btnVolverAtras.setOnClickListener(v -> {
            finish();
        });
    }

    private void inicializarVistas() {
        layoutFotoYNombre = findViewById(R.id.relativeLayoutChatVentana);
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
        btnVolverAtras = findViewById(R.id.btnVolverChat);
    }

    /**
     * Crea un chat entre dos usuarios en firebase realtime database en caso de que no exista, u obtiene el chat existente.
     */
    private void inicializarChat() {
        /**
         Crea una clave única para el chat a partir de los UIDs de los usuarios. Ordena los id de ambos usuarios alfabeticamente para que siempre la clave sea la misma.
         */
        if (usuarioActualUid.compareTo(usuario2Uid) < 0) {
            idChat = usuarioActualUid + "_" + usuario2Uid;
        } else {
            idChat = usuario2Uid + "_" + usuarioActualUid;
        }

        /**
         * Busca el chat en la base de datos
         */
        chatsRef = chatsRef.child(idChat);

        /**
         * Método que se llama cada vez que se escribe una letra, para verificar si el mensaje no está vacío
         */
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
                /**Establece la foto de perfil y el nombre del otro usuario*/
                lblNombreContacto.setText(otroUsuarioReceptor.getNombre());
                try {
                    Glide.with(ChatVentana.this).load(otroUsuarioReceptor.getFotoPerfil()).into(fotoPerfil);
                } catch (Exception e) {
                }

            }
        }, Usuario.class);
    }


    /**
     * Crea el chat si no ha encontrado uno existente con la clave compuesta de id de usuarios.
     *
     * @param usuario1Uid id de usuario1
     * @param usuario2Uid id de usuario 2
     */
    private void crearChat(String usuario1Uid, String usuario2Uid) {
        // Verifica si el chat ya existe antes de crear uno nuevo
        chatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //Existe
                    chat = snapshot.getValue(Chat.class);
                } else {
                    //No existe
                    chat = new Chat(new ArrayList<>(), usuario1Uid, usuario2Uid, "", idChat);
                    chatsRef.setValue(chat);

                    /**
                     * Añade el nuevo chat al array de chats recientes de ambos usuarios
                     */
                    gestorFirebase.anadirValorArray(usuario1Uid, "chatsRecientes", idChat, new GestorFirestore.Callback<String>() {
                        @Override
                        public void onSuccess(String result) {

                        }
                    });
                    gestorFirebase.anadirValorArray(usuario2Uid, "chatsRecientes", idChat, new GestorFirestore.Callback<String>() {
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

    /**
     * Envía un mensaje al otro usuario, y lo sube a la base de datos de tiempo real.
     *
     * @param chatId    clave compuesta del chat
     * @param remitente usuario que envía el mensaje
     * @param texto     cuerpo del mensaje
     */
    private void enviarMensaje(String chatId, String remitente, String texto) {
        //Obtenemos la fecha y hora del mensaje
        Date date = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            date = Date.from(Instant.now());
        }
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM HH:mm");
        formatter.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
        String strDate = formatter.format(date);

        //Creamos el objeto Mensaje
        Mensaje msj = new Mensaje(remitente, texto, strDate);


        //Añadimos el mensaje al objeto de chat que lo contiene
        chat.anadirMensaje(msj);
        //Seteamos la fecha del último mensaje enviado en el chat, con la fecha del mensaje recién enviado
        chat.setFechaUltimoMsj(msj.getFechaYHora());

        //Hacemos el push a la base de datos
        chatsRef.setValue(chat);
    }

    /**
     * Obtiene todos los mensajes existentes del chat y los añade al recyclerview
     */
    private void obtenerMensajes() {
        FirebaseDatabase.getInstance().getReference("chats").child(idChat).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //Ya existe el chat, por lo que lo obtiene
                    chat = snapshot.getValue(Chat.class);
                } else {
                    //No existe el chat, por lo que lo crea
                    chat = new Chat(new ArrayList<>(), usuarioActualUid, usuario2Uid, "", idChat);
                }
                listaMensajes = new ArrayList<>();

                // Iterar a través de la lista de mensajes en el chat
                for (DataSnapshot mensajeSnapshot : snapshot.child("listaMensajes").getChildren()) {
                    // Obtener los datos del mensaje
                    Mensaje mensaje = mensajeSnapshot.getValue(Mensaje.class);
                    // Agregar el mensaje al ArrayList para luego pasárselo al adaptador
                    listaMensajes.add(mensaje);
                }
                adaptadorCanciones = new AdaptadorMensajesChat(listaMensajes);
                recyclerMensajes.setAdapter(adaptadorCanciones);
                if (recyclerMensajes.getAdapter().getItemCount() > 0) {
                    recyclerMensajes.smoothScrollToPosition(recyclerMensajes.getAdapter().getItemCount() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Maneja el error
            }
        });
    }

    /**
     * Obtiene los datos de ambos usuarios e inicializa los objetos
     */
    public void inicializarUsuariosChat() {
        gestorFirebase.obtenerUsuarioPorId(usuarioActualUid, new GestorFirestore.Callback<Usuario>() {
            @Override
            public void onSuccess(Usuario result) {
            }
        }, Usuario.class);

        gestorFirebase.obtenerUsuarioPorId(usuario2Uid, new GestorFirestore.Callback<Usuario>() {
            @Override
            public void onSuccess(Usuario result) {
            }
        }, Usuario.class);

    }
}

