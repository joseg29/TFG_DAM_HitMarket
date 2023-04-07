package com.example.tarea1firebase.Fragments;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.example.tarea1firebase.AdaptadorCancionesRecycler;
import com.example.tarea1firebase.ChatVentana;
import com.example.tarea1firebase.ChatsRecientes;
import com.example.tarea1firebase.EditarPerfil;
import com.example.tarea1firebase.Login;
import com.example.tarea1firebase.PerfilUsuario;
import com.example.tarea1firebase.R;
import com.example.tarea1firebase.Usuario;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

public class PerfilFragment extends Fragment {
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
    private ImageButton btnInstagram, btnTiktok, btnYoutube, btnSpotify, btnSoundCloud, btnAñadirCancion;
    private Button btnCerrarSesion, btnChat, tvEditar;
    private String uid;
    private FirebaseAuth mAuth;
    private ImageView imgFotoPerfil;


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

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        imgFotoPerfil = view.findViewById(R.id.imgFotoPerfil);

        lblDescripcion = view.findViewById(R.id.tvDescripcion);
        lblUsername = view.findViewById(R.id.tvNombre);
        lblEmail = view.findViewById(R.id.tvCiudad);

        inicializarUsuario();

        recyclerCanciones = view.findViewById(R.id.recyclerCanciones);
        recyclerCanciones.setHasFixedSize(true);

        recyclerCanciones.setLayoutManager(new LinearLayoutManager(getContext()));

        ArrayList<String> arrayPrueba = new ArrayList<>();

        adaptadorCanciones = new AdaptadorCancionesRecycler(arrayPrueba);
        recyclerCanciones.setAdapter(adaptadorCanciones);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        storageRef = FirebaseStorage.getInstance().getReference();

        btnChat = view.findViewById(R.id.btnChat);
        btnInstagram = view.findViewById(R.id.btnInstagram);
        btnYoutube = view.findViewById(R.id.btnYoutube);
        btnTiktok = view.findViewById(R.id.btnTikTok);
        btnSpotify = view.findViewById(R.id.btnSpotify);
        btnSoundCloud = view.findViewById(R.id.btnSoundCloud);
        btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);

        btnCerrarSesion.setOnClickListener(v -> {
            mAuth.signOut();
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);
            try {
                mGoogleSignInClient.signOut();
            } catch (Exception e) {

            }
            Intent intent = new Intent(getContext(), Login.class);
            startActivity(intent);

        });


        btnAñadirCancion = view.findViewById(R.id.btnSubirAudio);
        btnAñadirCancion = view.findViewById(R.id.btnSubirAudio);
        btnAñadirCancion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarAudio();
            }
        });


        tvEditar = view.findViewById(R.id.tvEditar);

        if (!uid.equals(mAuth.getCurrentUser().

                getUid())) {
            btnAñadirCancion.setVisibility(View.GONE);
            tvEditar.setVisibility(View.GONE);
            btnCerrarSesion.setVisibility(View.GONE);
            btnChat.setVisibility(View.VISIBLE);
        }

        tvEditar.setOnClickListener(v ->

        {
            Intent intent = new Intent(getContext(), EditarPerfil.class);
            db.collection(COLECCION).document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        usuario = documentSnapshot.toObject(Usuario.class);
                        intent.putExtra("UsuarioAEditar", usuario);
                        intent.putExtra("UidUsuario", mAuth.getCurrentUser().getUid());
                        startActivity(intent);
                    } else {
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        });


        btnChat.setOnClickListener(v ->

        {
            Intent intent = new Intent(getContext(), ChatVentana.class);
            intent.putExtra("UsuarioActual", mAuth.getCurrentUser().getUid());
            intent.putExtra("UidUsuarioReceptor", uid);
            startActivity(intent);
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

    private void inicializarUsuario() {
        db.collection(COLECCION).document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    usuario = documentSnapshot.toObject(Usuario.class);
                    obtenerDatosUsuario();
                    setRedesSociales();
                } else {
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
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


                        lblUsername.setText(usuario.getNombre());
                        lblDescripcion.setText(usuario.getDescripcion());
                        lblEmail.setText(usuario.getEmail());

                        adaptadorCanciones = new AdaptadorCancionesRecycler(canciones);
                        recyclerCanciones.setAdapter(adaptadorCanciones);

                        //Establecer foto de perfil
                        if (!usuario.getFotoPerfil().equals("")) {
                            try {
                                Glide.with(getContext()).load(usuario.getFotoPerfil()).into(imgFotoPerfil);
                            } catch (Exception e) {
                            }
                        }
                    }
                }
                dialogoCargando.dismiss();
            }
        });

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

                //Carpeta de archivos del storage
                StorageReference storagePath = storageRef.child("audios").child(uri.getLastPathSegment());

                storagePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getContext(), "Subido correctamente", Toast.LENGTH_SHORT).show();
                        Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                        firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();
                                //Aquí se añaden los audios al array de canciones del usuario en firebase
                                db.collection(COLECCION).document(usuario.getId()).update("arrayCanciones", FieldValue.arrayUnion(url)).addOnSuccessListener(documentReference -> {
                                    Toast.makeText(getContext(), "Subido correctamente", Toast.LENGTH_SHORT).show();
                                    dialogoCargando.dismiss();
                                    obtenerDatosUsuario();
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialogoCargando.dismiss();
                        Toast.makeText(getContext(), "No se ha podido subir la canción", Toast.LENGTH_SHORT).show();
                    }
                });
                ;
            }
        }
    }


}

