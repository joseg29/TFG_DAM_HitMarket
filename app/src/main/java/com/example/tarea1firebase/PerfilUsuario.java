package com.example.tarea1firebase;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tarea1firebase.Fragments.GestorFirestore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class PerfilUsuario extends AppCompatActivity {
    private FirebaseFirestore db;
    private int PICK_AUDIO_REQUEST = 123120;
    //Este será el nombre de la colección que daremos en la BBDD de Firebase
    public final static String COLECCION = "Usuarios";
    private StorageReference storageRef;
    private FirebaseStorage storage;
    private ProgressDialog dialogoCargando;
    private RecyclerView recyclerCanciones;
    private AdaptadorCancionesRecycler adaptadorCanciones;
    private TextView lblUsername, lblDescripcion, lblEmail, lblRecyclerVacio;
    private Usuario usuario;
    private ImageButton btnInstagram, btnTiktok, btnYoutube, btnSpotify, btnSoundCloud, btnAnadirCancion;
    private Button btnChat, tvEditar;
    private String uid;
    private FirebaseAuth mAuth;
    private ImageView imgFotoPerfil, imgRecyclerVacio;
    private GestorFirestore gestorFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_usuario);

        inicializarObjetosFirebase();
        inicializarUsuario();
        inicializarVistas();

        esconderBotonesUsuarioPropio();

        setListenerBotones();

        inicializarBotonesRedesSociales();
    }

    public void setListenerBotones() {

    }

    public void inicializarBotonesRedesSociales() {
        btnChat.setOnClickListener(v -> {
            Intent intent = new Intent(PerfilUsuario.this, ChatVentana.class);
            intent.putExtra("UsuarioActual", mAuth.getCurrentUser().getUid());
            intent.putExtra("UidUsuarioReceptor", uid);
            startActivity(intent);
        });

        btnInstagram.setOnClickListener(v -> {
            abrirInstagram();
        });
        btnTiktok.setOnClickListener(v -> {
            abrirTikTok();
        });
        btnYoutube.setOnClickListener(v -> {
            abrirYoutube();
        });

        /**btnSoundCloud.setOnClickListener(v -> {
         abrirSoundCloud();
         });

         btnSpotify.setOnClickListener(v -> {
         abrirSpotify();
         });**/


    }

    public void esconderBotonesUsuarioPropio() {
        if (!uid.equals(mAuth.getCurrentUser().getUid())) {
            btnAnadirCancion.setVisibility(View.GONE);
            tvEditar.setVisibility(View.GONE);
            btnChat.setVisibility(View.VISIBLE);
        }
    }

    public void inicializarObjetosFirebase() {
        gestorFirebase = new GestorFirestore();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        uid = getIntent().getStringExtra("UidUsuario");
        imgFotoPerfil = findViewById(R.id.imgFotoPerfil);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        storageRef = FirebaseStorage.getInstance().getReference();
    }

    public void inicializarVistas() {
        btnChat = findViewById(R.id.btnChat);
        btnInstagram = findViewById(R.id.btnInstagram);
        btnYoutube = findViewById(R.id.btnYoutube);
        btnTiktok = findViewById(R.id.btnTikTok);
        btnSpotify = findViewById(R.id.btnSpotify);
        btnSoundCloud = findViewById(R.id.btnSoundCloud);

        imgRecyclerVacio = findViewById(R.id.imagenRecyclerVacio);
        lblRecyclerVacio = findViewById(R.id.lblRecyclerVacio);

        btnAnadirCancion = findViewById(R.id.btnSubirAudio);

        tvEditar = findViewById(R.id.tvEditar);

        recyclerCanciones = findViewById(R.id.recyclerCanciones);
        recyclerCanciones.setHasFixedSize(true);

        recyclerCanciones.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<String> arrayPrueba = new ArrayList<>();

        adaptadorCanciones = new AdaptadorCancionesRecycler(arrayPrueba);
        recyclerCanciones.setAdapter(adaptadorCanciones);

    }

    public void inicializarUsuario() {
        gestorFirebase.obtenerUsuarioPorId(uid, new GestorFirestore.Callback<Usuario>() {
            @Override
            public void onSuccess(Usuario usuarioDevuelto) {
                usuario = usuarioDevuelto;
                obtenerDatosUsuario();
                setRedesSociales();
            }
        }, Usuario.class);
    }

    //Busca las canciones de un usuario en la base de datos y asigna los audios a los mediaPlayer
    public void obtenerDatosUsuario() {
        dialogoCargando = new ProgressDialog(this);
        dialogoCargando.setTitle("Obteniendo");
        dialogoCargando.setMessage("Obteniendo datos de usuario");
        dialogoCargando.setCancelable(false);
        dialogoCargando.show();

        gestorFirebase.obtenerUsuarioPorId(uid, new GestorFirestore.Callback<Usuario>() {
            @Override
            public void onSuccess(Usuario usuarioDevuelto) {
                usuario = usuarioDevuelto;

                //Canciones que obtenemos de la base de datos.
                List<String> canciones;
                canciones = usuario.getArrayCanciones();

                lblDescripcion = findViewById(R.id.tvDescripcion);
                lblUsername = findViewById(R.id.tvNombre);
                lblEmail = findViewById(R.id.tvCiudad);

                lblUsername.setText(usuario.getNombre());
                lblDescripcion.setText(usuario.getDescripcion());
                lblEmail.setText(usuario.getEmail());

                adaptadorCanciones = new AdaptadorCancionesRecycler(canciones);
                recyclerCanciones.setAdapter(adaptadorCanciones);


                //Establecer foto de perfil
                if (!usuario.getFotoPerfil().equals("")) {
                    try {
                        Glide.with(PerfilUsuario.this).load(usuario.getFotoPerfil()).into(imgFotoPerfil);
                    } catch (Exception e) {
                    }
                }
                if (adaptadorCanciones.getItemCount() > 0) {
                    imgRecyclerVacio.setVisibility(View.GONE);
                    lblRecyclerVacio.setVisibility(View.GONE);
                } else {
                    imgRecyclerVacio.setVisibility(View.VISIBLE);
                    lblRecyclerVacio.setVisibility(View.VISIBLE);
                }
                dialogoCargando.dismiss();
            }
        }, Usuario.class);
    }


    //Método que abre el file explorer
    public void seleccionarAudio(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/mpeg");
        startActivityForResult(intent, PICK_AUDIO_REQUEST);
    }

    public void setRedesSociales() {
        //Revisa si tiene instagram
        if (usuario.getInstagram() != "") {
            //Si tiene, pone el botón para que se pueda clickar y opacidad al 100%
            btnInstagram.setClickable(true);
            btnInstagram.setAlpha(1.0F);
        } else {
            //Lo desactiva y opacidad al 20%
            btnInstagram.setClickable(false);
            btnInstagram.setAlpha(0.2f);
        }

        if (usuario.getYoutube() != "") {
            //Si tiene, pone el botón para que se pueda clickar y opacidad al 100%
            btnYoutube.setClickable(true);
            btnYoutube.setAlpha(1.0F);
        } else {
            //Lo desactiva y opacidad al 20%
            btnYoutube.setClickable(false);
            btnYoutube.setAlpha(0.2f);
        }

        //Exactamente lo mismo con TikTok
        if (usuario.getTiktTok() != "") {
            btnTiktok.setClickable(true);
            btnTiktok.setAlpha(1.0F);
        } else {
            btnTiktok.setClickable(false);
            btnTiktok.setAlpha(0.2f);
        }

        if (usuario.getSpotify() != "") {
            btnSpotify.setClickable(true);
            btnSpotify.setAlpha(1.0F);
        } else {
            btnSpotify.setClickable(false);
            btnSpotify.setAlpha(0.2f);
        }

        if (usuario.getSoundCloud() != "") {
            btnSoundCloud.setClickable(true);
            btnSoundCloud.setAlpha(1.0F);
        } else {
            btnSoundCloud.setClickable(false);
            btnSoundCloud.setAlpha(0.2f);
        }

    }

    public void abrirYoutube() {
        String username = usuario.getYoutube(); // Nombre de usuario del canal
        String channelUrl = "https://www.youtube.com/user/" + username;

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(channelUrl));
        intent.setPackage(null); // Elimina el paquete de la aplicación de YouTube para abrir en el navegador
        startActivity(intent); // Abre el canal en el navegador predeterminado
    }

    private void abrirInstagram() {

        String url = "https://www.instagram.com/" + usuario.getInstagram();

        Intent intent;

        // Verificar si la aplicación de Instagram está instalada
        try {
            // Si la aplicación de Instagram está instalada, abrir la aplicación
            getPackageManager().getPackageInfo("com.instagram.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("instagram://user?username=" + usuario.getInstagram()));
            intent.setPackage("com.instagram.android");
        } catch (PackageManager.NameNotFoundException e) {
            // Si la aplicación de Instagram no está instalada, abrir la página de Instagram en el navegador
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        }

        startActivity(intent);
    }

    public void abrirTikTok() {
        String username = usuario.getTiktTok();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tiktok.com/@" + username));
        intent.setPackage("com.zhiliaoapp.musically");
        if (intent.resolveActivity(getPackageManager()) != null) {
        } else {
            intent.setPackage(null); // Elimina el paquete de la aplicación de TikTok para abrir en el navegador
        }
        startActivity(intent);
    }
}
