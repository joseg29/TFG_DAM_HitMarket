package com.example.tarea1firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditarPerfil extends AppCompatActivity {
    private TextView lblNombre, lblDescripcion, lblInstagram, lblYoutube, lblSpotify, lblTikTok, lblSoundCloud;
    private EditText etNombre, etDescripcion, etInstagram, etYoutube, etSpotify, etTikTok, etSoundCloud;
    private String uid;
    private Usuario usuarioEditando;
    private FirebaseFirestore db;
    public final static String COLECCION = "Usuarios";
    private LinearLayout layoutRedesEditable;
    private ImageButton btnMostrarRedes;
    private Button btnGuardarCambios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        db = FirebaseFirestore.getInstance();

        etNombre = findViewById(R.id.etNombreEditar);
        etDescripcion = findViewById(R.id.etDescripcionEditar);
        etInstagram = findViewById(R.id.etInstagramEditar);
        etSoundCloud = findViewById(R.id.etSoundCloudEditar);
        etSpotify = findViewById(R.id.etSpotifyEditar);
        etYoutube = findViewById(R.id.etYoutubeEditar);
        etTikTok = findViewById(R.id.etTiktokEditar);


        btnGuardarCambios = findViewById(R.id.btnGuardarEditarPerfil);

        uid = getIntent().getStringExtra("UidUsuario");
        usuarioEditando = (Usuario) getIntent().getSerializableExtra("UsuarioAEditar");

        obtenerDatosUsuario(uid);

        btnMostrarRedes = findViewById(R.id.btnMostrarRedesEditables);
        layoutRedesEditable = findViewById(R.id.layoutRedesEditables);

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

        btnGuardarCambios.setOnClickListener(v -> {
            usuarioEditando.setNombre(etNombre.getText().toString());
            usuarioEditando.setDescripcion(etDescripcion.getText().toString());
            usuarioEditando.setInstagram(etInstagram.getText().toString());
            usuarioEditando.setSpotify(etSpotify.getText().toString());
            usuarioEditando.setYoutube(etYoutube.getText().toString());
            usuarioEditando.setSoundCloud(etSoundCloud.getText().toString());
            usuarioEditando.setTiktTok(etTikTok.getText().toString());

            CollectionReference refUsuarios = FirebaseFirestore.getInstance().

                    collection(COLECCION);

            refUsuarios.document(uid).set(usuarioEditando)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(EditarPerfil.this, "Cambios guardados.", Toast.LENGTH_LONG);
                            Intent intent = new Intent(EditarPerfil.this, PerfilUsuario.class);
                            intent.putExtra("UidUsuario", uid);
                            startActivity(intent);
                            finish();
                        }
                    });
        });
    }


    private void obtenerDatosUsuario(String uid) {

        CollectionReference refUsuarios = FirebaseFirestore.getInstance().

                collection(COLECCION);


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
                        etSpotify.setText(usuario.getSpotify());
                        etInstagram.setText(usuario.getInstagram());
                        etYoutube.setText(usuario.getYoutube());
                        etSoundCloud.setText(usuario.getSoundCloud());
                        etTikTok.setText(usuario.getTiktTok());

                    } else {
                        Toast.makeText(getApplicationContext(), "Error al obtener datos", Toast.LENGTH_LONG);
                    }
                }
            }
        });

    }

}