package com.example.tarea1firebase;

import static com.example.tarea1firebase.Registro.COLECCION;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class InicioApp extends AppCompatActivity {
    private FirebaseFirestore db;
    private Usuario user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio_app);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Se crea un objeto Handler y se usa su método postDelayed para agregar una pausa de 3 segundos
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Código que se ejecutará después de 3 segundos
                iniciarSesion();
            }
        }, 3000);
    }


    public void iniciarSesion() {
        //Se revisa si hay alguna sesión abierta (currentUser / usuarioActual).

        //Si hay alguna sesión iniciada, se envía directamente a la ventana siguiente.
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String idDocumento = FirebaseAuth.getInstance().getCurrentUser().getUid();

            //Se hace un get de firestore a partir del uid del currentUser. Su id se pasa como referencia de documento, para obtener el objeto Usuario y pasarlo a la siguiente activity.

            db.collection(COLECCION).document(idDocumento).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            user = document.toObject(Usuario.class);
                            Toast.makeText(InicioApp.this, "Sesión iniciada", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(InicioApp.this, PerfilUsuario.class);
                            intent.putExtra("USUARIO", user);
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            });
        }
        //Si no hay ninguna sesión iniciada, se envía a la ventana de Login.
        else {
            Intent intent = new Intent(InicioApp.this, Login.class);
            startActivity(intent);
            finish();
        }
    }
}