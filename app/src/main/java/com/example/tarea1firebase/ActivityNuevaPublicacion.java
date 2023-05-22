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

public class ActivityNuevaPublicacion extends AppCompatActivity {
    private ImageButton btnImagen;
    private EditText etTexto;
    private Button btnSubir, btnCancelar;
    private Uri imageUri;
    private GestorFirestore gestorFirestore;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private String usuarioActualUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_publicacion);

        gestorFirestore = new GestorFirestore();
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        usuarioActualUid = mAuth.getCurrentUser().getUid();

        btnSubir = findViewById(R.id.btnSubirPublicacion);
        btnCancelar = findViewById(R.id.btnCancelarPublicacion);
        btnImagen = findViewById(R.id.btnSeleccionarImagenPublicacion);
        etTexto = findViewById(R.id.etTextoPublicacion);

        btnImagen.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(
                    Intent.createChooser(intent, "Seleccione una imagen"),
                    200);
        });

        btnSubir.setOnClickListener(v -> {
            String fechaYHora = new SimpleDateFormat("yyyy/MM/dd/HH:mm").format(Calendar.getInstance().getTime());

            //Si la publicación es con imagen
            if (imageUri != null) {
                StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                        + "." + getFileExtension(imageUri));
                fileReference.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> uriImagenPerfil = taskSnapshot.getStorage().getDownloadUrl();
                                uriImagenPerfil.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Publicacion publicacion = new Publicacion(usuarioActualUid, etTexto.getText().toString(), fechaYHora, uri.toString());
                                        gestorFirestore.anadirValorArray(usuarioActualUid, "listaPublicaciones", publicacion, new GestorFirestore.Callback<String>() {
                                            @Override
                                            public void onSuccess(String result) {
                                                Toast.makeText(ActivityNuevaPublicacion.this, "Publicado", Toast.LENGTH_SHORT).show();
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
                Publicacion publicacion = null;
                publicacion = new Publicacion(usuarioActualUid, etTexto.getText().toString(), fechaYHora, "");
                gestorFirestore.anadirValorArray(usuarioActualUid, "listaPublicaciones", publicacion, new GestorFirestore.Callback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Toast.makeText(ActivityNuevaPublicacion.this, "Publicado", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });

        btnCancelar.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 200) {
            imageUri = data.getData();
            btnImagen.setImageURI(imageUri);
            btnImagen.setScaleType(ImageView.ScaleType.FIT_XY);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}