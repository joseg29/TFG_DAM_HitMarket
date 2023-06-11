package com.example.tarea1firebase;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tarea1firebase.entidades.Publicacion;
import com.example.tarea1firebase.fragments.MuroFragment;
import com.example.tarea1firebase.gestor.GestorFirestore;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

/**
 * Actividad para crear una nueva publicación.
 */
public class ActivityNuevaPublicacion extends AppCompatActivity {
    private ImageButton btnImagen;
    private EditText etTexto;
    private Button btnSubir, btnCancelar;
    private Uri imageUri;
    private GestorFirestore gestorFirestore;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private String usuarioActualUid;

    /**
     * Método que se ejecuta al crear la actividad.
     *
     * @param savedInstanceState El estado anterior de la actividad, si está disponible.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_publicacion);
        /*
         *  Inicializa el gestor de Firestore
         * */
        gestorFirestore = new GestorFirestore();
        /*
         *  Obtiene la instancia actual de FirebaseAuth
         * */
        mAuth = FirebaseAuth.getInstance();
        /*
         * Obtiene la referencia de Firebase Storage con la carpeta "uploads"
         * */
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        /*
         * Obtiene el UID del usuario actualmente autenticado
         * */
        usuarioActualUid = mAuth.getCurrentUser().getUid();
        btnSubir = findViewById(R.id.btnSubirPublicacion);
        btnCancelar = findViewById(R.id.btnCancelarPublicacion);
        btnImagen = findViewById(R.id.btnSeleccionarImagenPublicacion);
        etTexto = findViewById(R.id.etTextoPublicacion);
        /*
         *  Listener para seleccionar una imagen de la galería
         * */
        btnImagen.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(
                    Intent.createChooser(intent, "Seleccione una imagen"),
                    200);
        });
        /*
         *  Listener para subir la publicación
         * */
        btnSubir.setOnClickListener(v -> {
            /*
             * Obtiene la fecha y hora actual en el formato especificado
             * */
            String fechaYHora = new SimpleDateFormat("MM/dd/HH:mm").format(Calendar.getInstance().getTime());

            //Si la publicación es con imagen
            if (imageUri != null) {
                /*
                 * Crea una referencia al archivo en Firebase Storage con un nombre único
                 * */
                StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                        + "." + getFileExtension(imageUri));
                fileReference.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            /**
                             * Método que se ejecuta cuando la tarea de carga de la imagen es exitosa.
                             *
                             * @param taskSnapshot El resultado de la tarea de carga de la imagen.
                             */
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                /*
                                 * Obtiene la URL de descarga de la imagen subida
                                 * */
                                Task<Uri> uriImagenPerfil = taskSnapshot.getStorage().getDownloadUrl();
                                uriImagenPerfil.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    /**
                                     * Método que se ejecuta cuando se obtiene exitosamente la URL de descarga de la imagen.
                                     *
                                     * @param uri La URL de descarga de la imagen subida.
                                     */
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        /*
                                         * Crea una nueva instancia de Publicacion con los datos proporcionados
                                         * */
                                        Publicacion publicacion = new Publicacion(usuarioActualUid, etTexto.getText().toString(), fechaYHora, uri.toString());
                                        /*
                                         * Agrega la publicación al array "listaPublicaciones" del usuario en Firestore
                                         * */
                                        gestorFirestore.anadirValorArray(usuarioActualUid, "listaPublicaciones", publicacion, new GestorFirestore.Callback<String>() {
                                            /**
                                             * Método de callback invocado cuando se completa con éxito la operación.
                                             *
                                             * @param result El resultado de la operación.
                                             */
                                            @Override
                                            public void onSuccess(String result) {
                                                /*
                                                 * Muestra un mensaje de "Publicado"
                                                 * */
                                                Toast.makeText(ActivityNuevaPublicacion.this, "Publicado", Toast.LENGTH_SHORT).show();
                                                setResult(1);
                                                /*
                                                 * Finaliza la actividad
                                                 * */
                                                finish();
                                            }
                                        });
                                    }
                                });
                            }
                        });
            }
            //Si la publicación es solo texto
            else {
                /*
                 * Crea una nueva instancia de la clase Publicacion con los datos proporcionados,
                 * indicando una URL de imagen vacía.
                 */
                Publicacion publicacion = null;
                publicacion = new Publicacion(usuarioActualUid, etTexto.getText().toString(), fechaYHora, "");
                /*
                 * Agrega la publicación al array "listaPublicaciones" del usuario en Firestore
                 * utilizando el método anadirValorArray() del gestorFirestore.
                 * Se proporciona un nuevo objeto de tipo GestorFirestore.Callback<String>() como argumento,
                 * que implementa el método onSuccess() del callback.
                 * */
                gestorFirestore.anadirValorArray(usuarioActualUid, "listaPublicaciones", publicacion, new GestorFirestore.Callback<String>() {
                    /**
                     * Método de callback que se ejecuta cuando se completa exitosamente la operación en Firestore.
                     * Muestra un mensaje de "Publicado" utilizando un Toast y finaliza la actividad actual.
                     *
                     * @param result El resultado de la operación en Firestore (en este caso, no se utiliza).
                     */
                    @Override
                    public void onSuccess(String result) {
                        Toast.makeText(ActivityNuevaPublicacion.this, "Publicado", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
            /**
             * Decimos que se ha subido algo
             */
            setResult(1);
        });
        /*
         * Establece un OnClickListener para el botón btnCancelar.
         * */
        btnCancelar.setOnClickListener(v -> {
            /**
             * Decimos que no se ha subido nada
             */
            setResult(0);
            /*
             * Finaliza la actividad
             * */
            finish();
        });
    }

    /**
     * Método que se ejecuta cuando se obtiene un resultado de otra actividad.
     *
     * @param requestCode El código de solicitud enviado a la actividad.
     * @param resultCode  El código de resultado devuelto por la actividad.
     * @param data        Los datos resultantes de la actividad.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 200) {
            /*
             * Obtiene la URI de la imagen seleccionada.
             * */
            imageUri = data.getData();
            /*
             * Establece la imagen seleccionada en el botón de imagen.
             * */
            btnImagen.setImageURI(imageUri);
            /*
             * Ajusta la escala de la imagen en el botón de imagen para que se ajuste al tamaño del botón.
             * */
            btnImagen.setScaleType(ImageView.ScaleType.FIT_XY);
        }
    }

    /**
     * Obtiene la extensión del archivo a partir de su URI.
     *
     * @param uri La URI del archivo.
     * @return La extensión del archivo.
     */
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}