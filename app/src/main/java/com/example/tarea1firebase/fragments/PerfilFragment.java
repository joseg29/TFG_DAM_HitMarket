package com.example.tarea1firebase.fragments;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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
    //Este será el nombre de la colección que daremos en la BBDD de Firebase
    public final static String COLECCION = "Usuarios";
    private StorageReference storageRef;
    private FirebaseStorage storage;
    private ProgressDialog dialogoCargando;
    private RecyclerView recyclerCanciones, recyclerResenas;
    private AdaptadorCancionesRecycler adaptadorCanciones;
    private TextView lblUsername, lblDescripcion, lblEmail, lblRecyclerVacio;
    private Usuario usuario;
    private ImageButton btnInstagram, btnTiktok, btnYoutube, btnSpotify, btnSoundCloud, btnAnadirCancion;
    private Button btnChat, btnEditar;
    private String uid;
    private FirebaseAuth mAuth;
    private ImageView imgFotoPerfil, imgRecyclerVacio;
    private GestorFirestore gestorFirebase;
    private AdaptadorResenas adaptadorResenas;


    public PerfilFragment() {
        // Required empty public constructor
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

        /**gestorFirebase.imprimir(new GestorFirestore.Callback<String>() {
        @Override public void onSuccess(String result) {
        //Hacer algo con el resultado
        }
        });
         */
        gestorFirebase = new GestorFirestore();
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        inicializarUsuario();
        inicializarVistas();

        añadirListenerBotones();

        esconderVistasAjenas();

    }

    private void esconderVistasAjenas() {
        if (!uid.equals(mAuth.getCurrentUser().
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
            intent.putExtra("UidUsuarioReceptor", uid);
            startActivity(intent);
        });

        btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EditarPerfil.class);
            gestorFirebase.obtenerUsuarioPorId(uid, new GestorFirestore.Callback<Usuario>() {
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

        /**btnSoundCloud.setOnClickListener(v -> {
         abrirSoundCloud();
         });

         btnSpotify.setOnClickListener(v -> {
         abrirSpotify();
         });**/
    }


    public void inicializarVistas() {
        lblDescripcion = getView().findViewById(R.id.tvDescripcion);
        lblUsername = getView().findViewById(R.id.tvNombre);
        lblEmail = getView().findViewById(R.id.tvCiudad);
        btnChat = getView().findViewById(R.id.btnChat);

        btnEditar = getView().findViewById(R.id.tvEditar);

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
        gestorFirebase.obtenerUsuarioPorId(uid, new GestorFirestore.Callback<Usuario>() {
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

    //Busca las canciones de un usuario en la base de datos y asigna los audios a los mediaPlayer
    public void obtenerDatosUsuario() {
        dialogoCargando = new ProgressDialog(getActivity());
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

                List<Resena> resenas;
                resenas = usuario.getListaResenas();

                lblUsername.setText(usuario.getNombre());
                lblDescripcion.setText(usuario.getDescripcion());
                lblEmail.setText(usuario.getEmail());

                adaptadorCanciones = new AdaptadorCancionesRecycler(canciones);
                recyclerCanciones.setAdapter(adaptadorCanciones);

                adaptadorResenas = new AdaptadorResenas(resenas);
                recyclerResenas.setAdapter(adaptadorResenas);


                //Establecer foto de perfil
                if (!usuario.getFotoPerfil().equals("")) {
                    try {
                        Glide.with(PerfilFragment.this).load(usuario.getFotoPerfil()).into(imgFotoPerfil);
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


    //Listener que detecta cuando hemos seleccionado un audio.


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
            getContext().getPackageManager().getPackageInfo("com.instagram.android", 0);
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
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
        } else {
            intent.setPackage(null); // Elimina el paquete de la aplicación de TikTok para abrir en el navegador
        }
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            dialogoCargando = new ProgressDialog(requireContext());
            dialogoCargando.setTitle("Subiendo...");
            dialogoCargando.setMessage("Subiendo canción a base de datos");
            dialogoCargando.setCancelable(false);
            dialogoCargando.show();

            if (requestCode == PICK_AUDIO_REQUEST && resultCode == RESULT_OK) {
                Uri uri = data.getData();
                //Todo esto es simplemente para obtener la ruta local del archivo para que firebase la pueda descargar (Código no importante)
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
                        dialogoCargando.dismiss();
                        obtenerDatosUsuario();
                    }
                });
            }
        }
    }


}

