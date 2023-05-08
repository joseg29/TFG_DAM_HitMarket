package com.example.tarea1firebase.fragments;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tarea1firebase.adaptadores.AdaptadorCancionesRecycler;
import com.example.tarea1firebase.ChatVentana;
import com.example.tarea1firebase.EditarPerfil;
import com.example.tarea1firebase.R;
import com.example.tarea1firebase.adaptadores.AdaptadorResenas;
import com.example.tarea1firebase.entidades.Resena;
import com.example.tarea1firebase.entidades.Usuario;
import com.example.tarea1firebase.gestor.GestorFirestore;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PerfilFragment extends Fragment {
    private int PICK_AUDIO_REQUEST = 123120;
    private ProgressBar progressBar;
    public final static String COLECCION = "Usuarios";
    private StorageReference storageRef;
    private FirebaseStorage storage;
    private RecyclerView recyclerCanciones, recyclerResenas;
    private AdaptadorCancionesRecycler adaptadorCanciones;
    private TextView lblUsername, lblDescripcion, lblCiudad, lblRecyclerVacio, lblMediaEstrellas, lblNVisitas,lblSinRese;
    private Usuario usuario;
    private ImageButton btnInstagram, btnTiktok, btnYoutube, btnSpotify, btnSoundCloud, btnAnadirCancion;
    private Button btnEditar;
    private ImageButton btnChat;

    private String uidUsuarioActual;
    private FirebaseAuth mAuth;
    private ImageView imgFotoPerfil, imgRecyclerVacio,imgRecyclerRese;
    private GestorFirestore gestorFirebase;
    private AdaptadorResenas adaptadorResenas;


    public PerfilFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.perfil_usuario, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        gestorFirebase = new GestorFirestore();
        mAuth = FirebaseAuth.getInstance();
        uidUsuarioActual = mAuth.getCurrentUser().getUid();

        progressBar = view.findViewById(R.id.spin_kit);
        Sprite doubleBounce = new FadingCircle();
        progressBar.setIndeterminateDrawable(doubleBounce);
        progressBar.setVisibility(View.GONE);

        inicializarUsuario();
        inicializarVistas();

        añadirListenerBotones();

        esconderVistasAjenas();

    }

    private void esconderVistasAjenas() {
        if (!uidUsuarioActual.equals(mAuth.getCurrentUser().
                getUid())) {
            btnAnadirCancion.setVisibility(View.GONE);
            btnEditar.setVisibility(View.GONE);

            btnChat.setVisibility(View.VISIBLE);
        }
    }

    private void añadirListenerBotones() {
        btnAnadirCancion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarAudio();
            }
        });

        btnChat.setOnClickListener(v ->
        {
            Intent intent = new Intent(getContext(), ChatVentana.class);
            intent.putExtra("UsuarioActual", mAuth.getCurrentUser().getUid());
            intent.putExtra("UidUsuarioReceptor", uidUsuarioActual);
            startActivity(intent);
        });

        btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EditarPerfil.class);
            gestorFirebase.obtenerUsuarioPorId(uidUsuarioActual, new GestorFirestore.Callback<Usuario>() {
                @Override
                public void onSuccess(Usuario usuarioDevuelto) {
                    usuario = usuarioDevuelto;
                    intent.putExtra("UsuarioAEditar", usuario);
                    intent.putExtra("UidUsuario", mAuth.getCurrentUser().getUid());
                    startActivity(intent);
                    getActivity().finish();
                }
            }, Usuario.class);
        });


        btnInstagram.setOnClickListener(v ->

        {
            abrirInstagram();
        });
        btnTiktok.setOnClickListener(v ->

        {
            abrirTikTok();
        });
        btnYoutube.setOnClickListener(v ->

        {
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


    public void inicializarVistas() {
        lblDescripcion = getView().findViewById(R.id.tvDescripcion);
        lblUsername = getView().findViewById(R.id.tvNombre);
        lblCiudad = getView().findViewById(R.id.tvCiudad);
        btnChat = getView().findViewById(R.id.btnChat);
        lblMediaEstrellas = getView().findViewById(R.id.lblMediaEstrellas);
        btnEditar = getView().findViewById(R.id.tvEditar);
        lblNVisitas = getView().findViewById(R.id.lblVisitasAlPerfil);

        imgFotoPerfil = getView().findViewById(R.id.imgFotoPerfil);

        btnInstagram = getView().findViewById(R.id.btnInstagram);
        btnYoutube = getView().findViewById(R.id.btnYoutube);
        btnTiktok = getView().findViewById(R.id.btnTikTok);
        btnSpotify = getView().findViewById(R.id.btnSpotify);
        btnSoundCloud = getView().findViewById(R.id.btnSoundCloud);


        btnAnadirCancion = getView().findViewById(R.id.btnSubirAudio);
        btnAnadirCancion = getView().findViewById(R.id.btnSubirAudio);

        recyclerCanciones = getView().findViewById(R.id.recyclerCanciones);
        recyclerCanciones.setHasFixedSize(true);

        recyclerCanciones.setLayoutManager(new LinearLayoutManager(getContext()));


        imgRecyclerVacio = getView().findViewById(R.id.imagenRecyclerVacio);
        lblRecyclerVacio = getView().findViewById(R.id.lblRecyclerVacio);

        imgRecyclerRese = getView().findViewById(R.id.imagenRecyclerVacioRese);
        lblSinRese = getView().findViewById(R.id.lblRecyclerVacioRese);

        ArrayList<String> arrayPrueba = new ArrayList<>();

        adaptadorCanciones = new AdaptadorCancionesRecycler(arrayPrueba);
        recyclerCanciones.setAdapter(adaptadorCanciones);

        recyclerResenas = getView().findViewById(R.id.RecyclerResenas);
        recyclerResenas.setHasFixedSize(true);

        recyclerResenas.setLayoutManager(new LinearLayoutManager(getContext()));

        ArrayList<Resena> arrayResenas = new ArrayList<>();
        adaptadorResenas = new AdaptadorResenas(arrayResenas);
        recyclerResenas.setAdapter(adaptadorResenas);


    }

    private void inicializarUsuario() {
        gestorFirebase.obtenerUsuarioPorId(uidUsuarioActual, new GestorFirestore.Callback<Usuario>() {
            @Override
            public void onSuccess(Usuario usuarioDevuelto) {
                usuario = usuarioDevuelto;
                obtenerDatosUsuario();
                setRedesSociales();
            }
        }, Usuario.class);
    }

    @SuppressLint("NonConstantResourceId")
    public void seleccionarAudio() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/mpeg");
        startActivityForResult(intent, PICK_AUDIO_REQUEST);
    }

    public void obtenerDatosUsuario() {
        progressBar.setVisibility(View.VISIBLE);
        gestorFirebase.obtenerUsuarioPorId(uidUsuarioActual, new GestorFirestore.Callback<Usuario>() {
            @Override
            public void onSuccess(Usuario usuarioDevuelto) {
                usuario = usuarioDevuelto;


                List<String> canciones;
                canciones = usuario.getArrayCanciones();

                List<Resena> resenas;
                resenas = usuario.getListaResenas();

                lblUsername.setText(usuario.getNombre());
                lblDescripcion.setText(usuario.getDescripcion());
                lblCiudad.setText(usuario.getCiudad());

                adaptadorCanciones = new AdaptadorCancionesRecycler(canciones);
                recyclerCanciones.setAdapter(adaptadorCanciones);

                adaptadorResenas = new AdaptadorResenas(resenas);
                recyclerResenas.setAdapter(adaptadorResenas);


                    try {
                        Glide.with(PerfilFragment.this).load(usuario.getFotoPerfil()).into(imgFotoPerfil);
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
                    imgRecyclerRese.setVisibility(View.GONE);
                    lblSinRese.setVisibility(View.GONE);
                } else {
                    imgRecyclerRese.setVisibility(View.VISIBLE);
                    lblSinRese.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }
        }, Usuario.class);
        gestorFirebase.obtenerMediaResenas(uidUsuarioActual, new GestorFirestore.Callback() {
            @Override
            public void onSuccess(Object mediaEstrellas) {
                lblMediaEstrellas.setText(mediaEstrellas.toString());
            }
        });

        int nVisitas = usuario.getVisitasAlPerfil().size();
        lblNVisitas.setText(String.valueOf(nVisitas));

    }


    public void setRedesSociales() {
        //Revisa si tiene instagram
        if (usuario.getInstagram() != "") {
            btnInstagram.setClickable(true);
            btnInstagram.setAlpha(1.0F);
        } else {
            btnInstagram.setClickable(false);
            btnInstagram.setAlpha(0.2f);
        }

        if (usuario.getYoutube() != "") {
            btnYoutube.setClickable(true);
            btnYoutube.setAlpha(1.0F);
        } else {
            btnYoutube.setClickable(false);
            btnYoutube.setAlpha(0.2f);
        }

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
        String username = usuario.getYoutube();
        String channelUrl = "https://www.youtube.com/user/" + username;

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(channelUrl));
        intent.setPackage(null);
        startActivity(intent);
    }

    private void abrirInstagram() {

        String url = "https://www.instagram.com/" + usuario.getInstagram();

        Intent intent;

        try {
            getContext().getPackageManager().getPackageInfo("com.instagram.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("instagram://user?username=" + usuario.getInstagram()));
            intent.setPackage("com.instagram.android");
        } catch (PackageManager.NameNotFoundException e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        }

        startActivity(intent);
    }

    public void abrirTikTok() {
        String username = usuario.getTiktTok();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tiktok.com/@" + username));
        intent.setPackage("com.zhiliaoapp.musically");
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
        } else {
            intent.setPackage(null);
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {

            if (requestCode == PICK_AUDIO_REQUEST && resultCode == RESULT_OK) {
                Uri uri = data.getData();
                try {
                    String[] pathSegments = uri.getPath().split("/");
                    if (pathSegments.length > 1) {
                        String fileName = pathSegments[pathSegments.length - 1];
                        File cacheDir = requireActivity().getCacheDir();
                        File file = new File(cacheDir, fileName);
                        InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
                        FileOutputStream outputStream = new FileOutputStream(file);
                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = inputStream.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, len);
                        }
                        inputStream.close();
                        outputStream.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                gestorFirebase.subirAudio(uri, mAuth.getCurrentUser().getUid(), new GestorFirestore.Callback<String>() {
                    @Override
                    public void onSuccess(String url) {
                        Toast.makeText(getContext(), "Subido correctamente", Toast.LENGTH_SHORT).show();
                        obtenerDatosUsuario();
                    }
                });
            }
        }
    }


}

