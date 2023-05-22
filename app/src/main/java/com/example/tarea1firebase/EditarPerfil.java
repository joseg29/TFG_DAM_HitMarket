package com.example.tarea1firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tarea1firebase.SpinnerMultiGeneros.AdapatadorSpinnerMultiGeneros;
import com.example.tarea1firebase.SpinnerMultiGeneros.ControladorSpinnerMultiGeneros;
import com.example.tarea1firebase.adaptadores.CustomSpinnerAdapter;
import com.example.tarea1firebase.entidades.Usuario;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditarPerfil extends AppCompatActivity {
    private TextView lblNombre, lblDescripcion, lblInstagram, lblYoutube, lblSpotify, lblTikTok, lblSoundCloud, tvAyuda;
    private EditText etNombre, etDescripcion, etEmailEditar, etInstagram, etYoutube, etSpotify, etTikTok, etSoundCloud;
    private String uid;
    private Usuario usuarioEditando;
    private FirebaseFirestore db;
    public final static String COLECCION = "Usuarios";
    private LinearLayout layoutRedesEditable;
    private ImageButton btnMostrarRedes;
    private Button btnGuardarCambios, btnCancelarCambios, btnCerrarSesion;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageButton btnCambiarFotoPerfil, btnMostrarTextoAyuda;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private String urlImagenPerfil;
    private FirebaseAuth mAuth;
    private Spinner spinnerCiudad, spinnerGenero;
    private List<String> selectedGeneros;
    private List<ControladorSpinnerMultiGeneros> listVOs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        etNombre = findViewById(R.id.etNombreEditar);
        etDescripcion = findViewById(R.id.etDescripcionEditar);
        etEmailEditar = findViewById(R.id.etEmailEditar);
        spinnerCiudad = findViewById(R.id.spinnerOpcionesEditarCiudades);
        spinnerGenero = findViewById(R.id.spinnerOpcionesGeneroMusical);
        etInstagram = findViewById(R.id.etInstagramEditar);
        etSoundCloud = findViewById(R.id.etSoundCloudEditar);
        etSpotify = findViewById(R.id.etSpotifyEditar);
        etYoutube = findViewById(R.id.etYoutubeEditar);
        etTikTok = findViewById(R.id.etTiktokEditar);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);


        btnGuardarCambios = findViewById(R.id.btnGuardarEditarPerfil);
        btnCancelarCambios = findViewById(R.id.btnCancelarEditarPerfil);
        btnMostrarTextoAyuda = findViewById(R.id.btnMostrarAyuda);
        tvAyuda = findViewById(R.id.tvAyuda);

        uid = getIntent().getStringExtra("UidUsuario");
        usuarioEditando = (Usuario) getIntent().getSerializableExtra("UsuarioAEditar");

        obtenerDatosUsuario(uid);

        btnMostrarRedes = findViewById(R.id.btnMostrarRedesEditables);
        layoutRedesEditable = findViewById(R.id.layoutRedesEditables);

        btnMostrarTextoAyuda.setOnClickListener(v -> {
            if (tvAyuda.getVisibility() == View.INVISIBLE) {
                tvAyuda.setVisibility(View.VISIBLE);
            } else {
                tvAyuda.setVisibility(View.INVISIBLE);
            }
        });

        btnMostrarRedes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutRedesEditable.getVisibility() == View.VISIBLE) {
                    layoutRedesEditable.setVisibility(View.GONE);
                    btnMostrarRedes.setImageResource(R.drawable.flecha_abajo);
                } else {
                    layoutRedesEditable.setVisibility(View.VISIBLE);
                    btnMostrarRedes.setImageResource(R.drawable.flecha_arriba);
                }
            }
        });

        btnCancelarCambios.setOnClickListener(v -> {
            Toast.makeText(EditarPerfil.this, "Cambios descartados.", Toast.LENGTH_LONG);
            Intent intent = new Intent(EditarPerfil.this, MarcoMenu.class);
            intent.putExtra("abrir_perfil", true);
            startActivity(intent);
            finish();
        });

        btnGuardarCambios.setOnClickListener(v -> {
            usuarioEditando.setNombre(etNombre.getText().toString());
            usuarioEditando.setDescripcion(etDescripcion.getText().toString());
            usuarioEditando.setInstagram(etInstagram.getText().toString());
            usuarioEditando.setSpotify(etSpotify.getText().toString());
            usuarioEditando.setYoutube(etYoutube.getText().toString());
            usuarioEditando.setSoundCloud(etSoundCloud.getText().toString());
            usuarioEditando.setTiktTok(etTikTok.getText().toString());
            usuarioEditando.setEmail(etEmailEditar.getText().toString());
            usuarioEditando.setCiudad(spinnerCiudad.getSelectedItem().toString());
            usuarioEditando.setListaGeneros(selectedGeneros);

            if (urlImagenPerfil != null) {
                usuarioEditando.setFotoPerfil(urlImagenPerfil);
            }

            CollectionReference refUsuarios = FirebaseFirestore.getInstance().

                    collection(COLECCION);

            refUsuarios.document(uid).set(usuarioEditando)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(EditarPerfil.this, "Cambios guardados.", Toast.LENGTH_LONG);
                            Intent intent = new Intent(EditarPerfil.this, MarcoMenu.class);
                            intent.putExtra("abrir_perfil", true);
                            startActivity(intent);
                            finish();
                        }
                    });
        });
        btnCambiarFotoPerfil = findViewById(R.id.btnCambiarFotoPerfil);
        btnCambiarFotoPerfil.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE_REQUEST);
        });
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

        btnCerrarSesion.setOnClickListener(v -> {
            mAuth.signOut();
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            try {
                mGoogleSignInClient.signOut();
            } catch (Exception e) {

            }
            Intent intent = new Intent(EditarPerfil.this, Login.class);
            startActivity(intent);
            finish();
        });
        String[] ciudad = getResources().getStringArray(R.array.autonomous_communities);
        CustomSpinnerAdapter adapterCiudad = new CustomSpinnerAdapter(this, ciudad);
        spinnerCiudad.setAdapter(adapterCiudad);


        final String[] select_qualification = {"Seleccione Generos", "#Clasica", "#Country", "#Electro", "#Flamenco", "#Folk", "#Jazz", "#Kpop", "#Metal", "#Pop", "#Rap", "#Rock", "#Trap", "#Drill"};
        Spinner spinner = findViewById(R.id.spinnerOpcionesGeneroMusical);

       listVOs = new ArrayList<>();

        for (int i = 0; i < select_qualification.length; i++) {
            ControladorSpinnerMultiGeneros stateVO = new ControladorSpinnerMultiGeneros();
            stateVO.setTitle(select_qualification[i]);
            stateVO.setSelected(false);
            listVOs.add(stateVO);
        }

        selectedGeneros = new ArrayList<>();

        AdapatadorSpinnerMultiGeneros myAdapter = new AdapatadorSpinnerMultiGeneros(EditarPerfil.this, 0, listVOs, selectedGeneros);
        spinner.setAdapter(myAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ControladorSpinnerMultiGeneros selectedState = (ControladorSpinnerMultiGeneros) parent.getSelectedItem();
                String selectedGenre = selectedState.getTitle();

                if (!selectedGenre.equals("Seleccione Generos")) {
                    if (selectedState.isSelected()) {
                        selectedGeneros.add(selectedGenre);
                    } else {
                        selectedGeneros.remove(selectedGenre);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


    }


    private void obtenerDatosUsuario(String uid) {

        CollectionReference refUsuarios = FirebaseFirestore.getInstance().collection(COLECCION);


        refUsuarios.document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            //Obtenemos todas las urls del array de canciones del usuario en firebase, y asignamos cada una a un mediaplayer y seekbar distinto.
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //Obtenemos el usuario de la base de datos con todos sus campos
                        Usuario usuario = document.toObject(Usuario.class);

                        etNombre.setText(usuario.getNombre());
                        etDescripcion.setText(usuario.getDescripcion());
                        etEmailEditar.setText(usuario.getEmail());
                        etSpotify.setText(usuario.getSpotify());
                        etInstagram.setText(usuario.getInstagram());
                        etYoutube.setText(usuario.getYoutube());
                        etSoundCloud.setText(usuario.getSoundCloud());
                        etTikTok.setText(usuario.getTiktTok());

                        ArrayAdapter<String> adapterCiudad = (ArrayAdapter<String>) spinnerCiudad.getAdapter();
                        spinnerCiudad.setSelection(adapterCiudad.getPosition(usuario.getCiudad()));

                        ArrayAdapter<String> adapterGenero = (ArrayAdapter<String>) spinnerGenero.getAdapter();
                        spinnerGenero.setSelection(adapterGenero.getPosition(usuario.getListaGeneros().toString()));


                    } else {
                        Toast.makeText(getApplicationContext(), "Error al obtener datos", Toast.LENGTH_LONG);
                    }
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                btnCambiarFotoPerfil.setImageBitmap(bitmap);
                cargarArchivo();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void cargarArchivo() {
        if (mImageUri != null) {
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));
            fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(EditarPerfil.this, "Imagen subida correctamente", Toast.LENGTH_SHORT).show();
                            Task<Uri> uriImagenPerfil = taskSnapshot.getStorage().getDownloadUrl();
                            uriImagenPerfil.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    urlImagenPerfil = uri.toString();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditarPerfil.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No se ha seleccionado ninguna imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

}