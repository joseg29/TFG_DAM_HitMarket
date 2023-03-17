package com.example.tarea1firebase;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
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
    private TextView lblUsername, lblDescripcion, lblEmail;
    private Usuario usuario;
    private ImageButton btnInstagram, btnTiktok, btnYoutube, btnSpotify, btnSoundCloud;
    private Button btnCerrarSesion, btnTemporal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_usuario);
//ESTO ES PARA PASAR DE ACTIVITY PARA VER LOS USUARIOS//
        btnTemporal = findViewById(R.id.btnExplorarUsuarios);
        btnTemporal.setOnClickListener(v -> {
            Intent auxIntent = new Intent(PerfilUsuario.this, VistaExplora.class);
            startActivity(auxIntent);
        });
//BTN TEMPORAL FIN//
        usuario = (Usuario) getIntent().getSerializableExtra("USUARIO");

        recyclerCanciones = findViewById(R.id.recyclerCanciones);
        recyclerCanciones.setHasFixedSize(true);

        recyclerCanciones.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<String> arrayPrueba = new ArrayList<>();

        adaptadorCanciones = new AdaptadorCancionesRecycler(arrayPrueba);
        recyclerCanciones.setAdapter(adaptadorCanciones);


        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();


        storageRef = FirebaseStorage.getInstance().getReference();

        btnInstagram = findViewById(R.id.btnInstagram);
        btnYoutube = findViewById(R.id.btnYoutube);
        btnTiktok = findViewById(R.id.btnTikTok);
        btnSpotify = findViewById(R.id.btnSpotify);
        btnSoundCloud = findViewById(R.id.btnSoundCloud);


        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnCerrarSesion.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(PerfilUsuario.this, Login.class);
            startActivity(intent);
            finish();
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

        obtenerDatosUsuario();
        setRedesSociales();
    }


    //Busca las canciones de un usuario en la base de datos y asigna los audios a los mediaPlayer
    public void obtenerDatosUsuario() {
        dialogoCargando = new ProgressDialog(this);
        dialogoCargando.setTitle("Obteniendo");
        dialogoCargando.setMessage("Obteniendo datos de usuario");
        dialogoCargando.setCancelable(false);
        dialogoCargando.show();

        CollectionReference refUsuarios = FirebaseFirestore.getInstance().

                collection(COLECCION);


        refUsuarios.document(usuario.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            //Obtenemos todas las urls del array de canciones del usuario en firebase, y asignamos cada una a un mediaplayer y seekbar distinto.
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //Obtenemos el usuario de la base de datos con todos sus campos
                        Usuario usuario = document.toObject(Usuario.class);

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

                    } else {
                        Toast.makeText(getApplicationContext(), "Error al obtener datos", Toast.LENGTH_LONG);
                    }
                }
                dialogoCargando.dismiss();
            }
        });
    }


    //Método que abre el file explorer
    public void seleccionarAudio(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/mpeg");
        startActivityForResult(intent, PICK_AUDIO_REQUEST);
    }

    //Listener que detecta cuando hemos seleccionado un audio.
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            dialogoCargando = new ProgressDialog(this);
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
                        File cacheDir = getCacheDir();
                        File file = new File(cacheDir, fileName);
                        InputStream inputStream = getContentResolver().openInputStream(uri);
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

                //Carpeta de archivos del storage
                StorageReference storagePath = storageRef.child("audios").child(uri.getLastPathSegment());

                storagePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getApplicationContext(), "Subido correctamente", Toast.LENGTH_SHORT).show();
                        Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                        firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();
                                //Aquí se añaden los audios al array de canciones del usuario en firebase
                                db.collection(COLECCION).document(usuario.getId()).update("arrayCanciones", FieldValue.arrayUnion(url)).addOnSuccessListener(documentReference -> {
                                    Toast.makeText(getApplicationContext(), "Subido correctamente", Toast.LENGTH_SHORT).show();
                                    dialogoCargando.dismiss();
                                    obtenerDatosUsuario();
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                            }
                        });


                    }
                });
            }
        }
    }


    public void setRedesSociales() {
        //Revisa si tiene instagram
        if (usuario.getInstagram() != null) {
            //Si tiene, pone el botón para que se pueda clickar y opacidad al 100%
            btnInstagram.setClickable(true);
            btnInstagram.setAlpha(1.0F);
        } else {
            //Lo desactiva y opacidad al 20%
            btnInstagram.setClickable(false);
            btnInstagram.setAlpha(0.2f);
        }

        //Exactamente lo mismo con TikTok
        if (usuario.getTiktTok() != null) {
            btnTiktok.setClickable(true);
            btnTiktok.setAlpha(1.0F);
        } else {
            btnTiktok.setClickable(false);
            btnTiktok.setAlpha(0.2f);
        }

        if (usuario.getSpotify() != null) {
            btnSpotify.setClickable(true);
            btnSpotify.setAlpha(1.0F);
        } else {
            btnSpotify.setClickable(false);
            btnSpotify.setAlpha(0.2f);
        }

        if (usuario.getSoundCloud() != null) {
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
        String username = "joseg29_";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tiktok.com/@" + username));
        intent.setPackage("com.zhiliaoapp.musically");
        if (intent.resolveActivity(getPackageManager()) != null) {
        } else {
            intent.setPackage(null); // Elimina el paquete de la aplicación de TikTok para abrir en el navegador
        }
        startActivity(intent);
    }
}
