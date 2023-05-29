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

import com.bumptech.glide.Glide;
import com.example.tarea1firebase.SpinnerMultiGeneros.AdapatadorSpinnerMultiGeneros;
import com.example.tarea1firebase.SpinnerMultiGeneros.ControladorSpinnerMultiGeneros;
import com.example.tarea1firebase.adaptadores.CustomSpinnerAdapter;
import com.example.tarea1firebase.entidades.Usuario;
import com.example.tarea1firebase.gestor.GestorFirestore;
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

import javax.security.auth.callback.Callback;

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
        /*
         * Obtener instancias de FirebaseFirestore y FirebaseAuth
         * */
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        /*
         * Inicializar elementos de la interfaz de usuario
         * */
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
        /*
         * Obtener el ID de usuario y el objeto Usuario a editar
         * */
        uid = getIntent().getStringExtra("UidUsuario");
        usuarioEditando = (Usuario) getIntent().getSerializableExtra("UsuarioAEditar");
        /*
         * Obtener los datos del usuario desde Firestore y cargarlos en los campos correspondientes
         * */
        obtenerDatosUsuario(uid);

        btnMostrarRedes = findViewById(R.id.btnMostrarRedesEditables);
        layoutRedesEditable = findViewById(R.id.layoutRedesEditables);
        /*
         * Configurar el listener para mostrar/ocultar el texto de ayuda
         * */
        btnMostrarTextoAyuda.setOnClickListener(v -> {
            if (tvAyuda.getVisibility() == View.INVISIBLE) {
                tvAyuda.setVisibility(View.VISIBLE);
            } else {
                tvAyuda.setVisibility(View.INVISIBLE);
            }
        });
        /*
         * Configurar el listener para mostrar/ocultar las redes sociales editables
         * */
        btnMostrarRedes.setOnClickListener(new View.OnClickListener() {
            /**
             * Método invocado cuando se produce un evento de clic en la vista especificada.
             * Cambia la visibilidad del layout de redes editables y la imagen del botón btnMostrarRedes
             * según su estado actual. Si el layout es visible, se oculta y se muestra una flecha hacia abajo;
             * si el layout es invisible, se muestra y se muestra una flecha hacia arriba.
             *
             * @param view la vista en la que se ha producido el evento de clic.
             */
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
        /*
         * Configurar el listener para cancelar los cambios y volver al perfil sin guardarlos
         * */
        btnCancelarCambios.setOnClickListener(v -> {
            Toast.makeText(EditarPerfil.this, "Cambios descartados.", Toast.LENGTH_LONG);
            Intent intent = new Intent(EditarPerfil.this, MarcoMenu.class);
            intent.putExtra("abrir_perfil", true);
            startActivity(intent);
            finish();
        });
        /*
         * Configurar el listener para guardar los cambios en Firestore
         * */
        btnGuardarCambios.setOnClickListener(v -> {

            /*
             * Actualizar los datos del objeto Usuario con los valores de los campos de entrada
             * */
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
            /*
             * Si hay una URL de imagen de perfil, establecerla en el objeto Usuario
             * */

            /*
             * Obtener una referencia a la colección "usuarios" en Firestore
             * */
            CollectionReference refUsuarios = FirebaseFirestore.getInstance().

                    collection(COLECCION);
            /*
             * Guardar los cambios en Firestore
             * */

            /**
             * Revisa si hemos modificado la imagen
             */
            if (mImageUri != null) {
                cargarArchivo(mImageUri, result -> {
                    refUsuarios.document(uid).set(usuarioEditando)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    cerrar();
                                }
                            });
                });
            } else {
                cerrar();
            }
        });
        btnCambiarFotoPerfil = findViewById(R.id.btnCambiarFotoPerfil);
        Glide.with(this).load(usuarioEditando.getFotoPerfil()).into(btnCambiarFotoPerfil);
        /*
         * Listener para cambiar la foto de perfil
         * */
        btnCambiarFotoPerfil.setOnClickListener(v -> {
            /*
             * Crear un intent para seleccionar una imagen de la galería
             * */
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            /*
             * Iniciar la actividad para seleccionar una imagen y recibir el resultado en onActivityResult
             * */
            startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE_REQUEST);
        });
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

        btnCerrarSesion.setOnClickListener(v -> {
            /*
             * Cerrar sesión en Firebase
             * */
            mAuth.signOut();
            /*
             * Configurar opciones de inicio de sesión de Google
             * */
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
            /*
             * Crear cliente de inicio de sesión de Google
             * */
            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            try {
                /*
                 * Cerrar sesión de Google
                 * */
                mGoogleSignInClient.signOut();
            } catch (Exception e) {
                /*
                 * Manejar cualquier excepción que pueda ocurrir al cerrar sesión de Google
                 * */
            }
            /*
             *  Redirigir a la actividad de inicio de sesión
             * */
            Intent intent = new Intent(EditarPerfil.this, Login.class);
            startActivity(intent);
            finish();
        });
        /*
         * Obtener la lista de ciudades de los recursos y crear un adaptador
         * personalizado para el spinner
         * */
        String[] ciudad = getResources().getStringArray(R.array.autonomous_communities);
        CustomSpinnerAdapter adapterCiudad = new CustomSpinnerAdapter(this, ciudad);
        spinnerCiudad.setAdapter(adapterCiudad);

        /*
         * Crear una lista de objetos para representar las opciones de género musical
         * */
        final String[] select_qualification = {"Seleccione Generos", "#Clasica", "#Country", "#Electro", "#Flamenco", "#Folk", "#Jazz", "#Kpop", "#Metal", "#Pop", "#Rap", "#Rock", "#Trap", "#Drill"};
        Spinner spinner = findViewById(R.id.spinnerOpcionesGeneroMusical);

        listVOs = new ArrayList<>();
        /*
         * Iterar sobre las opciones de género musical y agregarlas a la lista de objetos
         * */
        for (int i = 0; i < select_qualification.length; i++) {
            ControladorSpinnerMultiGeneros stateVO = new ControladorSpinnerMultiGeneros();
            stateVO.setTitle(select_qualification[i]);
            stateVO.setSelected(false);
            listVOs.add(stateVO);
        }

        selectedGeneros = new ArrayList<>();
        /*
         * Crear un adaptador personalizado para el spinner de género musical
         * */
        AdapatadorSpinnerMultiGeneros myAdapter = new AdapatadorSpinnerMultiGeneros(EditarPerfil.this, 0, listVOs, selectedGeneros);
        spinner.setAdapter(myAdapter);
        /*
         * Configurar el listener de selección de items para el spinner de género musical
         * */
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * Maneja el evento de selección de un elemento en el AdapterView.
             *
             * @param parent El AdapterView donde se seleccionó el elemento.
             * @param view La vista del elemento seleccionado.
             * @param position La posición del elemento seleccionado en el AdapterView.
             * @param id El ID del elemento seleccionado.
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /*
                 * Obtener el objeto ControladorSpinnerMultiGeneros seleccionado
                 * */
                ControladorSpinnerMultiGeneros selectedState = (ControladorSpinnerMultiGeneros) parent.getSelectedItem();
                String selectedGenre = selectedState.getTitle();
                /*
                 * Verificar si se seleccionó una opción distinta a "Seleccione Generos"
                 * */
                if (!selectedGenre.equals("Seleccione Generos")) {
                    if (selectedState.isSelected()) {
                        /*
                         * Agregar el género a la lista de géneros seleccionados
                         * */
                        selectedGeneros.add(selectedGenre);
                    } else {
                        /*
                         * Eliminar el género de la lista de géneros seleccionados
                         * */
                        selectedGeneros.remove(selectedGenre);
                    }
                }
            }

            /**
             * Maneja el evento cuando no se selecciona ningún elemento en el AdapterView.
             *
             * @param parent El AdapterView donde no se seleccionó ningún elemento.
             */
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                /*
                 * No hacer nada cuando no se selecciona ningún item
                 * */
            }
        });


    }

    /**
     * Método invocado cuando la operación de guardado en Firestore se completa exitosamente.
     * Muestra un mensaje de "Cambios guardados" a través de un Toast, crea un intent para abrir
     * la actividad MarcoMenu con un extra "abrir_perfil" establecido en true, inicia la actividad
     * MarcoMenu y finaliza la actividad actual.
     */
    private void cerrar() {
        Toast.makeText(EditarPerfil.this, "Cambios guardados.", Toast.LENGTH_LONG);
        Intent intent = new Intent(EditarPerfil.this, MarcoMenu.class);
        intent.putExtra("abrir_perfil", true);
        startActivity(intent);
        finish();
    }

    /**
     * Obtiene los datos de un usuario a partir de su identificador único (uid).
     *
     * @param uid El identificador único del usuario.
     */
    private void obtenerDatosUsuario(String uid) {

        CollectionReference refUsuarios = FirebaseFirestore.getInstance().collection(COLECCION);


        refUsuarios.document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            /**
             * Escucha el evento de completitud de la tarea para obtener los datos del usuario desde Firestore.
             *
             * @param task La tarea completada que contiene los datos del usuario.
             */
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        /*
                         * Obtener el usuario de la base de datos con todos sus campos
                         * */
                        Usuario usuario = document.toObject(Usuario.class);
                        /*
                         * Actualizar los campos de la interfaz de usuario con los datos del usuario
                         * */
                        etNombre.setText(usuario.getNombre());
                        etDescripcion.setText(usuario.getDescripcion());
                        etEmailEditar.setText(usuario.getEmail());
                        etSpotify.setText(usuario.getSpotify());
                        etInstagram.setText(usuario.getInstagram());
                        etYoutube.setText(usuario.getYoutube());
                        etSoundCloud.setText(usuario.getSoundCloud());
                        etTikTok.setText(usuario.getTiktTok());
                        /*
                         * Obtener el adaptador del spinner de ciudad y seleccionar la ciudad del usuario
                         * */
                        ArrayAdapter<String> adapterCiudad = (ArrayAdapter<String>) spinnerCiudad.getAdapter();
                        spinnerCiudad.setSelection(adapterCiudad.getPosition(usuario.getCiudad()));
                        /*
                         * Obtener el adaptador del spinner de género y seleccionar los géneros del usuario
                         * */
                        ArrayAdapter<String> adapterGenero = (ArrayAdapter<String>) spinnerGenero.getAdapter();
                        spinnerGenero.setSelection(adapterGenero.getPosition(usuario.getListaGeneros().toString()));
                        Glide.with(EditarPerfil.this).load(usuarioEditando.getFotoPerfil()).into(btnCambiarFotoPerfil);
                    } else {
                        Toast.makeText(getApplicationContext(), "Error al obtener datos", Toast.LENGTH_LONG);
                    }
                }
            }
        });

    }

    /**
     * Metodo Callback que invoca cuando el resultado de una actividad es recibida.
     *
     * @param requestCode El código de solicitud pasado a startActivityForResult().
     * @param resultCode  El código de resultado devuelto por la actividad secundaria.
     * @param data        Un Intent que lleva los datos de resultado.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            if (mImageUri != null) {
                Glide.with(this).load(mImageUri).into(btnCambiarFotoPerfil);
            }

        }
    }

    /**
     * Carga el archivo seleccionado en el almacenamiento y obtiene la URL de descarga de la imagen.
     * Si no se ha seleccionado ninguna imagen, se muestra un mensaje de error.
     * @param uri uri de archivo a subir a storage
     * @param callback callback que avisa cuando se ha subido la imagen correctamente
     */
    private void cargarArchivo(Uri uri, GestorFirestore.Callback callback) {
        StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                + "." + getFileExtension(uri));

        fileReference.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    /**
                     * Método de devolución de llamada invocado cuando la carga de la imagen es exitosa.
                     *
                     * @param taskSnapshot El objeto TaskSnapshot que contiene información sobre la imagen cargada.
                     */
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                usuarioEditando.setFotoPerfil(uri.toString());
                                urlImagenPerfil = uri.toString();
                                callback.onSuccess("");
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    /**
                     * Metodo Callback que se invoca cuando la carga de la imagen falla
                     *
                     * @param e The exception indicating the reason for the failure.
                     */
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditarPerfil.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /**
     * Obtiene la extensión del archivo a partir de la URI especificada.
     *
     * @param uri la URI del archivo del que se desea obtener la extensión.
     * @return la extensión del archivo correspondiente a la URI especificada.
     */
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

}