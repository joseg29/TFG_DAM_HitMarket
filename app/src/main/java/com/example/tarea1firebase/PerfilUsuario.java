package com.example.tarea1firebase;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tarea1firebase.adaptadores.AdaptadorCancionesRecycler;
import com.example.tarea1firebase.adaptadores.AdaptadorGenerosRecycler;
import com.example.tarea1firebase.adaptadores.AdaptadorResenas;
import com.example.tarea1firebase.entidades.Resena;
import com.example.tarea1firebase.entidades.Usuario;
import com.example.tarea1firebase.gestor.GestorFirestore;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class PerfilUsuario extends AppCompatActivity {
    private int PICK_AUDIO_REQUEST = 123120;
    //Este será el nombre de la colección que daremos en la BBDD de Firebase
    public final static String COLECCION = "Usuarios";
    private ProgressBar progressBar;
    private RecyclerView recyclerCanciones, recyclerResenas, recyclerGeneros;
    private AdaptadorCancionesRecycler adaptadorCanciones;
    private AdaptadorResenas adaptadorResenas;
    private AdaptadorGenerosRecycler adaptadorGeneros;
    private TextView lblUsername, lblDescripcion, lblCiudad, lblRecyclerVacio, lblMediaEstrellas, lblNVisitas, lblRecyclerRese, lblGenero;
    private Usuario usuario;
    private ImageButton btnInstagram, btnTiktok, btnYoutube, btnSpotify, btnSoundCloud, btnAnadirCancion;
    private Button tvEditar;
    private ImageButton btnChat;
    private String uidUsuarioMostrandose;
    private FirebaseAuth mAuth;
    private ImageView imgFotoPerfil, imgRecyclerVacio, imgResenasVacias, imgGenero;
    private GestorFirestore gestorFirebase;
    private AlertDialog dialog;
    private String uidUsuarioActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_usuario);

        gestorFirebase = new GestorFirestore();
        mAuth = FirebaseAuth.getInstance();
        uidUsuarioMostrandose = getIntent().getStringExtra("UidUsuario");
        uidUsuarioActual = mAuth.getCurrentUser().getUid();

        inicializarUsuario();

        inicializarVistas();

        esconderBotonesUsuarioPropio();

        setListenerBotones();

        inicializarBotonesRedesSociales();

        inicializarProgressBar();

        anadirVisitaAlPerfil();
    }

    private void anadirVisitaAlPerfil() {
        gestorFirebase.obtenerUsuarioPorId(uidUsuarioMostrandose, new GestorFirestore.Callback<Usuario>() {
            @Override
            public void onSuccess(Usuario usuarioDevuelto) {
                List<String> listaVisitas = usuarioDevuelto.getVisitasAlPerfil();
                if (!listaVisitas.contains(uidUsuarioActual)) {
                    gestorFirebase.anadirVisitaAlPerfil(uidUsuarioMostrandose, uidUsuarioActual);
                    listaVisitas.add(uidUsuarioActual);
                }
                System.out.println(listaVisitas.size());
                System.out.println(listaVisitas);
            }
        }, Usuario.class);
    }


    public void setListenerBotones() {
        lblMediaEstrellas.setOnClickListener(v -> {
            crearDialogoResena();
            dialog.show();
        });
    }

    private void crearDialogoResena() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_resena, null);
        builder.setView(dialogView);

        EditText etTextoResena = dialogView.findViewById(R.id.edit_text_texto);
        RatingBar ratingBar = dialogView.findViewById(R.id.ratingBarResena);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String textoResena = etTextoResena.getText().toString();
                int rating = (int) ratingBar.getRating();
                String fechaActual = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

                Resena resena = new Resena(textoResena, mAuth.getCurrentUser().getUid(), rating, fechaActual);

                List<Resena> listaResenas = usuario.getListaResenas();
                List<String> listaAutores = new ArrayList<>();
                Resena miResenaExistente = null;

                for (int x = 0; x < listaResenas.size(); x++) {
                    listaAutores.add(listaResenas.get(x).getUidAutor());
                    if (listaResenas.get(x).getUidAutor().equals(uidUsuarioActual)) {
                        miResenaExistente = listaResenas.get(x);
                    }
                }

                if (miResenaExistente == null) {
                    gestorFirebase.anadirValorArray(usuario.getId(), "listaResenas", resena, new GestorFirestore.Callback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            obtenerDatosUsuario();
                        }
                    });
                } else {
                    gestorFirebase.actualizarValorArray(usuario.getId(), "listaResenas", miResenaExistente, resena, new GestorFirestore.Callback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            obtenerDatosUsuario();
                        }
                    });
                }

                dialogInterface.dismiss(); // Cierra el diálogo

            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss(); // Cierra el diálogo
            }
        });

        dialog = builder.create();
    }

    public void inicializarBotonesRedesSociales() {
        btnChat.setOnClickListener(v -> {
            Intent intent = new Intent(PerfilUsuario.this, ChatVentana.class);
            intent.putExtra("UsuarioActual", mAuth.getCurrentUser().getUid());
            intent.putExtra("UidUsuarioReceptor", uidUsuarioMostrandose);
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

        btnSpotify.setOnClickListener(v ->

        {
            abrirSpotify();
        });
        btnSoundCloud.setOnClickListener(v ->

        {
            abrirSoundCloud();
        });


    }

    public void esconderBotonesUsuarioPropio() {
        if (!uidUsuarioMostrandose.equals(mAuth.getCurrentUser().getUid())) {
            btnAnadirCancion.setVisibility(View.GONE);
            tvEditar.setVisibility(View.GONE);
            btnChat.setVisibility(View.VISIBLE);
            boolean vieneDeChat = getIntent().getExtras().getBoolean("vieneDeChat");
            if (vieneDeChat) {
                btnChat.setVisibility(View.GONE);
            } else {
                btnChat.setVisibility(View.VISIBLE);
            }
        }
    }

    public void inicializarVistas() {
        lblDescripcion = findViewById(R.id.tvDescripcion);
        lblUsername = findViewById(R.id.tvNombre);
        lblCiudad = findViewById(R.id.tvCiudad);
        imgFotoPerfil = findViewById(R.id.imgFotoPerfil);
        lblNVisitas = findViewById(R.id.lblVisitasAlPerfil);


        btnChat = findViewById(R.id.btnChat);
        btnInstagram = findViewById(R.id.btnInstagram);
        btnYoutube = findViewById(R.id.btnYoutube);
        btnTiktok = findViewById(R.id.btnTikTok);
        btnSpotify = findViewById(R.id.btnSpotify);
        btnSoundCloud = findViewById(R.id.btnSoundCloud);

        imgRecyclerVacio = findViewById(R.id.imagenRecyclerVacioFavsVacio);
        imgResenasVacias = findViewById(R.id.imagenRecyclerVacioRese);
        lblRecyclerVacio = findViewById(R.id.lblRecyclerVacioFavsVacio);
        lblRecyclerRese = findViewById(R.id.lblRecyclerVacioRese);

        btnAnadirCancion = findViewById(R.id.btnSubirAudio);
        lblMediaEstrellas = findViewById(R.id.lblMediaEstrellas);


        tvEditar = findViewById(R.id.tvEditar);

        recyclerCanciones = findViewById(R.id.recyclerCanciones);
        recyclerCanciones.setHasFixedSize(true);

        recyclerCanciones.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<String> arrayPrueba = new ArrayList<>();

        adaptadorCanciones = new AdaptadorCancionesRecycler(arrayPrueba, false);
        recyclerCanciones.setAdapter(adaptadorCanciones);


        recyclerResenas = findViewById(R.id.RecyclerResenas);
        recyclerResenas.setHasFixedSize(true);
        recyclerResenas.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        lblGenero = findViewById(R.id.txtGenero);
        imgGenero = findViewById(R.id.imgGenero);
        recyclerGeneros = findViewById(R.id.RecyclerGeneros);
        recyclerGeneros.setHasFixedSize(true);
        recyclerGeneros.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));


        ArrayList<Resena> arrayResenas = new ArrayList<>();
        adaptadorResenas = new AdaptadorResenas(arrayResenas);
        recyclerResenas.setAdapter(adaptadorResenas);

        progressBar = findViewById(R.id.spin_kit);


    }


    public void inicializarUsuario() {
        gestorFirebase.obtenerUsuarioPorId(uidUsuarioMostrandose, new GestorFirestore.Callback<Usuario>() {
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
        progressBar.setVisibility(View.VISIBLE);
        gestorFirebase.obtenerUsuarioPorId(uidUsuarioMostrandose, new GestorFirestore.Callback<Usuario>() {
            @Override
            public void onSuccess(Usuario usuarioDevuelto) {
                usuario = usuarioDevuelto;

                //Canciones que obtenemos de la base de datos.
                List<String> canciones;
                canciones = usuario.getArrayCanciones();

                List<Resena> resenas;
                resenas = usuario.getListaResenas();

                lblUsername.setText(usuario.getNombre());
                lblDescripcion.setText(usuario.getDescripcion());
                lblCiudad.setText(usuario.getCiudad());

                adaptadorCanciones = new AdaptadorCancionesRecycler(canciones, false);
                recyclerCanciones.setAdapter(adaptadorCanciones);

                adaptadorResenas = new AdaptadorResenas(resenas);
                recyclerResenas.setAdapter(adaptadorResenas);


                List<String> generos = usuario.getListaGeneros();
                adaptadorGeneros = new AdaptadorGenerosRecycler(generos);
                recyclerGeneros.setAdapter(adaptadorGeneros);


                //Establecer foto de perfil
                try {
                    Glide.with(PerfilUsuario.this).load(usuario.getFotoPerfil()).into(imgFotoPerfil);
                } catch (Exception e) {

                }
                if (adaptadorCanciones.getItemCount() > 0) {
                    imgRecyclerVacio.setVisibility(View.GONE);
                    lblRecyclerVacio.setVisibility(View.GONE);
                } else {
                    imgRecyclerVacio.setVisibility(View.VISIBLE);
                    lblRecyclerVacio.setVisibility(View.VISIBLE);
                }
                if (adaptadorResenas.getItemCount() > 0) {
                    imgResenasVacias.setVisibility(View.GONE);
                    lblRecyclerRese.setVisibility(View.GONE);
                } else {
                    imgResenasVacias.setVisibility(View.VISIBLE);
                    lblRecyclerRese.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }
        }, Usuario.class);

        gestorFirebase.obtenerMediaResenas(uidUsuarioMostrandose, new GestorFirestore.Callback() {
            @Override
            public void onSuccess(Object mediaEstrellas) {
                lblMediaEstrellas.setText(mediaEstrellas.toString());
            }
        });

        int nVisitas = usuario.getVisitasAlPerfil().size();
        lblNVisitas.setText(String.valueOf(nVisitas));

    }

    private void inicializarProgressBar() {
        Sprite doubleBounce = new FadingCircle();
        progressBar.setIndeterminateDrawable(doubleBounce);
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

    public void abrirSpotify() {

        String channelUrl = usuario.getSpotify();

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(channelUrl));
        intent.setPackage(null);
        startActivity(intent);
    }

    public void abrirSoundCloud() {

        String channelUrl = usuario.getSoundCloud();

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(channelUrl));
        intent.setPackage(null);
        startActivity(intent);
    }

}
